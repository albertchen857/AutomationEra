package com.automationera.ui;

import com.automationera.OutputRecipe.SelectionBox;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 教程 3D 结构在 GUI 中的一帧渲染参数（1.21.6+ 的 GUI 只能用 PictureInPicture 方式画 3D）。
 *
 * @param centerX     结构中心在本元素内的 X（GUI 坐标，相对 x1）
 * @param centerY     结构中心在本元素内的 Y（GUI 坐标，相对 y1）
 * @param blockSize   每个方块的边长（GUI 像素）
 * @param boxAlpha    选区框当前透明度（呼吸效果）
 */
public record TutorialStructureRenderState(
        TutorialManager.Structure structure,
        float rotation,
        float pitch,
        float centerX,
        float centerY,
        float blockSize,
        List<SelectionBox> boxes,
        float boxAlpha,
        int x1,
        int y1,
        int x2,
        int y2,
        float scale,
        @Nullable ScreenRect scissorArea,
        @Nullable ScreenRect bounds
) implements SpecialGuiElementRenderState {
    public TutorialStructureRenderState(TutorialManager.Structure structure, float rotation, float pitch,
                                        float centerX, float centerY, float blockSize,
                                        List<SelectionBox> boxes, float boxAlpha,
                                        int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea) {
        this(structure, rotation, pitch, centerX, centerY, blockSize, boxes, boxAlpha, x1, y1, x2, y2, 1.0f,
                scissorArea, SpecialGuiElementRenderState.createBounds(x1, y1, x2, y2, scissorArea));
    }
}
