package com.automationera.ui;

import com.automationera.OutputRecipe.SelectionBox;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;

/**
 * 教程 3D 结构在 GUI 中的一帧渲染参数（1.21.6+ 的 GUI 只能用 PictureInPicture 方式画 3D）。
 *
 * @param centerX     结构中心在本元素内的 X（GUI 坐标，相对 x0）
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
        int x0,
        int y0,
        int x1,
        int y1,
        float scale,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {
    public TutorialStructureRenderState(TutorialManager.Structure structure, float rotation, float pitch,
                                        float centerX, float centerY, float blockSize,
                                        List<SelectionBox> boxes, float boxAlpha,
                                        int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea) {
        this(structure, rotation, pitch, centerX, centerY, blockSize, boxes, boxAlpha, x0, y0, x1, y1, 1.0f,
                scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
    }
}
