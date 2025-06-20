package com.automationera.emi;

import com.automationera.emi.category.MachineRecipeCategory;
import com.automationera.emi.display.MachineEmiRecipe;
import com.automationera.emi.recipe.MachineRecipe;
import com.automationera.emi.recipe.OutputRecipe;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import dev.emi.emi.api.render.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class emiPlugin implements EmiPlugin {
    public static final Identifier MACHINE_CATEGORY_ID = new Identifier("automationera", "machine");
    public static final EmiRecipeCategory MACHINE_CATEGORY = new MachineRecipeCategory();
    public static final Map<String, List<Item>> ore = new OutputRecipe().OutputdRecipy();

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MACHINE_CATEGORY);
        for (Map.Entry<String, List<Item>> entry : ore.entrySet()) {
            registry.addRecipe(new MachineEmiRecipe(
                new MachineRecipe(Items.AIR, entry.getValue().get(0)),
                entry.getValue()
            ));
        }
    }
}

