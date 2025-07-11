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
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;

public class MachineReiRecipe implements Display {
    private final MachineRecipe recipe;
    private final Item representativeInput;
    private final Item representativeOutput;
    private final List<Item> allOutputs;
    private final List<Item> allInputs;
    private final String key;
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/rei/display/MachineReiRecipe");
    private final Map<String, List<List<String>>> Ing = new com.automationera.OutputRecipe().OutputIng();
    private final Map<String, List<Item>> Template = new com.automationera.OutputRecipe().OutputTemplate();

    /**
     * Constructs a machine recipe display for REI.
     *
     * @param recipe    A MachineRecipe containing a dummy input (e.g. AIR) and a representative output
     * @param key       The machine identifier (used for locating textures and localized text)
     * @param allOutputs A List of two lists: index 0 = all output Items this machine can produce, index 1 = all input Items this machine can accept
     */
    public MachineReiRecipe(MachineRecipe recipe, String key, List<List<Item>> allOutputs) {
        this.recipe = recipe;
        this.representativeInput = recipe.input;
        this.representativeOutput = recipe.output;
        // allOutputs list structure: [outputsList, inputsList]
        this.allOutputs = allOutputs.get(0);
        this.allInputs = allOutputs.get(1);
        this.key = key;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return reiPlugin.MACHINE_CATEGORY_ID;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        if (allInputs != null) {
            // All machine inputs as individual ingredients
            return allInputs.stream()
                    .map(item -> EntryIngredients.of(new ItemStack(item)))
                    .toList();
        }
        // Fallback to representative input if no list provided
        return List.of(EntryIngredients.of(new ItemStack(representativeInput)));
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        if (allOutputs != null && !allOutputs.isEmpty()) {
            // Show only the representative output (first in list)
            Item firstOutput = allOutputs.get(0);
            return List.of(EntryIngredients.of(new ItemStack(firstOutput)));
        }
        // Fallback: if no outputs list, use representative output
        return List.of(EntryIngredients.of(new ItemStack(representativeOutput)));
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        // Generate a unique recipe ID based on machine key
        return Optional.of(Identifier.of("automationera", "machine_recipe_" + key));
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }

    // Additional getters for use in Category layout
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
