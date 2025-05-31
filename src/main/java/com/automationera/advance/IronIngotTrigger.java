package com.automationera.advance;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class IronIngotTrigger extends BaseStackTrigger {
    @Override
    public Item getTargetItem() {
        return Items.IRON_INGOT;
    }

    @Override
    public int getRequiredStacks() {
        return 4; // 需要4组
    }

    @Override
    public int getRewardXP() {
        return 1000;
    }

    @Override
    public String getAdvancementID() {
        return "iron_warehouse";
    }
}
