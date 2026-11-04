package com.automationera.ui;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;

public class ItemIconButton extends Button {
    private final ItemStack stack;

    public ItemIconButton(ItemStack stack, net.minecraft.network.chat.Component text, int x, int y, int width, int height, OnPress action) {
        super(x, y, width, height, text, action, Supplier::get);
        this.stack = stack;
    }

    @Override
    protected void renderContents(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        // 绘制按钮背景
        this.renderDefaultSprite(ctx);
        // 绘制物品图标 (左边留点边距)
        int iconX = this.getX() + 4;
        int iconY = this.getY() + (this.getHeight() - 16) / 2;
        ctx.item(stack, iconX, iconY);
        // 绘制文字 (图标右边留 margin)
        Font textRenderer = Minecraft.getInstance().font;
        int textX = iconX + 20; // 图标宽度16 + margin 4
        int textY = this.getY() + (this.getHeight() - 8) / 2; // 居中
        ctx.text(textRenderer, this.getMessage(), textX, textY, this.active ? 0xFFFFFFFF : 0xFFA0A0A0);
    }
}
