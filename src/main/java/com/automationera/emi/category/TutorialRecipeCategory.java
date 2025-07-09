package com.automationera.emi.category;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class TutorialRecipeCategory extends EmiRecipeCategory {
    public TutorialRecipeCategory() {
        super(
                Identifier.of("automationera", "tutorial"),
                EmiStack.of(Items.KNOWLEDGE_BOOK) // 类别图标
        );
    }
}

