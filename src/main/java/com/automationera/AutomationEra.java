package com.automationera;

import com.automationera.advance.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class AutomationEra implements ModInitializer {
	public static final String MOD_ID = "automationera";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final FullStackCriterion FULL_STACK_CRITERION = new FullStackCriterion();
	public static final FullShulkerBoxCriterion FULL_SHULKER_BOX_CRITERION = new FullShulkerBoxCriterion();
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
	private static final Map<UUID, Long> netherEntryTimes = new HashMap<>();
	private static final Set<UUID> aboveNetherPlayers = new HashSet<>();

	private final long[] tickTimes = new long[100];
	private int tickIndex = 0;
	private long lastTickStartTime;
	private long tickDuration;

	private int[] lastpos = new int[]{0,0};
	private boolean afk = false;
	private int afkperiod = 0;
	private int furnace = 0;
	private int Observer = 0;

	@Override
	public void onInitialize() {
		Criteria.register(FULL_STACK_CRITERION);
		PlacedBlockInNetherCriterion.register();
		Criteria.register(FULL_SHULKER_BOX_CRITERION);

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (afk){
				MinecraftServer server = newPlayer.getServer();
				Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:afkdie"));
				if (adv != null) {
					newPlayer.getAdvancementTracker().grantCriterion(adv, "afkdie");
				}
			}
		});

		// 添加每刻检查
		ServerTickEvents.START_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				lastTickStartTime = System.nanoTime();
				 if (server.getTicks() % 36000 == 0){ //10min
					 BlockPos pos = player.getBlockPos();
					 //LOGGER.info("afk {}/{}/{}",lastpos, new int[]{pos.getX(), pos.getZ()},Arrays.equals(lastpos, new int[]{pos.getX(), pos.getZ()}));
					 if (Arrays.equals(lastpos, new int[]{pos.getX(), pos.getZ()})){
						 Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:afk"));
						 if (adv != null) {
							 player.getAdvancementTracker().grantCriterion(adv, "afk");
						 }
						 MinecraftClient.getInstance().player.sendMessage(Text.literal("You already AFK "+(afkperiod*0.5)+" Hours"));
						 afkperiod+=1;
						 afk = true;
					 }else{
						 afkperiod = 0;
						 afk = false;
					 }
				 }else if (server.getTicks() % 200 == 0) {
					 ChunkPos chunkPos = player.getChunkPos();
					 if (isChunkAirSpace(player.getWorld(), chunkPos, -64)){
						 Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:realperimeter"));
						 if (adv != null) {
							 player.getAdvancementTracker().grantCriterion(adv, "realperimeter");
						 }
				 	 }else if (isChunkAirSpace(player.getWorld(), chunkPos, -59)) {
						 if (player.getWorld().getRegistryKey().equals(World.OVERWORLD)) {
							 Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:perimeter"));
							 if (adv != null) {
								 player.getAdvancementTracker().grantCriterion(adv, "perimeter");
							 }
						 } else if (player.getWorld().getRegistryKey().equals(World.NETHER)) {
							 Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:netherperimeter"));
							 if (adv != null) {
								 player.getAdvancementTracker().grantCriterion(adv, "netherperimeter");
							 }
						 }
					 }else{
						 ServerWorld world = player.getServerWorld();
						 boolean peaceful = world.getDifficulty() == Difficulty.PEACEFUL;
						 int mobCount = world.getEntitiesByClass(MobEntity.class, new Box(player.getBlockPos()).expand(64), Entity::isAlive).size();
						 long timeOfDay = world.getTimeOfDay() % 24000;
						 if (!peaceful && mobCount == 0 && timeOfDay >= 13000 && timeOfDay <= 23000) {
							 Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:fakepeaceful"));
							 if (adv != null) {
								 player.getAdvancementTracker().grantCriterion(adv, "fakepeaceful");
							 }
						 }
					 }
				 }else if (server.getTicks() % 20 == 0) {
					int online = server.getCurrentPlayerCount();
					//LOGGER.info("Checking player {} for advancements", player.getName().getString());
					
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
					//LOGGER.info("Triggering shulker box check for player {}", player.getName().getString());
					FULL_SHULKER_BOX_CRITERION.trigger(player);

					 BlockPos pos = player.getBlockPos();
					// 检查地狱上层成就
					if (player.getWorld().getRegistryKey().equals(World.NETHER)) {
						// 确保玩家真的在地狱上层，并且已经在那里待了一段时间
						if (pos.getY() > 127) {
							LOGGER.info("Player {} in Nether at Y: {}", player.getName().getString(), pos.getY());
							Long entryTime = netherEntryTimes.get(player.getUuid());
							// 检查玩家是否已经获得过这个成就
							Advancement adv = player.getServer().getAdvancementLoader().get(new Identifier("minecraft:abovenether"));
							//LOGGER.info("ADV:{}",!player.getAdvancementTracker().getProgress(adv).isDone());
							if (!player.getAdvancementTracker().getProgress(adv).isDone()) {
								//ABOVE_NETHER_CRITERION.trigger(player);
								player.getAdvancementTracker().grantCriterion(adv, "above_nether");
								//aboveNetherPlayers.add(player.getUuid());
								LOGGER.info("Triggered above nether advancement for player {}", player.getName().getString());
							}
						}
						int x = Math.abs(pos.getX()-lastpos[0]);
						int y = Math.abs(pos.getZ()-lastpos[1]);
						long distance = (long)Math.sqrt(x*x+y*y);
						LOGGER.info("distance:{}, {}", distance, lastpos);
						if (distance > 10000){
							Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:fastesttravel"));
							if (adv != null) {
								player.getAdvancementTracker().grantCriterion(adv, "fastest_travel");
							}
						} else if (Math.abs(pos.getX()) > 4285700 || Math.abs(pos.getZ()) > 4285700) {
							Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:lighttravel"));
							if (adv != null) {
								player.getAdvancementTracker().grantCriterion(adv, "light_travel");
							}
						}
					}
					lastpos = new int[]{pos.getX(), pos.getZ()};

					double mspt = Arrays.stream(tickTimes).average().orElse(0.0);
					LOGGER.info("MSPT:{}, ONLINE:{}",mspt,online);
					if (mspt > 100 && server.getTicks()>600) {
						Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:stuckserver"));
						if (adv != null) {
							player.getAdvancementTracker().grantCriterion(adv, "stuck_server");
						}
					} else if (online > 9) {
						Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:largeserver"));
						if (adv != null) {
							player.getAdvancementTracker().grantCriterion(adv, "large_server");
						}
					} else if (online > 49) {
						Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:giantserver"));
						if (adv != null) {
							player.getAdvancementTracker().grantCriterion(adv, "giant_server");
						}
					}

					int dragonKills = player.getStatHandler().getStat(Stats.KILLED.getOrCreateStat(EntityType.ENDER_DRAGON));
					//LOGGER.info("dragon: {}",dragonKills);
					if (dragonKills > 9) {
						Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:killdragon10time"));
						if (adv != null) {
							player.getAdvancementTracker().grantCriterion(adv, "kill_10time");
						}
					}

					 ServerWorld world = player.getServerWorld();
					 int entityCount = 0;
					 for (Entity entity : world.iterateEntities()) {
						 if (entity instanceof TntEntity) entityCount++;
					 }
					 if (entityCount  > 1000) {
						 Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:worldeater"));
						 if (adv != null) {
							 player.getAdvancementTracker().grantCriterion(adv, "worldeater");
						 }
					 }else if (entityCount  > 10) {
						 Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:tntquarry"));
						 if (adv != null) {
							 player.getAdvancementTracker().grantCriterion(adv, "tntquarry");
						 }
					 }

					 BlockPos posB = player.getBlockPos();
					 long timeOfDay = world.getTimeOfDay() % 24000;
					 int lightLevel = world.getLightLevel(LightType.BLOCK, posB);
					 int skyLevel = world.getLightLevel(LightType.SKY, posB);
					 int real = world.getLightLevel(posB);
					 if (real == 0 && lightLevel == 0 && skyLevel == 0 && player.getWorld().getRegistryKey().equals(World.OVERWORLD) && timeOfDay>0 && timeOfDay<12000 && world.isSkyVisible(posB.up())) {
						 Advancement adv = server.getAdvancementLoader().get(new Identifier("minecraft:lightsuppression"));
						 if (adv != null) {
							 player.getAdvancementTracker().grantCriterion(adv, "lightsuppression");
						 }
					 }



				 }
			}
		});


		ServerTickEvents.END_SERVER_TICK.register(server -> {
			// 计算当前刻耗时（纳秒转毫秒）
			long now = System.nanoTime();
			tickDuration = (now - lastTickStartTime) / 1_000_000;
			tickTimes[tickIndex] = tickDuration;
			tickIndex = (tickIndex + 1) % tickTimes.length;
		});

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!world.isClient) {
				ItemStack stack = player.getStackInHand(hand);
				if (player instanceof ServerPlayerEntity serverPlayer) {
					if(entity instanceof VillagerEntity villager){
						LOGGER.info("Player {} traded with villager", player.getName().getString());
						// 检查玩家是否已经获得过这个成就
						Advancement adv = serverPlayer.getServer().getAdvancementLoader().get(new Identifier("minecraft:tradingpost"));
						if (adv != null && !serverPlayer.getAdvancementTracker().getProgress(adv).isDone()) {
							tradeCounts.merge(player.getUuid(), 1, Integer::sum);
							int tradeCount = tradeCounts.get(player.getUuid());
							LOGGER.info("Player {} trade count: {}", player.getName().getString(), tradeCount);
							if (tradeCount >= 60) {
								serverPlayer.getAdvancementTracker().grantCriterion(adv, "trading_post");
								LOGGER.info("Triggered trading post advancement for player {}", player.getName().getString());
							}
						}
					}else if (stack.getItem() == Items.OBSERVER) {
						Observer+=1;
						if (Observer>9999){
							Advancement adv = serverPlayer.getServer().getAdvancementLoader().get(new Identifier("minecraft:placeobserver"));
							if (adv != null) {
								serverPlayer.getAdvancementTracker().grantCriterion(adv, "placeobserver");
							}
						}
					}
				}
			}
			return ActionResult.PASS;
		});

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
				BlockState state = world.getBlockState(hitResult.getBlockPos());
				if (state.getBlock() == Blocks.FURNACE) {
					furnace+=1;
					if(furnace>1999){
						Advancement adv = serverPlayer.getServer().getAdvancementLoader().get(new Identifier("minecraft:openfurnace"));
						if (adv != null) {
							serverPlayer.getAdvancementTracker().grantCriterion(adv, "openfurnace");
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

	public boolean isChunkAirSpace(World world, ChunkPos chunkPos, int miny) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = miny; y <= 62; y++) {
					BlockPos pos = new BlockPos(chunkPos.getStartX() + x, y, chunkPos.getStartZ() + z);
					BlockState state = world.getBlockState(pos);
					if (!state.isAir()) {
						return false; // 找到非空气方块，失败
					}
				}
			}
		}

		return true; // 全是空气
	}

}