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
	private static final Set<Item> CORAL_FAN = Set.of(
			Items.BRAIN_CORAL_FAN,
			Items.BUBBLE_CORAL_FAN,
			Items.FIRE_CORAL_FAN,
			Items.HORN_CORAL_FAN,
			Items.TUBE_CORAL_FAN
	);
	private static final Set<Item> FLOWER = Set.of(
			Items.DANDELION,
			Items.POPPY,
			Items.BLUE_ORCHID,
			Items.ALLIUM,
			Items.AZURE_BLUET,
			Items.CORNFLOWER,
			Items.LILY_OF_THE_VALLEY,
			Items.ORANGE_TULIP,
			Items.PINK_TULIP,
			Items.RED_TULIP,
			Items.WHITE_TULIP,
			Items.WITHER_ROSE
	);
	private static final Set<Item> COPPER_BLOCKS = Set.of(
			Items.EXPOSED_COPPER,
			Items.WEATHERED_COPPER,
			Items.OXIDIZED_COPPER
	);
	private static final Set<Item> LEAVES = Set.of(
			Items.OAK_LEAVES,
			Items.SPRUCE_LEAVES,
			Items.BIRCH_LEAVES,
			Items.JUNGLE_LEAVES,
			Items.ACACIA_LEAVES,
			Items.DARK_OAK_LEAVES,
			Items.MANGROVE_LEAVES,
			Items.CHERRY_LEAVES,
			Items.AZALEA_LEAVES,
			Items.FLOWERING_AZALEA_LEAVES
	);
	private static final Set<Item> NETHER_WOOD = Set.of(
			Items.CRIMSON_STEM,
			Items.WARPED_STEM
	);
	private static final Set<Item> WOOL = Set.of(
			Items.WHITE_WOOL,
			Items.ORANGE_WOOL,
			Items.MAGENTA_WOOL,
			Items.LIGHT_BLUE_WOOL,
			Items.YELLOW_WOOL,
			Items.LIME_WOOL,
			Items.PINK_WOOL,
			Items.GRAY_WOOL,
			Items.LIGHT_GRAY_WOOL,
			Items.CYAN_WOOL,
			Items.PURPLE_WOOL,
			Items.BLUE_WOOL,
			Items.BROWN_WOOL,
			Items.GREEN_WOOL,
			Items.RED_WOOL,
			Items.BLACK_WOOL
	);
	private static final Set<Item> FROG_LIGHT = Set.of(
			Items.OCHRE_FROGLIGHT,
			Items.PEARLESCENT_FROGLIGHT,
			Items.VERDANT_FROGLIGHT
	);

	@Override
	public void onInitialize() {
		Criteria.register(FULL_STACK_CRITERION);
		// 添加每刻检查
		ServerTickEvents.START_SERVER_TICK.register(server -> {
			if (server.getTicks() % 20 == 0) {
				for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
					// 0.1.0
					checkPlayerStacks(player, Items.COBBLESTONE, 9); // 刷石机
					checkPlayerStacks(player, Items.BONE, 4); // 刷怪塔
					checkPlayerStacks(player, Items.ROTTEN_FLESH, 4); // 刷怪塔
					checkPlayerStacks(player, Items.GUNPOWDER, 4); // 刷怪塔
					checkPlayerStacks(player, Items.SAND, 9); // 刷沙机
					checkPlayerStacks(player, Items.SHULKER_SHELL, 9); // 潜影贝农场
					checkPlayerStacks(player, Items.REDSTONE, 9); // 女巫塔
					checkPlayerStacks(player, Items.SLIME_BALL, 9); // 史莱姆农场
					checkPlayerStacks(player, Items.IRON_INGOT, 4); // 刷铁机
					checkPlayerStacks(player, Items.STONE, 4); // 熔炉组
					checkPlayerStacks(player, Items.GLASS, 4); // 熔炉组
					checkPlayerStacks(player, Items.SMOOTH_STONE, 27); // 大型熔炉组
					checkPlayerStacks(player, Items.BONE_MEAL, 9); // 骨粉机
					checkPlayerStacks(player, Items.PORKCHOP, 9); // 疣猪塔
					checkPlayerStacks(player, Items.TRIPWIRE_HOOK, 9); // 钩子机
					checkPlayerStacks(player, Items.STRING, 9); // 刷线机
					checkPlayerStacks(player, Items.EMERALD, 9); // 袭击塔
					checkPlayerStacks(player, Items.BAMBOO, 4); // 竹子机
					checkPlayerStacks(player, Items.SUGAR_CANE, 4); // 甘蔗机
					checkPlayerStacks(player, Items.PUMPKIN, 4); // 南瓜机
					checkPlayerStacks(player, Items.MELON, 4); // 西瓜机
					checkPlayerStacks(player, Items.BLAZE_ROD, 9); // 烈焰人农场
					checkPlayerStacks(player, Items.ENDER_PEARL, 9); // 小黑塔
					checkPlayerStacks(player, Items.GOLD_INGOT, 9); // 猪人塔
					checkPlayerStacks(player, Items.GOLD_INGOT, 27); // 双维度猪人塔
					checkPlayerStacks(player, Items.WITHER_ROSE, 9); // 凋零玫瑰农场
					checkPlayerStacks(player, Items.WITHER_SKELETON_SKULL, 4); // 凋零骷髅农场
					checkPlayerStacks(player, Items.NETHER_STAR, 4); // 杀凋机
					checkPlayerStacks(player, Items.QUARTZ, 4); // 猪灵交易所
					checkPlayerStacks(player, Items.OBSIDIAN, 4); // 猪灵交易所
					checkPlayerStacks(player, Items.COAL, 4); // 凋零骷髅农场
					// Multiple triggers
					checkAnyItemStacks(player, LOG_ITEMS, 9);
					checkAnyItemStacks(player, LOG_ITEMS, 27);
					checkAnyItemStacks(player, CROP_ITEMS, 4);
					checkAnyItemStacks(player, RAIL_ITEMS, 9);

					// 0.1.1
					checkPlayerStacks(player, Items.COOKED_CHICKEN, 4); // 熔岩烤鸡
					checkPlayerStacks(player, Items.EGG, 16); // 熔岩烤鸡
					checkPlayerStacks(player, Items.COOKED_BEEF, 4); // 烤牛机
					checkPlayerStacks(player, Items.CACTUS, 4); // 仙人掌机
					checkPlayerStacks(player, Items.KELP, 9); // 海带机
					checkPlayerStacks(player, Items.HONEY_BLOCK, 4); // 蜜蜂农场
					checkPlayerStacks(player, Items.HONEYCOMB, 2); // 蜜蜂农场
					checkPlayerStacks(player, Items.WHEAT, 9); // 小麦自动农场
					checkPlayerStacks(player, Items.NETHER_WART, 9); // 地狱疣农场
					checkPlayerStacks(player, Items.SWEET_BERRIES, 9); // 甜浆果农场
					checkPlayerStacks(player, Items.SEA_PICKLE, 4); // 海泡菜机
					checkAnyItemStacks(player, CORAL_FAN, 4); // 珊瑚机
					checkAnyItemStacks(player, FLOWER, 4); // 刷花机

					// 0.1.2
					// 刷石机分支
					checkPlayerStacks(player, Items.EXPOSED_COPPER, 2); // 晒铜机
					checkPlayerStacks(player, Items.WEATHERED_COPPER, 2); // 晒铜机
					checkPlayerStacks(player, Items.OXIDIZED_COPPER, 2); // 晒铜机
					checkPlayerStacks(player, Items.ICE, 9); // 刷冰机
					checkPlayerStacks(player, Items.BASALT, 9); // 玄武岩刷石机
					checkPlayerStacks(player, Items.DRIPSTONE_BLOCK, 9); // 滴水石头锥农场

					// 熔炉组分支
					checkPlayerStacks(player, Items.CRAFTING_TABLE, 4); // 红石原件工厂
					checkPlayerStacks(player, Items.HOPPER, 4); // 红石原件工厂
					checkPlayerStacks(player, Items.DISPENSER, 4); // 红石原件工厂

					// 刷铁机分支
					checkPlayerStacks(player, Items.AMETHYST_SHARD, 9); // 紫水晶农场

					// 刷沙机分支
					checkPlayerStacks(player, Items.CLAY, 9); // 粘土机
					checkPlayerStacks(player, Items.DIRT, 27); // 泥土机

					// 骨粉机分支
					checkAnyItemStacks(player, LEAVES, 9); // 树叶机
					checkAnyItemStacks(player, NETHER_WOOD, 9); // 菌柄树厂

					// 畜牧业分支
					checkPlayerStacks(player, Items.TURTLE_EGG, 4); // 海龟农场
					checkPlayerStacks(player, Items.SNOW_BLOCK, 9); // 刷雪机
					checkAnyItemStacks(player, WOOL, 4); // 羊毛农场
					checkPlayerStacks(player, Items.INK_SAC, 4); // 鱿鱼农场
					checkPlayerStacks(player, Items.GLOW_INK_SAC, 4); // 鱿鱼农场
					checkPlayerStacks(player, Items.TORCHFLOWER_SEEDS, 4); // 嗅探兽农场
					checkPlayerStacks(player, Items.PITCHER_POD, 4); // 嗅探兽农场

					// 自动农场分支
					// 催熟机需要特殊处理，这里先留空

					// 烈焰人农场分支
					checkPlayerStacks(player, Items.MAGMA_CREAM, 9); // 岩浆怪农场
					// 炼药机需要特殊处理，这里先留空
					checkAnyItemStacks(player, FROG_LIGHT, 9); // 蛙鸣灯农场
					checkPlayerStacks(player, Items.GHAST_TEAR, 4); // 恶魂农场

					// 刷怪塔分支
					checkPlayerStacks(player, Items.PRISMARINE_SHARD, 4); // 守卫者农场
					checkPlayerStacks(player, Items.ROTTEN_FLESH, 4); // 溺尸塔
					checkPlayerStacks(player, Items.COPPER_INGOT, 4); // 溺尸塔
					checkPlayerStacks(player, Items.TRIDENT, 1); // 溺尸塔
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
}