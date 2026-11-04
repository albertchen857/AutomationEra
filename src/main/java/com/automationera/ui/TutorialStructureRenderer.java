package com.automationera.ui;

import com.automationera.OutputRecipe.SelectionBox;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.joml.Quaternionf;

/**
 * 把教程结构渲染到离屏纹理，再由 GUI 贴回界面（替代 1.21.5 及以前直接在 GuiGraphicsExtractor 里画方块的做法）。
 */
public class TutorialStructureRenderer extends PictureInPictureRenderer<TutorialStructureRenderState> {
    private static final int FULL_BRIGHT = LightTexture.FULL_BRIGHT;
    // 正交投影深度范围只有 ±1000，大结构放大后会被裁掉；压缩视线方向的深度即可，不影响画面
    private static final float DEPTH_SQUASH = 0.25f;
    private static final Material WATER = new Material(TextureAtlas.LOCATION_BLOCKS, Identifier.withDefaultNamespace("block/water_still"));
    private static final Material LAVA = new Material(TextureAtlas.LOCATION_BLOCKS, Identifier.withDefaultNamespace("block/lava_still"));

    public TutorialStructureRenderer(MultiBufferSource.BufferSource vertexConsumers) {
        super(vertexConsumers);
    }

    @Override
    public Class<TutorialStructureRenderState> getRenderStateClass() {
        return TutorialStructureRenderState.class;
    }

    @Override
    protected float getTranslateY(int height, int windowScaleFactor) {
        return 0; // 原点放在元素顶部中点，方便直接用 GUI 坐标定位
    }

    @Override
    protected String getTextureLabel() {
        return "automationera tutorial structure";
    }

