package com.automationera.rei.category;

import com.automationera.OutputRecipe;
import com.automationera.rei.ReiLayout;
import com.automationera.rei.display.MachineReiRecipe;
import com.automationera.rei.reiPlugin;
import com.automationera.ui.IsometricRenderState;
import com.automationera.ui.TutorialGroupScreen;
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
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MachineRecipeCategory implements DisplayCategory<MachineReiRecipe> {
    public static final Logger LOGGER = reiPlugin.LOGGER;
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

        if (Objects.equals(key, "circuit")) {
            widgets.add(ReiLayout.text(I18n.translate("emi.automationera.circuit.title"),
                    originX + 5, originY + 4, 150, 15, 0xFFE71B1B, 0.6f));
            widgets.add(Widgets.createTexturedWidget(
                    reiPlugin.ICON_TEXTURE, // Identifier.of("automationera", "icon.png")
                    new Rectangle(originX + 96, originY + 60, 64, 64),
                    0, 0, 1000, 1000, 1000, 1000));
            widgets.add(ReiLayout.text(I18n.translate("emi.automationera.circuit.descr"),
                    originX + 5, originY + 19, 150, 40, 0xFF404040, 0.5f));
            Widget wikiButton = Widgets.createButton(new Rectangle(originX + 2, originY + 120, 32, 12), Text.literal("wiki"))
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
//        LOGGER.info("{}|{}",I18n.translate("emi.automationera." + key + ".title"),I18n.translate("emi.automationera." + key + ".descr"));
        widgets.add(ReiLayout.text(I18n.translate("emi.automationera." + key + ".title"),
                originX + 5, originY + 4, 120, 15, 0xFF404040, 0.6f));
        Widget wikiButton = Widgets.createButton(new Rectangle(originX + 128, originY + 2, 32, 12), Text.literal(""))
                .text(Text.of("WIKI"))
                .onClick(button -> {
                    String url = I18n.translate("emi.automationera." + key + ".wiki");
                    reiPlugin.openWiki(url);
                });
        widgets.add(wikiButton);
        // 跳转到对应的 3D 教程
        List<?> tutorial = OutputRecipe.ConvertMap().get(key);
        if (tutorial != null) {
            widgets.add(Widgets.createButton(
                            new Rectangle(originX + 135, originY + 120, 22, 22),
                            Text.literal(""))
                    .tooltipLines(Text.translatable("tutorial.ui.open"))
                    .onClick(button -> MinecraftClient.getInstance().setScreen(new TutorialGroupScreen(
                            (String) tutorial.getFirst(), (int) tutorial.getLast(), 1, new IsometricRenderState()))));
            widgets.add(Widgets.createTexturedWidget(Identifier.of("automationera", "textures/gui/wikibutton.png"),
                    originX + 136, originY + 121, 1, 1, 20, 20));
        }
        // Machine image (e.g., textures/<key>.png scaled to 96x96)
        widgets.add(Widgets.createTexturedWidget(
                reiPlugin.getMachineTexture(key),  // Identifier.of("automationera", "textures/" + key + ".png")
                new Rectangle(originX -8 , originY + 48, 96, 96),
                0, 0, 500, 500, 500, 500));
        // Description (wrapped lines)
        widgets.add(ReiLayout.text(I18n.translate("emi.automationera." + key + ".descr"),
                originX + 5, originY + 19, 150, 36, 0xFF404040, 0.5f));

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
                            display.getIng().get(key).getFirst().get(altIndex));
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
