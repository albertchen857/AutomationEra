package com.automationera.emi.category;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class MachineRecipeCategory extends EmiRecipeCategory {
    public MachineRecipeCategory() {
        super(
                new Identifier("automationera", "machine"),
                EmiStack.of(Items.DISPENSER) // 类别图标
        );
    }
}

