package com.automationera.advance;

import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface StackTriggerTemplate {
    /**
     * 检查玩家是否满足进度条件
     *
     * @param player 玩家实体
     * @return 如果满足条件则返回true
     */
    boolean check(ServerPlayerEntity player);

    static AdvancementCriterion IronTrigger() {
        return new AdvancementCriterion() {
            public boolean check(ServerPlayerEntity player) {
                int count = 0;
                for (var stack : player.getInventory().main) {
                    if (!stack.isEmpty() &&
                            stack.getItem() == getTargetItem() &&
                            stack.getCount() == 64) {
                        count++;
                    }
                }
                return count >= getRequiredStacks();
            }

            public Item getTargetItem() {
                return Items.IRON_INGOT;
            }

            public int getRequiredStacks() {
                return 4;
            }
        };
    }

    static StackTriggerTemplate StoneTrigger() {
        return new StackTriggerTemplate() {
            @Override
            public boolean check(ServerPlayerEntity player) {
                int count = 0;
                for (var stack : player.getInventory().main) {
                    if (!stack.isEmpty() &&
                            stack.getItem() == getTargetItem() &&
                            stack.getCount() == 64) {
                        count++;
                    }
                }
                return count >= getRequiredStacks();
            }

            public Item getTargetItem() {
                return Items.COBBLESTONE;
            }

            public String getAdvancementID() {
                return "stonefarm";
            }

            public int getRequiredStacks() {
                return 9;
            }
        };
    }
}


