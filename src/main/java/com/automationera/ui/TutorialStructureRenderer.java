package com.automationera.ui;

import com.automationera.OutputRecipe.SelectionBox;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.render.BlockRenderLayers;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

/**
 * 把教程结构渲染到离屏纹理，再由 GUI 贴回界面（替代 1.21.5 及以前直接在 DrawContext 里画方块的做法）。
 */
public class TutorialStructureRenderer extends SpecialGuiElementRenderer<TutorialStructureRenderState> {
    private static final int FULL_BRIGHT = LightmapTextureManager.MAX_LIGHT_COORDINATE;
    // 正交投影深度范围只有 ±1000，大结构放大后会被裁掉；压缩视线方向的深度即可，不影响画面
    private static final float DEPTH_SQUASH = 0.25f;
    private static final SpriteIdentifier WATER = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/water_still"));
    private static final SpriteIdentifier LAVA = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/lava_still"));

    public TutorialStructureRenderer(VertexConsumerProvider.Immediate vertexConsumers) {
        super(vertexConsumers);
    }

    @Override
    public Class<TutorialStructureRenderState> getElementClass() {
        return TutorialStructureRenderState.class;
    }

    @Override
    protected float getYOffset(int height, int windowScaleFactor) {
        return 0; // 原点放在元素顶部中点，方便直接用 GUI 坐标定位
    }

    @Override
    protected String getName() {
        return "automationera tutorial structure";
    }

