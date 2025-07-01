package com.automationera.emi;

import com.automationera.OutputRecipe;
import com.automationera.emi.category.MachineRecipeCategory;
import com.automationera.emi.category.TradeRecipeCategory;
import com.automationera.emi.display.MachineEmiRecipe;
import com.automationera.emi.display.TradeEmiRecipe;
import com.automationera.emi.recipe.MachineRecipe;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class emiPlugin implements EmiPlugin {
    public static final EmiRecipeCategory MACHINE_CATEGORY = new MachineRecipeCategory();
    public static final EmiRecipeCategory TRADE_CATEGORY = new TradeRecipeCategory();
    public static final Map<String, List<List<Item>>> ore = new OutputRecipe().OutputdRecipy();
    public static final Map<String, List<List<Item>>> trade = new OutputRecipe().TradeRecipy();

    @Override
    public void register(EmiRegistry registry) {
        // 注册自定义的机器配方分类
        registry.addCategory(MACHINE_CATEGORY);
        registry.addCategory(TRADE_CATEGORY);
        registry.addRecipe(new MachineEmiRecipe(new MachineRecipe(Items.REDSTONE, Items.REDSTONE), "circuit", List.of(List.of(Items.REDSTONE),List.of(Items.REDSTONE_BLOCK))));
        // 为 ore 映射中的每个机器条目注册一条配方，展示该机器的所有产物
        for (Map.Entry<String, List<List<Item>>> entry : ore.entrySet()) {
            String key = entry.getKey();
            List<Item> outputs = entry.getValue().get(0);
            List<Item> inputs = entry.getValue().get(1);
            if (!outputs.isEmpty()) {
                registry.addRecipe(new MachineEmiRecipe(new MachineRecipe(Items.AIR, outputs.get(0)), key, entry.getValue()));
            }
            /*if (!inputs.isEmpty()) {
                registry.addRecipe(new MachineEmiRecipe(new MachineRecipe(inputs.get(0), Items.AIR), key, entry.getValue()));
            }*/
        }
        //trade register
        for (Map.Entry<String, List<List<Item>>> entry : trade.entrySet()) {
            String key = entry.getKey();
            List<Item> o1 = entry.getValue().get(0);
            List<Item> o2 = entry.getValue().get(0);
            if (!o1.isEmpty()) {
                registry.addRecipe(new TradeEmiRecipe(new MachineRecipe(Items.EMERALD, o1.get(0)), key+"_in", entry.getValue()));
            }
            if (!o2.isEmpty()) {
                registry.addRecipe(new TradeEmiRecipe(new MachineRecipe(o2.get(0), Items.EMERALD), key+"_out", entry.getValue()));
            }
        }
    }
}
