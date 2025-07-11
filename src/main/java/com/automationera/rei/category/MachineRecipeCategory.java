package com.automationera.rei.category;

import com.automationera.rei.display.MachineReiRecipe;
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

public class MachineRecipeCategory implements DisplayCategory<MachineReiRecipe> {
    @Override
    public CategoryIdentifier<? extends MachineReiRecipe> getCategoryIdentifier() {
        return reiPlugin.MACHINE_CATEGORY_ID;
    }

    @Override
    public Renderer getIcon() {
        // Icon remains a Redstone Block as in EMI version
        return EntryStacks.of(Items.REDSTONE_BLOCK);
    }

    @Override
    public Text getTitle() {
        // Use translation key if available for category title
        return Text.translatable("emi.category.automationera.machine");
    }

    @Override
    public int getDisplayWidth(MachineReiRecipe display) {
        // Match the custom width used in EMI
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        // Match the custom height used in EMI
        return 146;
    }

    @Override
    public List<Widget> setupDisplay(MachineReiRecipe display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        // Base background
        widgets.add(Widgets.createRecipeBase(bounds));

        // Starting coordinates relative to display bounds
        int originX = bounds.getX();
        int originY = bounds.getY();

        String key = display.getKey();
        // Determine text wrapping length based on language (Chinese vs others)
        String langCode = MinecraftClient.getInstance().getLanguageManager().getLanguage();
        int textLen = ("zh_cn".equalsIgnoreCase(langCode)) ? 17 : 40;

        // Special case: "circuit" machine – highlighted title and icon
        if (Objects.equals(key, "circuit")) {
            // Title in red
            widgets.add(Widgets.createLabel(new Point(originX + 30, originY + 5),
                            Text.literal(I18n.translate("emi.automationera.circuit.title")))
                    .color(0xE71B1B, 0xE71B1B).noShadow());
            // Machine icon image (scaled down)
            widgets.add(Widgets.createTexturedWidget(
                    reiPlugin.ICON_TEXTURE, // Identifier.of("automationera", "icon.png")
                    new Rectangle(originX + 96, originY + 60, 64, 64),
                    0, 0, 1000, 1000, 1000, 1000));
            // Description text (wrapped)
            String descr = I18n.translate("emi.automationera.circuit.descr");
            for (int i = 0; i <= descr.length(); i += textLen) {
                String line = descr.substring(i, Math.min(descr.length(), i + textLen));
                widgets.add(Widgets.createLabel(new Point(originX + 5, originY + 20 + (i / textLen) * 8),
                        Text.literal(line)).color(0x404040, 0x404040).noShadow().leftAligned());
            }
            Widget wikiButton = Widgets.createButton(new Rectangle(originX + 2, originY + 120, 32, 12), Text.literal(""))
                    .text(Text.of("WIKI"))
                    .onClick(button -> {
                        String url = I18n.translate("emi.automationera.circuit.wiki");
                        reiPlugin.openWiki(url);
                    });
            // Use the wiki button texture as background
            widgets.add(wikiButton);
            return widgets;
        }

        // Standard case for other machines:
        // Title (default color)
        widgets.add(Widgets.createLabel(new Point(originX + 5, originY + 5),
                        Text.literal(I18n.translate("emi.automationera." + key + ".title")))
                .color(0x404040, 0x404040).noShadow().leftAligned());
        Widget wikiButton = Widgets.createButton(new Rectangle(originX + 128, originY + 2, 32, 12), Text.literal(""))
                .text(Text.of("WIKI"))
                .onClick(button -> {
                    String url = I18n.translate("emi.automationera." + key + ".wiki");
                    reiPlugin.openWiki(url);
                });
        widgets.add(wikiButton);
        // Machine image (e.g., textures/<key>.png scaled to 96x96)
        widgets.add(Widgets.createTexturedWidget(
                reiPlugin.getMachineTexture(key),  // Identifier.of("automationera", "textures/" + key + ".png")
                new Rectangle(originX -8 , originY + 48, 96, 96),
                0, 0, 500, 500, 500, 500));
        // Description (wrapped lines)
        String descr = I18n.translate("emi.automationera." + key + ".descr");
        for (int i = 0; i <= descr.length(); i += textLen) {
            String line = descr.substring(i, Math.min(descr.length(), i + textLen));
            widgets.add(Widgets.createLabel(new Point(originX + 5, originY + 20 + (i / textLen) * 8),
                    Text.literal(line)).color(0x404040, 0x404040).noShadow().leftAligned());
        }

        // Compute counts for alternate outputs/inputs if any (from Ing map)
        int ingOlen = 0;
        int ingIlen = 0;
        if (display.getIng().containsKey(key) && display.getIng().get(key).size() == 2) {
            ingOlen = display.getIng().get(key).get(0).size();
            ingIlen = display.getIng().get(key).get(1).size();
        }
        // Output slots (allOutputs + any alternate output ingredients)
        int totalOutputs = display.getAllOutputs().size() + ingOlen;
        if (totalOutputs > 0) {
            for (int i = 0; i < totalOutputs; i++) {
                int sx = originX + 86 + (i % 4) * 18;
                int sy = originY + 56 + (i / 4) * 18;
                if (ingOlen == 0 || i < display.getAllOutputs().size()) {
                    // Direct output item
                    widgets.add(Widgets.createSlot(new Point(sx, sy))
                            .entry(EntryStacks.of(display.getAllOutputs().get(i)))
                            .disableBackground()
                    );
                } else {
                    // Alternate output ingredient (template-defined)
                    int altIndex = i - display.getAllOutputs().size();
                    List<net.minecraft.item.Item> altItems = display.getTemplate().get(
                            display.getIng().get(key).get(0).get(altIndex));
                    List<EntryStack<?>> stacks = altItems.stream()
                            .map(item -> EntryStacks.of(new ItemStack(item)))
                            .collect(Collectors.toList());
                    widgets.add(Widgets.createSlot(new Point(sx, sy))
                            .entries(stacks)
                            .disableBackground()
                    );
                }
            }
        }
        // Input slots (allInputs + any alternate input ingredients)
        int totalInputs = display.getAllInputs().size() + ingIlen;
        if (totalInputs > 0) {
            for (int i = 0; i < totalInputs; i++) {
                int sx = originX - 8 + i * 18;
                int sy = originY + 130;
                if (ingIlen == 0 || i < display.getAllInputs().size()) {
                    widgets.add(Widgets.createSlot(new Point(sx, sy))
                            .entry(EntryStacks.of(display.getAllInputs().get(i)))
                            //.disableBackground()
                    );
                } else {
                    int altIndex = i - display.getAllInputs().size();
                    List<net.minecraft.item.Item> altItems = display.getTemplate().get(
                            display.getIng().get(key).get(1).get(altIndex));
                    List<EntryStack<?>> stacks = altItems.stream()
                            .map(item -> EntryStacks.of(new ItemStack(item)))
                            .collect(Collectors.toList());
                    widgets.add(Widgets.createSlot(new Point(sx, sy))
                            .entries(stacks)
                            //.disableBackground()
                    );
                }
            }
        }
        return widgets;
    }
}
