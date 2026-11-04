package com.automationera.ui;

import com.automationera.basic.ExportFile;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class TutorialMainScreen extends Screen {
    private static final int NOTE_WIDTH = 200;

    public TutorialMainScreen() {
        super(Text.translatable("tutorial.ui.main"));
    }

    @Override
    protected void init() {
        TutorialManager.clearCache();
        int midX = this.width / 2;
        int y = 40;

        int buttonWidth = 100;
        int spacing = 32;
        int totalWidth = 3 * buttonWidth + 2 * spacing;
        int startX = midX - totalWidth / 2;

        // 工厂
        this.addDrawableChild(new ItemIconButton(
                new ItemStack(Items.IRON_INGOT),
                Text.translatable("tutorial.group.factory"),
                startX, y, buttonWidth, 32,
                btn -> this.client.setScreen(new TutorialGroupScreen("factory", 0, 1, new IsometricRenderState()))
        ));
        // 农场
        this.addDrawableChild(new ItemIconButton(
                new ItemStack(Items.WHEAT),
                Text.translatable("tutorial.group.farm"),
                startX + buttonWidth + spacing, y, buttonWidth, 32,
                btn -> this.client.setScreen(new TutorialGroupScreen("farm", 0, 1, new IsometricRenderState()))
        ));
        // 特殊
        this.addDrawableChild(new ItemIconButton(
                new ItemStack(Items.TNT),
                Text.translatable("tutorial.group.special"),
                startX + 2 * (buttonWidth + spacing), y, buttonWidth, 32,
                btn -> this.client.setScreen(new TutorialGroupScreen("special", 0, 1, new IsometricRenderState()))
        ));
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("tutorial.ui.schem"),
                btn -> ExportFile.openAutomationEraDir()).dimensions(startX, this.height - 40, buttonWidth, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Buy Me A Coffee"),
                btn -> ExportFile.buymeacoffee()).dimensions(startX, this.height - 70, buttonWidth, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0x99000000);
        super.render(ctx, mouseX, mouseY, delta);
        // 1.21.6+ 颜色必须带 alpha，0xFFFFFF 会完全透明
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 16, 0xFFFFFFFF);
        ctx.drawWrappedTextWithShadow(this.textRenderer, Text.translatable("tutorial.ui.note1"),
                this.width - NOTE_WIDTH - 20, this.height - 150, NOTE_WIDTH, 0xFFFFFFFF);
    }
}
