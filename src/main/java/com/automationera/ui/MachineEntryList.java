package com.automationera.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class MachineEntryList extends AbstractSelectionList<MachineEntryList.Entry> {
    private final int rowWidth;

    public MachineEntryList(Minecraft client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
        this.rowWidth = width - 12;
    }

    /**
     * 添加条目，onClick里建议传index给Screen
     */
    public void addEntry(int index, ItemStack icon, Component text, Runnable onClick) {
        this.addEntry(new com.automationera.ui.MachineEntryList.Entry(this, index, icon, text, onClick));
    }

    // 高亮用
    public void selectByIndex(int index) {
        if (index >= 0 && index < this.children().size()) {
            com.automationera.ui.MachineEntryList.Entry entry = this.children().get(index);
            this.setSelected(entry);
            this.scrollToEntry(entry);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }

    @Override
    protected void extractSelection(GuiGraphicsExtractor context, com.automationera.ui.MachineEntryList.Entry entry, int color) {
        // 选中高亮由 Entry 自己画
    }

    public static class Entry extends AbstractSelectionList.Entry<com.automationera.ui.MachineEntryList.Entry> {
        private final MachineEntryList parentList;
        private final int index;
        private final ItemStack icon;
        private final Component text;
        private final Runnable onClick;

        public Entry(MachineEntryList parentList, int index, ItemStack icon, Component text, Runnable onClick) {
            this.parentList = parentList;
            this.index = index;
            this.icon = icon;
            this.text = text;
            this.onClick = onClick;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor ctx, int mouseX, int mouseY, boolean hovered, float delta) {
            int x = this.getContentX(), y = this.getContentY(), width = this.getContentWidth(), height = this.getContentHeight();
            if (parentList.getSelected() == this) {
                ctx.fill(x, y, x + width, y + height, 0x44007FFF);
            } else if (hovered) {
                ctx.fill(x, y, x + width, y + height, 0x22FFFFFF);
            }
            ctx.item(icon, x + 2, y + (height - 16) / 2);
            var tr = Minecraft.getInstance().font;
            Component shown = text;
            int maxTextW = width - 22;
            if (tr.width(text) > maxTextW) {
                shown = Component.literal(tr.plainSubstrByWidth(text.getString(), maxTextW - tr.width("…")) + "…");
            }
            ctx.text(tr, shown, x + 20, y + (height - 8) / 2, 0xFFFFFFFF, false);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
            parentList.setSelected(this);
            onClick.run();
            return true;
        }
    }

    @Override
    public int getRowWidth() {
        return rowWidth;
    }

    @Override
    public int getRowLeft() {
        return this.getX() + 4;
    }
}
