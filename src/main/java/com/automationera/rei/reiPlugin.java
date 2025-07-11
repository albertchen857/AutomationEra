package com.automationera.rei;

import com.automationera.OutputRecipe;
import com.automationera.rei.category.MachineRecipeCategory;
import com.automationera.rei.category.TradeRecipeCategory;
import com.automationera.rei.category.TutorialRecipeCategory;
import com.automationera.rei.display.MachineReiRecipe;
import com.automationera.rei.display.TradeReiRecipe;
import com.automationera.rei.display.TutorialReiRecipe;
import com.automationera.rei.recipe.MachineRecipe;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public class reiPlugin implements REIClientPlugin {
    // New Category Identifiers for REI (replace EmiRecipeCategory)
    public static final CategoryIdentifier<MachineReiRecipe> MACHINE_CATEGORY_ID =
            CategoryIdentifier.of("automationera", "machine");
    public static final CategoryIdentifier<TradeReiRecipe> TRADE_CATEGORY_ID =
            CategoryIdentifier.of("automationera", "trade");
    public static final CategoryIdentifier<TutorialReiRecipe> TUTORIAL_CATEGORY_ID =
            CategoryIdentifier.of("automationera", "tutorial");

    // Data maps from OutputRecipe (unchanged)
    public static final Map<String, List<List<Item>>> ore = new OutputRecipe().OutputdRecipy();
    public static final Map<String, List<List<Item>>> trade = new OutputRecipe().TradeRecipy();
    public static final Map<String, List<Item>> tut = new OutputRecipe().TutorialRecipy();

    public static final Identifier ICON_TEXTURE = Identifier.of("automationera", "icon.png");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        // Register custom recipe categories
        registry.add(new MachineRecipeCategory());
        registry.add(new TradeRecipeCategory());
        registry.add(new TutorialRecipeCategory());
        // (Optional) link workstations or other category settings if needed
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // Register a sample machine recipe (circuit example)
        registry.add(new MachineReiRecipe(
                new MachineRecipe(Items.REDSTONE, Items.REDSTONE),
                "circuit",
                List.of(List.of(Items.REDSTONE), List.of(Items.REDSTONE_BLOCK))
        ));
        // Register each machine entry from ore map as a recipe (show all outputs of that machine)
        for (Map.Entry<String, List<List<Item>>> entry : ore.entrySet()) {
            String key = entry.getKey();
            List<Item> outputs = entry.getValue().get(0);
            List<Item> inputs = entry.getValue().get(1);
            if (!outputs.isEmpty()) {
                // Use a dummy MachineRecipe (AIR -> first output) to represent machine outputs
                registry.add(new MachineReiRecipe(new MachineRecipe(Items.AIR, outputs.get(0)), key, entry.getValue()));
            }
            // Note: The complementary input-focused machine recipe (MachineEmiRecipe in EMI) is omitted in REI for simplicity
        }
        // Register villager trade recipes
        for (Map.Entry<String, List<List<Item>>> entry : trade.entrySet()) {
            String key = entry.getKey();
            List<Item> offers = entry.getValue().get(0);
            List<Item> requirements = entry.getValue().get(1);
            if (!offers.isEmpty()) {
                // Villager offers (output) for emerald
                registry.add(new TradeReiRecipe(new MachineRecipe(Items.EMERALD, offers.get(0)), key + "_in", entry.getValue()));
            }
            if (!requirements.isEmpty()) {
                // Villager accepts item for emerald (emerald as output)
                registry.add(new TradeReiRecipe(new MachineRecipe(requirements.get(0), Items.EMERALD), key + "_out", entry.getValue()));
            }
        }
        // Register tutorial entries
        for (Map.Entry<String, List<Item>> entry : tut.entrySet()) {
            String key = entry.getKey();
            List<Item> outputItems = entry.getValue();
            if (!outputItems.isEmpty()) {
                // Use first item as representative (input/output are the same dummy for tutorial)
                Item repr = outputItems.get(0);
                registry.add(new TutorialReiRecipe(new MachineRecipe(repr, repr), key, outputItems));
            }
        }
    }

    // Utility methods for texture Identifiers (for categories to use)
    public static Identifier getMachineTexture(String machineKey) {
        return Identifier.of("automationera", "textures/" + machineKey + ".png");
    }
    public static Identifier getVillagerTexture(String job) {
        return Identifier.of("automationera", "textures/villager/" + job + ".png");
    }
    public static Identifier getTutorialTexture(String tutKey) {
        return Identifier.of("automationera", "textures/tutorial/" + tutKey + ".png");
    }
    // Utility to open wiki URL (called from category widgets on button click)
    public static void openWiki(String url) {
        net.minecraft.util.Util.getOperatingSystem().open(url);
    }
}
