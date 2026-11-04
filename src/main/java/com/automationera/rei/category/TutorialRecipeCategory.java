package com.automationera.rei.category;

import com.automationera.rei.display.TutorialReiRecipe;
import com.automationera.rei.ReiLayout;
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
        return 146;
    }

    @Override
    public List<Widget> setupDisplay(TutorialReiRecipe display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        int originX = bounds.getX();
        int originY = bounds.getY();
        String key = display.getKey();

        // Special case: "circuit" tutorial page
        if (Objects.equals(key, "circuit")) {
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
        // Tutorial image (80x80) for this key
        widgets.add(Widgets.createTexturedWidget(
                reiPlugin.getTutorialTexture(key),
                new Rectangle(originX + 16, originY + 62, 80, 80),
                0, 0, 500, 500, 500, 500));
        // Standard tutorial display
        widgets.add(ReiLayout.text(I18n.translate("emi.automationera." + key + ".title"),
                originX + 5, originY + 4, 120, 15, 0xFF404040, 0.6f));
        Widget wikiButton = Widgets.createButton(new Rectangle(originX + 128, originY + 2, 32, 12), Text.literal(""))
                .text(Text.of("WIKI"))
                .onClick(button -> {
                    String url = I18n.translate("emi.automationera." + key + ".wiki");
                    reiPlugin.openWiki(url);
                });
        widgets.add(wikiButton);
        widgets.add(ReiLayout.text(I18n.translate("emi.automationera." + key + ".descr"),
                originX + 5, originY + 19, 150, 43, 0xFF404040, 0.5f));
        // List all tutorial output items at bottom
        List<net.minecraft.item.Item> outputs = display.getAllOutputs();
        for (int i = 0; i < outputs.size(); i++) {
            int sx = originX + 136;
            int sy = originY + 124 - i * 18;
            widgets.add(Widgets.createSlot(new Point(sx, sy))
                    .entry(EntryStacks.of(outputs.get(i)))
                    //.disableBackground()
            );
        }
        return widgets;
    }
}
