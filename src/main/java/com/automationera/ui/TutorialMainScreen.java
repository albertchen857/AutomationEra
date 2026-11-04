package com.automationera.ui;

import com.automationera.basic.ExportFile;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TutorialMainScreen extends Screen {
    private static final int NOTE_WIDTH = 200;

    public TutorialMainScreen() {
        super(Component.translatable("tutorial.ui.main"));
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
        this.addRenderableWidget(new ItemIconButton(
                new ItemStack(Items.IRON_INGOT),
                Component.translatable("tutorial.group.factory"),
                startX, y, buttonWidth, 32,
                btn -> this.minecraft.setScreen(new TutorialGroupScreen("factory", 0, 1, new IsometricRenderState()))
        ));
        // 农场
        this.addRenderableWidget(new ItemIconButton(
                new ItemStack(Items.WHEAT),
                Component.translatable("tutorial.group.farm"),
                startX + buttonWidth + spacing, y, buttonWidth, 32,
                btn -> this.minecraft.setScreen(new TutorialGroupScreen("farm", 0, 1, new IsometricRenderState()))
        ));
        // 特殊
        this.addRenderableWidget(new ItemIconButton(
                new ItemStack(Items.TNT),
                Component.translatable("tutorial.group.special"),
                startX + 2 * (buttonWidth + spacing), y, buttonWidth, 32,
                btn -> this.minecraft.setScreen(new TutorialGroupScreen("special", 0, 1, new IsometricRenderState()))
        ));
        this.addRenderableWidget(Button.builder(Component.translatable("tutorial.ui.schem"),
                btn -> ExportFile.openAutomationEraDir()).bounds(startX, this.height - 40, buttonWidth, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Buy Me A Coffee"),
                btn -> ExportFile.buymeacoffee()).bounds(startX, this.height - 70, buttonWidth, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0x99000000);
        super.extractRenderState(ctx, mouseX, mouseY, delta);
        // 1.21.6+ 颜色必须带 alpha，0xFFFFFF 会完全透明
        ctx.centeredText(this.font, this.title, this.width / 2, 16, 0xFFFFFFFF);
        ctx.textWithWordWrap(this.font, Component.translatable("tutorial.ui.note1"),
                this.width - NOTE_WIDTH - 20, this.height - 150, NOTE_WIDTH, 0xFFFFFFFF);
    }
}
