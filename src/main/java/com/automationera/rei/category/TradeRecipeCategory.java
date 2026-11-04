package com.automationera.rei.category;

import com.automationera.rei.display.TradeReiRecipe;
import com.automationera.rei.ReiLayout;
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

        // Special case: "trade_main" – similar highlight as circuit
        if (Objects.equals(key, "trade_main")) {
            widgets.add(ReiLayout.text(I18n.translate("emi.automationera.circuit.title"),
                    originX + 5, originY + 4, 150, 15, 0xFFE71B1B, 0.6f));
            widgets.add(ReiLayout.text(I18n.translate("emi.automationera.circuit.descr"),
                    originX + 5, originY + 19, 150, 40, 0xFF404040, 0.5f));
            widgets.add(Widgets.createTexturedWidget(
                    reiPlugin.ICON_TEXTURE,
                    new Rectangle(originX + 48, originY + 60, 64, 64),
                    0, 0, 1000, 1000, 1000, 1000));
            Widget wikiButton = Widgets.createButton(new Rectangle(originX + 2, originY + 120, 32, 12), Text.literal(""))
                    .text(Text.of("WIKI"))
                    .onClick(button -> {
                        String url = I18n.translate("emi.automationera.circuit.wiki");
                        reiPlugin.openWiki(url);
                    });
            widgets.add(wikiButton);
            return widgets;
        }

        // Standard villager trade display
        widgets.add(ReiLayout.text(I18n.translate("emi.automationera." + key + ".title"),
                originX + 5, originY + 4, 120, 15, 0xFF404040, 0.6f));

        Widget wikiButton = Widgets.createButton(new Rectangle(originX + 128, originY + 2, 32, 12), Text.literal(""))
                .text(Text.of("WIKI"))
                .onClick(button -> {
                    String url = I18n.translate("emi.automationera.trade.wiki");
                    reiPlugin.openWiki(url);
                });
        widgets.add(wikiButton);
        widgets.add(ReiLayout.text(I18n.translate("emi.automationera." + key + ".descr"),
                originX + 5, originY + 19, 150, 20, 0xFF404040, 0.5f));
        // Villager image corresponding to profession (job)
        String job = key.contains("_") ? key.split("_")[0] : key;
        // 按图片真实宽高等比缩放（fisherman/farmer 比其他村民宽，写死 410x825 会被挤窄）
        widgets.add(ReiLayout.fitTexture(reiPlugin.getVillagerTexture(job), originX, originY + 30, 52, 96));

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
                        .disableBackground()
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
                        .disableBackground()
                );
            }
        }
        // Always show an emerald icon at bottom-left as currency indicator
        widgets.add(Widgets.createSlot(new Point(originX -4, originY + 114))
                .entry(EntryStacks.of(Items.EMERALD))
                //.disableBackground()
        );
        return widgets;
    }
}
