package com.automationera.rei.display;

import com.automationera.rei.recipe.MachineRecipe;
import com.automationera.rei.reiPlugin;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TradeReiRecipe implements Display {
    private final MachineRecipe recipe;
    private final Item representativeInput;
    private final Item representativeOutput;
    private final List<Item> allOutputs;
    private final List<Item> allInputs;
    private final String key;
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/rei/display/TradeRmiRecipe");
    private final Map<String, List<List<String>>> Ing = new com.automationera.OutputRecipe().OutputIng();
    private final Map<String, List<Item>> Template = new com.automationera.OutputRecipe().OutputTemplate();

    /**
     * Constructs a trade recipe display for REI.
     *
     * @param recipe    A MachineRecipe with a representative input and output (e.g., Emerald for input trades)
     * @param key       The trade identifier (profession + "_in" or "_out")
     * @param allOutputs Two lists: index 0 = output items offered by villager, index 1 = input items required by villager
     */
    public TradeReiRecipe(MachineRecipe recipe, String key, List<List<Item>> allOutputs) {
        this.recipe = recipe;
        this.representativeInput = recipe.input;
        this.representativeOutput = recipe.output;
        // For trade, reuse naming: allOutputs.get(0) = items villager offers, allOutputs.get(1) = items villager takes
        this.allOutputs = allOutputs.get(0);
        this.allInputs = allOutputs.get(1);
        this.key = key;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return reiPlugin.TRADE_CATEGORY_ID;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        if (key.endsWith("_in")) {
            // "in" trade: player gives emerald (and possibly other inputs)
            List<EntryIngredient> inputs = new ArrayList<>();
            inputs.add(EntryIngredients.of(new ItemStack(Items.EMERALD)));
            if (allInputs != null) {
                inputs.addAll(allInputs.stream().map(item -> EntryIngredients.of(new ItemStack(item))).toList());
            }
            return inputs;
        }
        // Default: for "out" trade or unspecified, list allInputs (items player gives)
        if (allInputs != null) {
            return allInputs.stream().map(item -> EntryIngredients.of(new ItemStack(item))).toList();
        }
        return List.of();
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        if (key.endsWith("_out")) {
            // "out" trade: villager gives emerald
            List<EntryIngredient> outputs = new ArrayList<>();
            outputs.add(EntryIngredients.of(new ItemStack(Items.EMERALD)));
            if (allOutputs != null) {
                outputs.addAll(allOutputs.stream().map(item -> EntryIngredients.of(new ItemStack(item))).toList());
            }
            return outputs;
        }
        // Default: for "in" trade or unspecified, list allOutputs (items villager offers)
        if (allOutputs != null) {
            return allOutputs.stream().map(item -> EntryIngredients.of(new ItemStack(item))).toList();
        }
        return List.of();
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(Identifier.of("automationera", "trade_recipe_" + key));
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }

    public String getKey() {
        return key;
    }
    public List<Item> getAllOutputs() {
        return allOutputs;
    }
    public List<Item> getAllInputs() {
        return allInputs;
    }
    public Map<String, List<List<String>>> getIng() {
        return Ing;
    }
    public Map<String, List<Item>> getTemplate() {
        return Template;
    }
}
