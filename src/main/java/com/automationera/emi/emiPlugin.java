package com.automationera.emi;

import com.automationera.OutputRecipe;
import com.automationera.emi.category.MachineRecipeCategory;
import com.automationera.emi.display.MachineEmiRecipe;
import com.automationera.emi.recipe.MachineRecipe;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public class emiPlugin implements EmiPlugin {
    public static final Identifier MACHINE_CATEGORY_ID = new Identifier("automationera", "machine");
    public static final EmiRecipeCategory MACHINE_CATEGORY = new MachineRecipeCategory();
    public static final Map<String, List<Item>> ore = new OutputRecipe().OutputdRecipy();

    @Override
    public void register(EmiRegistry registry) {
        // 注册自定义的机器配方分类
        registry.addCategory(MACHINE_CATEGORY);
        // 为 ore 映射中的每个机器条目注册一条配方，展示该机器的所有产物
        for (Map.Entry<String, List<Item>> entry : ore.entrySet()) {
            String key = entry.getKey();
            List<Item> outputs = entry.getValue();
            if (outputs == null || outputs.isEmpty()) {
                // 若该机器没有产物，则跳过注册
                continue;
            }
            // 使用第一个产物作为代表输出（确保列表非空后第一个元素存在）
            Item representativeOutput = outputs.get(0);
            MachineRecipe machineRecipe = new MachineRecipe(Items.AIR, representativeOutput);
            // 创建该机器对应的 EMI 配方，传入机器标识和全部产物列表
            registry.addRecipe(new MachineEmiRecipe(machineRecipe, key, outputs));
        }
    }
}
