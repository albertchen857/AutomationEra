
package com.automationera.advance;

import com.automationera.AutomationEra;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public class AdvancementTriggerHandler {
    public static final Item IRON_INGOT = Items.IRON_INGOT;
    public static final int FULL_STACK_SIZE = 64; // 整组大小

    public static void checkFullStacks(ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        int fullStackCount = 0;

        // 检查所有物品栏
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (!stack.isEmpty() &&
                    stack.getItem() == Items.IRON_INGOT &&
                    stack.getCount() == FULL_STACK_SIZE) {
                fullStackCount++;
            }
        }

        // 触发自定义条件（如果至少有4组整铁锭）
        if (fullStackCount >= 4) {
            AutomationEra.FULL_STACK_CRITERION.trigger(player, fullStackCount);
        }
    }
}
