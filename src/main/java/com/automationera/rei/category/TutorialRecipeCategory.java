package com.automationera.rei.category;

import com.automationera.rei.display.TutorialReiRecipe;
import com.automationera.rei.reiPlugin;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TutorialRecipeCategory implements DisplayCategory<TutorialReiRecipe> {
    @Override
    public CategoryIdentifier<? extends TutorialReiRecipe> getCategoryIdentifier() {
        return reiPlugin.TUTORIAL_CATEGORY_ID;
    }

    @Override
    public Renderer getIcon() {
        // Icon: Knowledge Book for tutorial category
        return EntryStacks.of(Items.KNOWLEDGE_BOOK);
    }

    @Override
    public Text getTitle() {
        return Text.translatable("emi.category.automationera.tutorial");
    }

    @Override
    public int getDisplayWidth(TutorialReiRecipe display) {
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return 130;
    }

    @Override
    public List<Widget> setupDisplay(TutorialReiRecipe display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        int originX = bounds.getX();
        int originY = bounds.getY();
        String key = display.getKey();
        String langCode = MinecraftClient.getInstance().getLanguageManager().getLanguage();
        int textLen = ("zh_cn".equalsIgnoreCase(langCode)) ? 17 : 40;

        // Special case: "circuit" tutorial page
        if (Objects.equals(key, "circuit")) {
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

        // Standard tutorial display
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
                    String url = I18n.translate("emi.automationera." + key + ".wiki");
                    reiPlugin.openWiki(url);
                });
        widgets.add(wikiButton);
        String descr = I18n.translate("emi.automationera." + key + ".descr");
        for (int i = 0; i <= descr.length(); i += textLen) {
            String line = descr.substring(i, Math.min(descr.length(), i + textLen));
            widgets.add(Widgets.createLabel(new Point(originX + 5, originY + 20 + (i / textLen) * 8),
                    Text.literal(line)).color(0x404040, 0x404040).noShadow().leftAligned());
        }
        // Tutorial image (80x80) for this key
        widgets.add(Widgets.createTexturedWidget(
                reiPlugin.getTutorialTexture(key),
                new Rectangle(originX + 16, originY + 50, 80, 80),
                0, 0, 500, 500, 500, 500));
        // List all tutorial output items at bottom
        List<net.minecraft.item.Item> outputs = display.getAllOutputs();
        for (int i = 0; i < outputs.size(); i++) {
            int sx = originX + i * 18;
            int sy = originY + 114;
            widgets.add(Widgets.createSlot(new Point(sx, sy))
                    .entry(EntryStacks.of(outputs.get(i)))
                    //.disableBackground()
            );
        }
        return widgets;
    }
}
