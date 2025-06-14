package com.automationera.emi;

import com.automationera.emi.category.MachineRecipeCategory;
import com.automationera.emi.display.MachineEmiRecipe;
import com.automationera.emi.recipe.MachineRecipe;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import dev.emi.emi.api.render.*;

import java.util.List;

public class emiPlugin implements EmiPlugin {
    public static final Identifier MACHINE_CATEGORY_ID = new Identifier("automationera", "machine");
    public static final EmiRecipeCategory MACHINE_CATEGORY = new EmiRecipeCategory(MACHINE_CATEGORY_ID, EmiStack.of(Items.DISPENSER));

    @Override
    public void register(EmiRegistry registry) {
        // 添加“机器”分类
        registry.addCategory(new MachineRecipeCategory());
        // 添加工作站（机器图标显示在哪）
        registry.addWorkstation(MACHINE_CATEGORY, EmiStack.of(Items.DISPENSER)); // 自定义图标机器

        // 添加自定义配方展示
        List<MachineRecipe> recipes = List.of(
                new MachineRecipe(Items.CACTUS, Items.CACTUS)
        );
        for (MachineRecipe recipe : recipes) {
            registry.addRecipe(new MachineEmiRecipe(recipe));
        }
    }
}

