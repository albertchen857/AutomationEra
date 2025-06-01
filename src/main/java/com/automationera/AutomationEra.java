package com.automationera;

import com.automationera.FullStackCriterion;
import com.automationera.advance.StackTriggerTemplate;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class AutomationEra implements ModInitializer {
	public static final String MOD_ID = "automationera";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final FullStackCriterion FULL_STACK_CRITERION = new FullStackCriterion();
	private static final Set<Item> LOG_ITEMS = Set.of(
			Items.OAK_LOG,
			Items.SPRUCE_LOG,
			Items.BIRCH_LOG,
			Items.JUNGLE_LOG,
			Items.ACACIA_LOG,
			Items.DARK_OAK_LOG,
			Items.MANGROVE_LOG,
			Items.CHERRY_LOG,
			Items.BAMBOO_BLOCK, // 竹子块作为替代原木
			Items.CRIMSON_STEM,
			Items.WARPED_STEM
	);
	private static final Set<Item> CROP_ITEMS = Set.of(
			Items.WHEAT,
			Items.POTATO,
			Items.CARROT,
			Items.BEETROOT
	);
	private static final Set<Item> RAIL_ITEMS = Set.of(
			Items.RAIL,           // 普通铁轨
			Items.POWERED_RAIL,   // 动力铁轨
			Items.DETECTOR_RAIL,  // 探测铁轨
			Items.ACTIVATOR_RAIL  // 激活铁轨
	);


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Criteria.register(FULL_STACK_CRITERION);
		// 添加每刻检查
		ServerTickEvents.START_SERVER_TICK.register(server -> {
			if (server.getTicks() % 20 == 0) {
				for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
					// 普通trigger类
					checkPlayerStacks(player, Items.COBBLESTONE, 9); // 刷石机
					checkPlayerStacks(player, Items.BONE, 9); // 刷怪塔
					checkPlayerStacks(player, Items.SAND, 9); // 刷沙机
					checkPlayerStacks(player, Items.SHULKER_SHELL, 27); // 潜影贝农场
					checkPlayerStacks(player, Items.REDSTONE, 27); // 女巫塔
					checkPlayerStacks(player, Items.SLIME_BALL, 27); // 史莱姆农场
					checkPlayerStacks(player, Items.IRON_INGOT, 4); // 刷铁机
					checkPlayerStacks(player, Items.COBBLESTONE, 9); // 熔炉组
					checkPlayerStacks(player, Items.SMOOTH_STONE, 27); // 大型熔炉组
					checkPlayerStacks(player, Items.BONE_MEAL, 27); // 骨粉机
					checkPlayerStacks(player, Items.PORKCHOP, 9); // 疣猪塔
					checkPlayerStacks(player, Items.TRIPWIRE_HOOK, 27); // 钩子机
					checkPlayerStacks(player, Items.STRING, 27); // 刷线机
					checkPlayerStacks(player, Items.EMERALD, 9); // 袭击塔
					checkPlayerStacks(player, Items.BAMBOO, 4); // 竹子机
					checkPlayerStacks(player, Items.SUGAR_CANE, 4); // 甘蔗机
					checkPlayerStacks(player, Items.PUMPKIN, 4); // 南瓜机
					checkPlayerStacks(player, Items.MELON, 4); // 西瓜机
					checkPlayerStacks(player, Items.BLAZE_ROD, 27); // 烈焰人农场
					checkPlayerStacks(player, Items.ENDER_PEARL, 27); // 小黑塔
					checkPlayerStacks(player, Items.GOLD_INGOT, 27); // 猪人塔/双维度猪人塔
					checkPlayerStacks(player, Items.WITHER_ROSE, 9); // 凋零玫瑰农场
					checkPlayerStacks(player, Items.WITHER_SKELETON_SKULL, 4); // 凋零骷髅农场
					checkPlayerStacks(player, Items.NETHER_STAR, 4); // 杀凋机
					checkPlayerStacks(player, Items.QUARTZ, 4); // 猪灵交易所
					checkPlayerStacks(player, Items.OBSIDIAN, 4); // 猪灵交易所
					checkPlayerStacks(player, Items.COAL, 4); // 凋零骷髅农场
					// 附魔书只需获得一次即可
					// 任意原木9组（树厂）
					checkAnyItemStacks(player, LOG_ITEMS, 9);
					// 任意原木27组（全树种）
					checkAnyItemStacks(player, LOG_ITEMS, 27);
					// 任意农作物4组（自动农场）
					checkAnyItemStacks(player, CROP_ITEMS, 4);
					// 铁轨每种4组（铁轨机）
					checkAllItemsEachStacks(player, RAIL_ITEMS, 4);
				}
			}
		});
	}

	private void checkPlayerStacks(ServerPlayerEntity player, net.minecraft.item.Item item, int requiredStacks) {
		int fullStackCount = 0;
		int stackSize = item.getMaxCount();

		// 扫描玩家背包
		for (var stack : player.getInventory().main) {
			if (!stack.isEmpty() &&
					stack.getItem() == item &&
					stack.getCount() == stackSize) {
				fullStackCount++;
			}
		}

		// 如果满足条件则触发进度
		if (fullStackCount >= requiredStacks) {
			FULL_STACK_CRITERION.trigger(player, item, requiredStacks);
		}
	}

    // 检查任意集合内物品达到指定组数
    private void checkAnyItemStacks(ServerPlayerEntity player, Set<Item> items, int requiredStacks) {
        for (Item item : items) {
            int fullStackCount = 0;
            int stackSize = item.getMaxCount();
            for (var stack : player.getInventory().main) {
                if (!stack.isEmpty() && stack.getItem() == item && stack.getCount() == stackSize) {
                    fullStackCount++;
                }
            }
            if (fullStackCount >= requiredStacks) {
                FULL_STACK_CRITERION.trigger(player, item, requiredStacks);
                break;
            }
        }
    }

    // 检查集合内所有物品都达到指定组数
    private void checkAllItemsEachStacks(ServerPlayerEntity player, Set<Item> items, int requiredStacks) {
        boolean allMet = true;
        for (Item item : items) {
            int fullStackCount = 0;
            int stackSize = item.getMaxCount();
            for (var stack : player.getInventory().main) {
                if (!stack.isEmpty() && stack.getItem() == item && stack.getCount() == stackSize) {
                    fullStackCount++;
                }
            }
            if (fullStackCount < requiredStacks) {
                allMet = false;
                break;
            }
        }
        if (allMet) {
            // 只用第一个物品做trigger
            FULL_STACK_CRITERION.trigger(player, items.iterator().next(), requiredStacks);
        }
    }
}