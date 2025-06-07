package com.automationera;

import com.automationera.advance.PlacedBlockInNetherCriterion;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
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
			Items.CARROT
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
	private static final Set<Item> SQUID = Set.of(
			Items.INK_SAC,
			Items.GLOW_INK_SAC
	);

	@Override
	public void onInitialize() {
		Criteria.register(FULL_STACK_CRITERION);
		PlacedBlockInNetherCriterion.register();
		
		// 添加每刻检查
		ServerTickEvents.START_SERVER_TICK.register(server -> {
			if (server.getTicks() % 20 == 0) {
				for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
					// 检查单物品成就
					checkPlayerStacks(player, Items.COBBLESTONE, 9);
					checkPlayerStacks(player, Items.ICE, 9);
					checkPlayerStacks(player, Items.BASALT, 9);
					checkPlayerStacks(player, Items.DRIPSTONE_BLOCK, 9);
					checkPlayerStacks(player, Items.GLASS, 4);
					checkPlayerStacks(player, Items.SMOOTH_STONE, 27);
					checkPlayerStacks(player, Items.IRON_INGOT, 4);
					checkPlayerStacks(player, Items.AMETHYST_SHARD, 9);
					checkPlayerStacks(player, Items.SAND, 9);
					checkPlayerStacks(player, Items.CLAY, 9);
					checkPlayerStacks(player, Items.DIRT, 27);
					checkPlayerStacks(player, Items.BONE_MEAL, 9);
					checkPlayerStacks(player, Items.BONE, 4);
					checkPlayerStacks(player, Items.SHULKER_SHELL, 9);
					checkPlayerStacks(player, Items.REDSTONE, 9);
					checkPlayerStacks(player, Items.SLIME_BALL, 9);
					checkPlayerStacks(player, Items.BLAZE_ROD, 9);
					checkPlayerStacks(player, Items.ENDER_PEARL, 9);
					checkPlayerStacks(player, Items.GOLD_INGOT, 9);
					checkPlayerStacks(player, Items.GOLD_INGOT, 27);
					checkPlayerStacks(player, Items.WITHER_ROSE, 9);
					checkPlayerStacks(player, Items.WITHER_SKELETON_SKULL, 4);
					checkPlayerStacks(player, Items.NETHER_STAR, 9);
					checkPlayerStacks(player, Items.PRISMARINE_SHARD, 9);
					checkPlayerStacks(player, Items.WHEAT, 9);
					checkPlayerStacks(player, Items.SUGAR_CANE, 4);
					checkPlayerStacks(player, Items.CACTUS, 4);
					checkPlayerStacks(player, Items.KELP, 9);
					checkPlayerStacks(player, Items.BAMBOO, 4);
					checkPlayerStacks(player, Items.PUMPKIN, 4);
					checkPlayerStacks(player, Items.MELON, 4);
					checkPlayerStacks(player, Items.NETHER_WART, 9);
					checkPlayerStacks(player, Items.SWEET_BERRIES, 9);
					checkPlayerStacks(player, Items.SEA_PICKLE, 4);
					checkPlayerStacks(player, Items.TURTLE_EGG, 4);
					checkPlayerStacks(player, Items.SNOW_BLOCK, 9);
					checkPlayerStacks(player, Items.COOKED_PORKCHOP, 9);
					checkPlayerStacks(player, Items.COOKED_CHICKEN, 4);
					checkPlayerStacks(player, Items.COOKED_BEEF, 4);
					checkPlayerStacks(player, Items.HONEY_BLOCK, 4);
					checkPlayerStacks(player, Items.EMERALD, 9);
					checkPlayerStacks(player, Items.BEDROCK, 1);
					
					// 检查物品集合成就
					checkAnyItemStacks(player, LOG_ITEMS, 9);
					checkAnyItemStacks(player, CROP_ITEMS, 4);
					checkAnyItemStacks(player, RAIL_ITEMS, 9);
					checkAnyItemStacks(player, CORAL_FAN, 4);
					checkAnyItemStacks(player, FLOWER, 4);
					checkAnyItemStacks(player, COPPER_BLOCKS, 2);
					checkAnyItemStacks(player, LEAVES, 9);
					checkAnyItemStacks(player, NETHER_WOOD, 9);
					checkAnyItemStacks(player, WOOL, 4);
					checkAnyItemStacks(player, SQUID, 4);
					checkAnyItemStacks(player, FROG_LIGHT, 9);
				}
			}
		});
	}

	private void checkPlayerStacks(ServerPlayerEntity player, Item item, int requiredStacks) {
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
				FULL_STACK_CRITERION.trigger(player, items, requiredStacks);
				return; // 一旦找到满足条件的物品就触发成就并返回
			}
		}
	}
}