    @Override
    protected void renderToTexture(TutorialStructureRenderState state, PoseStack matrices) {
        Minecraft mc = Minecraft.getInstance();
        mc.gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        TutorialManager.Structure structure = state.structure();

        matrices.pushPose();
        matrices.scale(1f, 1f, DEPTH_SQUASH);
        matrices.translate(state.centerX() - (state.x1() - state.x0()) / 2f, state.centerY(), 0f);
        // 下面的 Z 翻转会让旋转方向反过来，这里取负号：仰角负数 = 俯视，向右拖 = 近处往右转（与 1.21.5 一致）
        matrices.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-state.pitch())));
        matrices.mulPose(new Quaternionf().rotateY((float) Math.toRadians(-state.rotation())));
        float s = state.blockSize();
        // 手性：GUI 投影本身 Y 向下（一次镜像），PiP 底层又翻了一次 Z。
        // 这里必须再翻 Y 和 Z（总共奇数次翻转），世界才不是镜像的；与 1.21.5 的 scale(g,-g,g) 等价
        matrices.scale(s, -s, -s);
        matrices.translate(
                -(structure.minX() + structure.maxX() + 1) / 2f,
                -(structure.minY() + structure.maxY() + 1) / 2f,
                -(structure.minZ() + structure.maxZ() + 1) / 2f);

        BlockRenderDispatcher brm = mc.getBlockRenderer();
        // 先画不透明方块，再画半透明方块，最后流体和选区框，避免半透明排序问题
        renderBlocks(structure, matrices, brm, false);
        renderBlocks(structure, matrices, brm, true);
        renderFluids(mc, structure, matrices);
        for (SelectionBox box : state.boxes()) {
            renderBoxOutline(matrices, this.bufferSource, box, 1f, 1f, 0f, state.boxAlpha());
        }
        matrices.popPose();
    }

    private void renderBlocks(TutorialManager.Structure structure, PoseStack matrices, BlockRenderDispatcher brm, boolean translucentPass) {
        for (TutorialManager.PlacedBlock b : structure.blocks()) {
            BlockState bs = b.state();
            if (bs.getRenderShape() != RenderShape.MODEL) continue;
            if (ItemBlockRenderTypes.getChunkRenderType(bs).sortOnUpload() != translucentPass) continue;
            matrices.pushPose();
            matrices.translate(b.x(), b.y(), b.z());
            brm.renderSingleBlock(bs, matrices, this.bufferSource, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            matrices.popPose();
        }
    }

    private void renderFluids(Minecraft mc, TutorialManager.Structure structure, PoseStack matrices) {
        TextureAtlasSprite water = null, lava = null;
        for (TutorialManager.PlacedBlock b : structure.blocks()) {
            FluidState fs = b.state().getFluidState();
            if (fs.isEmpty()) continue;
            boolean isLava = fs.is(FluidTags.LAVA);
            TextureAtlasSprite sprite;
            if (isLava) {
                if (lava == null) lava = mc.getAtlasManager().get(LAVA);
                sprite = lava;
            } else {
                if (water == null) water = mc.getAtlasManager().get(WATER);
                sprite = water;
            }
            // 水源/含水方块满格；流动液体按等级算高度（下落的流体 level=8 也接近满格）
            float height = fs.isSource() ? 1.0f : Math.max(1, fs.getAmount()) / 9.0f;
            int color = isLava ? 0xFFFFFFFF : 0xE63F76E4;
            matrices.pushPose();
            matrices.translate(b.x(), b.y(), b.z());
            renderFluidCube(matrices, this.bufferSource, sprite, height - 0.001f, color);
            matrices.popPose();
        }
    }

    private static void renderFluidCube(PoseStack matrices, MultiBufferSource consumers, TextureAtlasSprite sprite, float y2, int argb) {
        float u0 = sprite.getU0(), u1 = sprite.getU1(), v0 = sprite.getV0(), v1 = sprite.getV1();
        PoseStack.Pose e = matrices.last();
        VertexConsumer vc = consumers.getBuffer(RenderTypes.translucentMovingBlock());
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

    private static void quad(VertexConsumer vc, PoseStack.Pose e,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float u0, float v0, float u1, float v1, int argb, float nx, float ny, float nz) {
        vc.addVertex(e, x1, y1, z1).setColor(argb).setUv(u0, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(e, nx, ny, nz);
        vc.addVertex(e, x2, y2, z2).setColor(argb).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(e, nx, ny, nz);
        vc.addVertex(e, x3, y3, z3).setColor(argb).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(e, nx, ny, nz);
        vc.addVertex(e, x4, y4, z4).setColor(argb).setUv(u0, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(e, nx, ny, nz);
    }

    /** 1.21.11 起线宽是顶点属性（没有 RenderSystem.lineWidth 了），法线必须是线段方向，否则线会消失 */
    private static void renderBoxOutline(PoseStack matrices, MultiBufferSource consumers, SelectionBox box,
                                         float r, float g, float b, float a) {
        float minX = Math.min(box.x1(), box.x2()), minY = Math.min(box.y1(), box.y2()), minZ = Math.min(box.z1(), box.z2());
        float maxX = Math.max(box.x1(), box.x2()) + 1, maxY = Math.max(box.y1(), box.y2()) + 1, maxZ = Math.max(box.z1(), box.z2()) + 1;
        // 往外扩一点，避免和方块表面 Z-fighting
        float eps = 0.02f;
        minX -= eps; minY -= eps; minZ -= eps; maxX += eps; maxY += eps; maxZ += eps;
        PoseStack.Pose e = matrices.last();
        VertexConsumer vc = consumers.getBuffer(RenderTypes.lines());
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

    private static void line(VertexConsumer vc, PoseStack.Pose e, float x1, float y1, float z1, float x2, float y2, float z2, int argb, float width) {
        float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len > 0) { dx /= len; dy /= len; dz /= len; }
        vc.addVertex(e, x1, y1, z1).setColor(argb).setNormal(e, dx, dy, dz).setLineWidth(width);
        vc.addVertex(e, x2, y2, z2).setColor(argb).setNormal(e, dx, dy, dz).setLineWidth(width);
    }
}
