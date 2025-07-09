package com.automationera.emi.category;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineRecipeCategory extends EmiRecipeCategory {
    public MachineRecipeCategory() {
        super(
                Identifier.of("automationera", "machine"),
                EmiStack.of(Items.REDSTONE_BLOCK) // 类别图标
        );
    }
}

