package com.automationera.emi.category;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class TradeRecipeCategory extends EmiRecipeCategory {
    public TradeRecipeCategory() {
        super(
                new Identifier("automationera", "trade"),
                EmiStack.of(Items.EMERALD) // 类别图标
        );
    }
}

