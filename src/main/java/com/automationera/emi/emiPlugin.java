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
        // 添加"机器"分类
        registry.addCategory(MACHINE_CATEGORY);
        // 添加工作站（机器图标显示在哪）
        registry.addWorkstation(MACHINE_CATEGORY, EmiStack.of(Items.REDSTONE_BLOCK)); // 自定义图标机器

        for (Map.Entry<String, List<Item>> recipe : ore.entrySet()) {
            for (Item item : recipe.getValue()) {
                registry.addRecipe(new MachineEmiRecipe(new MachineRecipe(Items.AIR, item)));
            }
        }
    }
}