    @Override
    protected void render(TutorialStructureRenderState state, MatrixStack matrices) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ENTITY_IN_UI);
        TutorialManager.Structure structure = state.structure();

        matrices.push();
        matrices.scale(1f, 1f, DEPTH_SQUASH);
        matrices.translate(state.centerX() - (state.x2() - state.x1()) / 2f, state.centerY(), 0f);
        // 下面的 Z 翻转会让旋转方向反过来，这里取负号：仰角负数 = 俯视，向右拖 = 近处往右转（与 1.21.5 一致）
        matrices.multiply(new Quaternionf().rotateX((float) Math.toRadians(-state.pitch())));
        matrices.multiply(new Quaternionf().rotateY((float) Math.toRadians(-state.rotation())));
        float s = state.blockSize();
        // 手性：GUI 投影本身 Y 向下（一次镜像），PiP 底层又翻了一次 Z。
        // 这里必须再翻 Y 和 Z（总共奇数次翻转），世界才不是镜像的；与 1.21.5 的 scale(g,-g,g) 等价
        matrices.scale(s, -s, -s);
        matrices.translate(
                -(structure.minX() + structure.maxX() + 1) / 2f,
                -(structure.minY() + structure.maxY() + 1) / 2f,
                -(structure.minZ() + structure.maxZ() + 1) / 2f);

        BlockRenderManager brm = mc.getBlockRenderManager();
        // 先画不透明方块，再画半透明方块，最后流体和选区框，避免半透明排序问题
        renderBlocks(structure, matrices, brm, false);
        renderBlocks(structure, matrices, brm, true);
        renderFluids(mc, structure, matrices);
        for (SelectionBox box : state.boxes()) {
            renderBoxOutline(matrices, this.vertexConsumers, box, 1f, 1f, 0f, state.boxAlpha());
        }
        matrices.pop();
    }

    private void renderBlocks(TutorialManager.Structure structure, MatrixStack matrices, BlockRenderManager brm, boolean translucentPass) {
        for (TutorialManager.PlacedBlock b : structure.blocks()) {
            BlockState bs = b.state();
            if (bs.getRenderType() != BlockRenderType.MODEL) continue;
            if (BlockRenderLayers.getBlockLayer(bs).isTranslucent() != translucentPass) continue;
            matrices.push();
            matrices.translate(b.x(), b.y(), b.z());
            brm.renderBlockAsEntity(bs, matrices, this.vertexConsumers, FULL_BRIGHT, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
    }

    private void renderFluids(MinecraftClient mc, TutorialManager.Structure structure, MatrixStack matrices) {
        Sprite water = null, lava = null;
        for (TutorialManager.PlacedBlock b : structure.blocks()) {
            FluidState fs = b.state().getFluidState();
            if (fs.isEmpty()) continue;
            boolean isLava = fs.isIn(FluidTags.LAVA);
            Sprite sprite;
            if (isLava) {
                if (lava == null) lava = mc.getAtlasManager().getSprite(LAVA);
                sprite = lava;
            } else {
                if (water == null) water = mc.getAtlasManager().getSprite(WATER);
                sprite = water;
            }
            // 水源/含水方块满格；流动液体按等级算高度（下落的流体 level=8 也接近满格）
            float height = fs.isStill() ? 1.0f : Math.max(1, fs.getLevel()) / 9.0f;
            int color = isLava ? 0xFFFFFFFF : 0xE63F76E4;
            matrices.push();
            matrices.translate(b.x(), b.y(), b.z());
            renderFluidCube(matrices, this.vertexConsumers, sprite, height - 0.001f, color);
            matrices.pop();
        }
    }

    private static void renderFluidCube(MatrixStack matrices, VertexConsumerProvider consumers, Sprite sprite, float y2, int argb) {
        float u0 = sprite.getMinU(), u1 = sprite.getMaxU(), v0 = sprite.getMinV(), v1 = sprite.getMaxV();
        MatrixStack.Entry e = matrices.peek();
        VertexConsumer vc = consumers.getBuffer(RenderLayers.translucentMovingBlock());
        float y1 = 0f;
        // 上
        quad(vc, e, 0, y2, 1, 1, y2, 1, 1, y2, 0, 0, y2, 0, u0, v0, u1, v1, argb, 0, 1, 0);
        // 下
        quad(vc, e, 0, y1, 0, 1, y1, 0, 1, y1, 1, 0, y1, 1, u0, v0, u1, v1, argb, 0, -1, 0);
        // 北 (z=0)
        quad(vc, e, 0, y2, 0, 1, y2, 0, 1, y1, 0, 0, y1, 0, u0, v0, u1, v1, argb, 0, 0, -1);
        // 南 (z=1)
        quad(vc, e, 0, y1, 1, 1, y1, 1, 1, y2, 1, 0, y2, 1, u0, v0, u1, v1, argb, 0, 0, 1);
        // 西 (x=0)
        quad(vc, e, 0, y1, 0, 0, y1, 1, 0, y2, 1, 0, y2, 0, u0, v0, u1, v1, argb, -1, 0, 0);
        // 东 (x=1)
        quad(vc, e, 1, y2, 0, 1, y2, 1, 1, y1, 1, 1, y1, 0, u0, v0, u1, v1, argb, 1, 0, 0);
    }

    private static void quad(VertexConsumer vc, MatrixStack.Entry e,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float u0, float v0, float u1, float v1, int argb, float nx, float ny, float nz) {
        vc.vertex(e, x1, y1, z1).color(argb).texture(u0, v1).overlay(OverlayTexture.DEFAULT_UV).light(FULL_BRIGHT).normal(e, nx, ny, nz);
        vc.vertex(e, x2, y2, z2).color(argb).texture(u1, v1).overlay(OverlayTexture.DEFAULT_UV).light(FULL_BRIGHT).normal(e, nx, ny, nz);
        vc.vertex(e, x3, y3, z3).color(argb).texture(u1, v0).overlay(OverlayTexture.DEFAULT_UV).light(FULL_BRIGHT).normal(e, nx, ny, nz);
        vc.vertex(e, x4, y4, z4).color(argb).texture(u0, v0).overlay(OverlayTexture.DEFAULT_UV).light(FULL_BRIGHT).normal(e, nx, ny, nz);
    }

    /** 1.21.11 起线宽是顶点属性（没有 RenderSystem.lineWidth 了），法线必须是线段方向，否则线会消失 */
    private static void renderBoxOutline(MatrixStack matrices, VertexConsumerProvider consumers, SelectionBox box,
                                         float r, float g, float b, float a) {
        float minX = Math.min(box.x1(), box.x2()), minY = Math.min(box.y1(), box.y2()), minZ = Math.min(box.z1(), box.z2());
        float maxX = Math.max(box.x1(), box.x2()) + 1, maxY = Math.max(box.y1(), box.y2()) + 1, maxZ = Math.max(box.z1(), box.z2()) + 1;
        // 往外扩一点，避免和方块表面 Z-fighting
        float eps = 0.02f;
        minX -= eps; minY -= eps; minZ -= eps; maxX += eps; maxY += eps; maxZ += eps;
        MatrixStack.Entry e = matrices.peek();
        VertexConsumer vc = consumers.getBuffer(RenderLayers.lines());
        int argb = ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
        float w = 2.5f;
        // X 方向 4 条
        line(vc, e, minX, minY, minZ, maxX, minY, minZ, argb, w);
        line(vc, e, minX, maxY, minZ, maxX, maxY, minZ, argb, w);
        line(vc, e, minX, minY, maxZ, maxX, minY, maxZ, argb, w);
        line(vc, e, minX, maxY, maxZ, maxX, maxY, maxZ, argb, w);
        // Y 方向 4 条
        line(vc, e, minX, minY, minZ, minX, maxY, minZ, argb, w);
        line(vc, e, maxX, minY, minZ, maxX, maxY, minZ, argb, w);
        line(vc, e, minX, minY, maxZ, minX, maxY, maxZ, argb, w);
        line(vc, e, maxX, minY, maxZ, maxX, maxY, maxZ, argb, w);
        // Z 方向 4 条
        line(vc, e, minX, minY, minZ, minX, minY, maxZ, argb, w);
        line(vc, e, maxX, minY, minZ, maxX, minY, maxZ, argb, w);
        line(vc, e, minX, maxY, minZ, minX, maxY, maxZ, argb, w);
        line(vc, e, maxX, maxY, minZ, maxX, maxY, maxZ, argb, w);
    }

    private static void line(VertexConsumer vc, MatrixStack.Entry e, float x1, float y1, float z1, float x2, float y2, float z2, int argb, float width) {
        float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len > 0) { dx /= len; dy /= len; dz /= len; }
        vc.vertex(e, x1, y1, z1).color(argb).normal(e, dx, dy, dz).lineWidth(width);
        vc.vertex(e, x2, y2, z2).color(argb).normal(e, dx, dy, dz).lineWidth(width);
    }
}
