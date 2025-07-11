package com.automationera.rei.category;

import com.automationera.rei.display.TradeReiRecipe;
import com.automationera.rei.reiPlugin;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TradeRecipeCategory implements DisplayCategory<TradeReiRecipe> {
    @Override
    public CategoryIdentifier<? extends TradeReiRecipe> getCategoryIdentifier() {
        return reiPlugin.TRADE_CATEGORY_ID;
    }

    @Override
    public Renderer getIcon() {
        // Icon: Emerald for trade category
        return EntryStacks.of(Items.EMERALD);
    }

    @Override
    public Text getTitle() {
        return Text.translatable("emi.category.automationera.trade");
    }

    @Override
    public int getDisplayWidth(TradeReiRecipe display) {
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return 130;
    }

    @Override
    public List<Widget> setupDisplay(TradeReiRecipe display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        int originX = bounds.getX();
        int originY = bounds.getY();
        String key = display.getKey();
        String langCode = MinecraftClient.getInstance().getLanguageManager().getLanguage();
        int textLen = ("zh_cn".equalsIgnoreCase(langCode)) ? 17 : 40;

        // Special case: "trade_main" – similar highlight as circuit
        if (Objects.equals(key, "trade_main")) {
            widgets.add(Widgets.createLabel(new Point(originX + 5, originY + 5),
                            Text.literal(I18n.translate("emi.automationera.circuit.title")))
                    .color(0xE71B1B, 0xE71B1B).noShadow());
            String descr = I18n.translate("emi.automationera.circuit.descr");
            for (int i = 0; i <= descr.length(); i += textLen) {
                String line = descr.substring(i, Math.min(descr.length(), i + textLen));
                widgets.add(Widgets.createLabel(new Point(originX + 5, originY + 20 + (i / textLen) * 8),
                        Text.literal(line)).color(0x404040, 0x404040).noShadow().leftAligned());
            }
            widgets.add(Widgets.createTexturedWidget(
                    reiPlugin.ICON_TEXTURE,
                    new Rectangle(originX + 48, originY + 60, 64, 64),
                    0, 0, 1000, 1000, 1000, 1000));
            widgets.add(Widgets.createTexturedWidget(
                    reiPlugin.WIKI_BUTTON_TEXTURE,
                    new Rectangle(0, 0, 32, 12),
                    0, 0, 32, 12, 32, 12
            ));
            Widget wikiButton = Widgets.createButton(new Rectangle(originX + 2, originY + 120, 32, 12), Text.literal(""))
                    .onClick(button -> {
                        String url = I18n.translate("emi.automationera.circuit.wiki");
                        reiPlugin.openWiki(url);
                    });
            widgets.add(wikiButton);
            return widgets;
        }

        // Standard villager trade display
        widgets.add(Widgets.createLabel(new Point(originX + 5, originY + 5),
                        Text.literal(I18n.translate("emi.automationera." + key + ".title")))
                .color(0x404040, 0x404040).noShadow().leftAligned());
        widgets.add(Widgets.createTexturedWidget(
                reiPlugin.WIKI_BUTTON_TEXTURE,
                new Rectangle(0, 0, 32, 12),
                0, 0, 32, 12, 32, 12
        ));
        Widget wikiButton = Widgets.createButton(new Rectangle(originX + 128, originY + 0, 32, 12), Text.literal(""))
                .onClick(button -> {
                    String url = I18n.translate("emi.automationera.trade.wiki");
                    reiPlugin.openWiki(url);
                });
        widgets.add(wikiButton);
        String descr = I18n.translate("emi.automationera." + key + ".descr");
        for (int i = 0; i <= descr.length(); i += textLen) {
            String line = descr.substring(i, Math.min(descr.length(), i + textLen));
            widgets.add(Widgets.createLabel(new Point(originX + 5, originY + 20 + (i / textLen) * 8),
                    Text.literal(line)).color(0x404040, 0x404040).noShadow().leftAligned());
        }
        // Villager image corresponding to profession (job)
        String job = key.contains("_") ? key.split("_")[0] : key;
        widgets.add(Widgets.createTexturedWidget(
                reiPlugin.getVillagerTexture(job),
                new Rectangle(originX + 0, originY + 30, 48, 96),
                0, 0, 410, 825, 410, 825));

        // Determine if this display is an "input trade" or "output trade"
        boolean isOutputTrade = key.endsWith("_out");
        // Compute number of alternate ingredients for this side
        int ingOlen = 0, ingIlen = 0, inglen = 0;
        if (display.getIng().containsKey(job) && display.getIng().get(job).size() == 2) {
            ingOlen = display.getIng().get(job).get(0).size();
            ingIlen = display.getIng().get(job).get(1).size();
            inglen = isOutputTrade ? ingOlen : ingIlen;
        }
        // Determine primary item list to display (outputs vs inputs)
        List<net.minecraft.item.Item> primaryList = isOutputTrade ? display.getAllOutputs() : display.getAllInputs();
        int total = primaryList.size() + inglen;
        // Display the primary items (and any alternate ingredients) in a grid
        for (int i = 0; i < total; i++) {
            int sx = originX + 52 + (i % 6) * 18;
            int sy = originY + 40 + (i / 6) * 18;
            if (inglen == 0 || i < primaryList.size()) {
                widgets.add(Widgets.createSlot(new Point(sx, sy))
                        .entry(EntryStacks.of(primaryList.get(i)))
                        //.disableBackground()
                );
            } else {
                int altIndex = i - primaryList.size();
                List<net.minecraft.item.Item> altItems = display.getTemplate().get(
                        display.getIng().get(job).get(isOutputTrade ? 0 : 1).get(altIndex));
                List<EntryStack<?>> stacks = altItems.stream()
                        .map(item -> EntryStacks.of(new ItemStack(item)))
                        .collect(Collectors.toList());
                widgets.add(Widgets.createSlot(new Point(sx, sy))
                        .entries(stacks)
                        //.disableBackground()
                );
            }
        }
        // Always show an emerald icon at bottom-left as currency indicator
        widgets.add(Widgets.createSlot(new Point(originX + 0, originY + 114))
                .entry(EntryStacks.of(Items.EMERALD))
                //.disableBackground()
        );
        return widgets;
    }
}
