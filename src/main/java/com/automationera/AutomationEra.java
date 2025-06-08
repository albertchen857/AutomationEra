package com.automationera;

import com.automationera.advance.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.HashSet;

public class AutomationEra implements ModInitializer {
	public static final String MOD_ID = "automationera";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final FullStackCriterion FULL_STACK_CRITERION = new FullStackCriterion();
	public static final FullShulkerBoxCriterion FULL_SHULKER_BOX_CRITERION = new FullShulkerBoxCriterion();
	public static final TradingPostCriterion TRADING_POST_CRITERION = new TradingPostCriterion();
	public static final AboveNetherCriterion ABOVE_NETHER_CRITERION = new AboveNetherCriterion();
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
	private static final Set<Item> Furnace = Set.of(Items.GLASS,Items.STONE);
	private static final Set<Item> Mushroom = Set.of(
			Items.BROWN_MUSHROOM,
			Items.RED_MUSHROOM,
			Items.RED_MUSHROOM_BLOCK,
			Items.BROWN_MUSHROOM_BLOCK
	);
	private static final Set<Item> Concrete = Set.of( // 混凝土农场
			Items.WHITE_CONCRETE,
			Items.ORANGE_CONCRETE,
			Items.MAGENTA_CONCRETE,
			Items.LIGHT_BLUE_CONCRETE,
			Items.YELLOW_CONCRETE,
			Items.LIME_CONCRETE,
			Items.PINK_CONCRETE,
			Items.GRAY_CONCRETE,
			Items.LIGHT_GRAY_CONCRETE,
			Items.CYAN_CONCRETE,
			Items.PURPLE_CONCRETE,
			Items.BLUE_CONCRETE,
			Items.BROWN_CONCRETE,
			Items.GREEN_CONCRETE,
			Items.RED_CONCRETE,
			Items.BLACK_CONCRETE
	);
	private static final Set<Item> Sniffer = Set.of( // 嗅探兽农场
			Items.TORCHFLOWER_SEEDS,
			Items.PITCHER_POD
	);
	private static final Set<Item> Pigtrade = Set.of(Items.QUARTZ,Items.CRYING_OBSIDIAN);
	private static final Set<Item> Ripening = Set.of( // 催熟机
			Items.COCOA_BEANS,
			Items.GLOW_BERRIES,
			Items.VINE,
			Items.GLOW_LICHEN,
			Items.BIG_DRIPLEAF,
			Items.HANGING_ROOTS,
			Items.PINK_PETALS,
			Items.ROSE_BUSH,
			Items.LILAC,
			Items.PEONY,
			Items.SUNFLOWER
	);

	private static final Map<UUID, Integer> tradeCounts = new HashMap<>();
	private static final Set<UUID> aboveNetherPlayers = new HashSet<>();

	@Override
	public void onInitialize() {
		Criteria.register(FULL_STACK_CRITERION);
		PlacedBlockInNetherCriterion.register();
		Criteria.register(FULL_SHULKER_BOX_CRITERION);
		Criteria.register(TRADING_POST_CRITERION);
		Criteria.register(ABOVE_NETHER_CRITERION);
		CustomAdvancementCriteria.register();

		// 添加每刻检查
		ServerTickEvents.START_SERVER_TICK.register(server -> {
			// 降低检查频率到每100个游戏刻
			if (server.getTicks() % 100 == 0) {
				for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
					LOGGER.info("Checking player {} for advancements", player.getName().getString());
					
					// 检查单物品成就
					checkPlayerStacks(player, Items.COBBLESTONE, 9);
					checkPlayerStacks(player, Items.ICE, 9);
					checkPlayerStacks(player, Items.BASALT, 9);
					checkPlayerStacks(player, Items.DRIPSTONE_BLOCK, 9);
					checkPlayerStacks(player, Items.GLASS, 4);
					checkPlayerStacks(player, Items.SMOOTH_STONE, 27);
					checkPlayerStacks(player, Items.IRON_INGOT, 9);
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
					checkAnyItemStacks(player, LOG_ITEMS, 27);
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
					checkAnyItemStacks(player, Furnace, 9);
					checkAnyItemStacks(player, Mushroom, 9);
					checkAnyItemStacks(player, Concrete, 9);
					checkAnyItemStacks(player, Sniffer, 9);
					checkAnyItemStacks(player, Pigtrade, 9);
					checkAnyItemStacks(player, Ripening, 9);

					// 检查潜影盒成就
					LOGGER.info("Triggering shulker box check for player {}", player.getName().getString());
					FULL_SHULKER_BOX_CRITERION.trigger(player);

					// 检查地狱上层成就
					if (player.getWorld().getRegistryKey().equals(World.NETHER)) {
						BlockPos pos = player.getBlockPos();
						LOGGER.info("Player {} in Nether at Y: {}", player.getName().getString(), pos.getY());
						
						// 确保玩家真的在地狱上层，并且已经在那里待了一段时间
						if (pos.getY() > 127 && !aboveNetherPlayers.contains(player.getUuid())) {
							// 确保玩家不是刚进入世界
							if (player.getWorld().getTime() - player.getWorld().getTimeOfDay() > 200) {
								ABOVE_NETHER_CRITERION.trigger(player);
								aboveNetherPlayers.add(player.getUuid());
								LOGGER.info("Triggered above nether advancement for player {}", player.getName().getString());
							}
						}
					}
				}
			}
		});

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!world.isClient && entity instanceof VillagerEntity villager) {
				if (player instanceof ServerPlayerEntity serverPlayer) {
					LOGGER.info("Player {} traded with villager", player.getName().getString());
					
					// 确保玩家不是刚进入世界
					if (world.getTime() - world.getTimeOfDay() > 200) {
						tradeCounts.merge(player.getUuid(), 1, Integer::sum);
						int tradeCount = tradeCounts.get(player.getUuid());
						LOGGER.info("Player {} trade count: {}", player.getName().getString(), tradeCount);
						
						if (tradeCount >= 10) {
							TRADING_POST_CRITERION.trigger(serverPlayer);
							LOGGER.info("Triggered trading post advancement for player {}", player.getName().getString());
						}
					}
				}
			}
			return ActionResult.PASS;
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