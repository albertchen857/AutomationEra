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
import java.util.Optional;
import java.util.stream.Collectors;

public class TutorialReiRecipe implements Display {
    private final MachineRecipe recipe;
    private final Item representativeOutput;
    private final List<Item> allOutputs;
    private final String key;
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/rei/display/TutorialRmiRecipe");

    /**
     * Constructs a tutorial recipe display for REI.
     *
     * @param recipe     A MachineRecipe with a representative output (both input/output may be same dummy item)
     * @param key        The tutorial identifier
     * @param allOutputs The list of all Items showcased in this tutorial entry
     */
    public TutorialReiRecipe(MachineRecipe recipe, String key, List<Item> allOutputs) {
        this.recipe = recipe;
        this.representativeOutput = recipe.output;
        this.allOutputs = allOutputs;
        this.key = key;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return reiPlugin.TUTORIAL_CATEGORY_ID;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        if (allOutputs != null) {
            // Show all tutorial items as "inputs" as well (for completeness/search)
            return allOutputs.stream()
                    .map(item -> EntryIngredients.of(new ItemStack(item)))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        if (allOutputs != null) {
            // Show all tutorial items as outputs
            return allOutputs.stream()
                    .map(item -> EntryIngredients.of(new ItemStack(item)))
                    .collect(Collectors.toList());
        }
        // Fallback: single output
        return List.of(EntryIngredients.of(new ItemStack(representativeOutput)));
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(Identifier.of("automationera", "tutorial_recipe_" + key));
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
}
