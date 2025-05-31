package com.automationera.advance;

import com.automationera.AutomationEra;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class BaseStackTrigger {
    // 抽象方法 - 子类必须实现这些来定义具体行为
    public abstract Item getTargetItem();
    public abstract int getRequiredStacks();
    public abstract int getRewardXP();
    public abstract String getAdvancementID();

    // 通用检查逻辑
    public void checkAndTrigger(ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        int fullStackCount = 0;
        int stackSize = getTargetItem().getMaxCount();

        // 检查所有物品栏
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() &&
                    stack.getItem() == getTargetItem() &&
                    stack.getCount() == stackSize) {
                fullStackCount++;
            }
        }

        // 如果满足条件则触发
        if (fullStackCount >= getRequiredStacks()) {
            AutomationEra.FULL_STACK_CRITERION.trigger(player, fullStackCount);
        }
    }
}


