package com.automationera.ui;

import com.automationera.OutputRecipe.SelectionBox;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import org.joml.Quaternionf;

/**
 * 把教程结构渲染到离屏纹理，再由 GUI 贴回界面。
 * 26.x 起渲染改为“提交节点”：方块用 BlockModelResolver + BlockModelRenderState（和方块展示实体同一套，
 * 箱子/床这类特殊模型也能画），流体用 submitCustomGeometry，选区框用 submitShapeOutline；
 * 半透明排序由 FeatureRenderDispatcher 统一处理，不再需要手动分两遍画。
 */
public class TutorialStructureRenderer extends PictureInPictureRenderer<TutorialStructureRenderState> {
    private static final int FULL_BRIGHT = LightCoordsUtil.FULL_BRIGHT;
    // 正交投影深度范围只有 ±1000，大结构放大后会被裁掉；压缩视线方向的深度即可，不影响画面
    private static final float DEPTH_SQUASH = 0.25f;
    private static final SpriteId WATER = new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.withDefaultNamespace("block/water_still"));
    private static final SpriteId LAVA = new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.withDefaultNamespace("block/lava_still"));
    private static final BlockDisplayContext DISPLAY_CONTEXT = BlockDisplayContext.create();

    private final BlockModelRenderState blockState = new BlockModelRenderState();
    private BlockModelResolver resolver;

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
    protected void renderToTexture(TutorialStructureRenderState state, PoseStack matrices, SubmitNodeCollector collector) {
        Minecraft mc = Minecraft.getInstance();
        mc.gameRenderer.lighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        if (resolver == null) resolver = new BlockModelResolver(mc.getModelManager());
        TutorialManager.Structure structure = state.structure();

        matrices.pushPose();
        matrices.scale(1f, 1f, DEPTH_SQUASH);
        matrices.translate(state.centerX() - (state.x1() - state.x0()) / 2f, state.centerY(), 0f);
        // 下面的 Z 翻转会让旋转方向反过来，这里取负号：仰角负数 = 俯视，向右拖 = 近处往右转（与 1.21.5 一致）
        matrices.rotate(new Quaternionf().rotateX((float) Math.toRadians(-state.pitch())));
        matrices.rotate(new Quaternionf().rotateY((float) Math.toRadians(-state.rotation())));
        float s = state.blockSize();
        // 手性：GUI 投影本身 Y 向下（一次镜像），PiP 底层又翻了一次 Z。
        // 这里必须再翻 Y 和 Z（总共奇数次翻转），世界才不是镜像的；与 1.21.5 的 scale(g,-g,g) 等价
        matrices.scale(s, -s, -s);
        matrices.translate(
                -(structure.minX() + structure.maxX() + 1) / 2f,
                -(structure.minY() + structure.maxY() + 1) / 2f,
                -(structure.minZ() + structure.maxZ() + 1) / 2f);

        submitBlocks(structure, matrices, collector);
        submitFluids(mc, structure, matrices, collector);
        int argb = ((int) (state.boxAlpha() * 255) << 24) | 0xFFFF00;
        for (SelectionBox box : state.boxes()) {
            submitBoxOutline(matrices, collector, box, argb);
        }
        matrices.popPose();
    }

    private void submitBlocks(TutorialManager.Structure structure, PoseStack matrices, SubmitNodeCollector collector) {
        for (TutorialManager.PlacedBlock b : structure.blocks()) {
            resolver.update(blockState, b.state(), DISPLAY_CONTEXT);
            if (blockState.isEmpty()) continue;
            matrices.pushPose();
            matrices.translate(b.x(), b.y(), b.z());
            blockState.submit(matrices, collector, FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
            matrices.popPose();
        }
    }

    private void submitFluids(Minecraft mc, TutorialManager.Structure structure, PoseStack matrices, SubmitNodeCollector collector) {
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
            final TextureAtlasSprite sp = sprite;
            matrices.pushPose();
            matrices.translate(b.x(), b.y(), b.z());
            collector.submitCustomGeometry(matrices, RenderTypes.translucentMovingBlock(),
                    (pose, vc) -> fluidCube(pose, vc, sp, height - 0.001f, color));
            matrices.popPose();
        }
    }

    private static void fluidCube(PoseStack.Pose e, VertexConsumer vc, TextureAtlasSprite sprite, float y2, int argb) {
        float u0 = sprite.getU0(), u1 = sprite.getU1(), v0 = sprite.getV0(), v1 = sprite.getV1();
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

    /** 选区框：用原版的形状描边（方块选中框那一套），往外扩一点避免和方块表面 Z-fighting */
    private static void submitBoxOutline(PoseStack matrices, SubmitNodeCollector collector, SelectionBox box, int argb) {
        double eps = 0.02;
        double minX = Math.min(box.x1(), box.x2()) - eps, minY = Math.min(box.y1(), box.y2()) - eps, minZ = Math.min(box.z1(), box.z2()) - eps;
        double maxX = Math.max(box.x1(), box.x2()) + 1 + eps, maxY = Math.max(box.y1(), box.y2()) + 1 + eps, maxZ = Math.max(box.z1(), box.z2()) + 1 + eps;
        collector.submitShapeOutline(matrices, Shapes.create(minX, minY, minZ, maxX, maxY, maxZ), RenderTypes.lines(), argb, 2.5f, false);
    }
}
