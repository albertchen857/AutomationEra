package com.automationera.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class MachineEntryList extends EntryListWidget<MachineEntryList.Entry> {
    private final int rowWidth;

    public MachineEntryList(MinecraftClient client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
        this.rowWidth = width - 12;
    }

    /**
     * 添加条目，onClick里建议传index给Screen
     */
    public void addEntry(int index, ItemStack icon, Text text, Runnable onClick) {
        this.addEntry(new Entry(this, index, icon, text, onClick));
    }

    // 高亮用
    public void selectByIndex(int index) {
        if (index >= 0 && index < this.children().size()) {
            Entry entry = this.children().get(index);
            this.setSelected(entry);
            this.scrollTo(entry);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    protected void drawSelectionHighlight(DrawContext context, Entry entry, int color) {
        // 选中高亮由 Entry 自己画
    }

    public static class Entry extends EntryListWidget.Entry<Entry> {
        private final MachineEntryList parentList;
        private final int index;
        private final ItemStack icon;
        private final Text text;
        private final Runnable onClick;

        public Entry(MachineEntryList parentList, int index, ItemStack icon, Text text, Runnable onClick) {
            this.parentList = parentList;
            this.index = index;
            this.icon = icon;
            this.text = text;
            this.onClick = onClick;
        }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, boolean hovered, float delta) {
            int x = this.getContentX(), y = this.getContentY(), width = this.getContentWidth(), height = this.getContentHeight();
            if (parentList.getSelectedOrNull() == this) {
                ctx.fill(x, y, x + width, y + height, 0x44007FFF);
            } else if (hovered) {
                ctx.fill(x, y, x + width, y + height, 0x22FFFFFF);
            }
            ctx.drawItem(icon, x + 2, y + (height - 16) / 2);
            var tr = MinecraftClient.getInstance().textRenderer;
            Text shown = text;
            int maxTextW = width - 22;
            if (tr.getWidth(text) > maxTextW) {
                shown = Text.literal(tr.trimToWidth(text.getString(), maxTextW - tr.getWidth("…")) + "…");
            }
            ctx.drawText(tr, shown, x + 20, y + (height - 8) / 2, 0xFFFFFFFF, false);
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
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
