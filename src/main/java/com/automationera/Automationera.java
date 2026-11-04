package com.automationera;

import com.automationera.advance.*;
import com.automationera.network.ClientModsPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import com.automationera.command.AdvancementCommand;
import com.automationera.keybinding.ModKeyBinding;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;

public class Automationera implements ModInitializer {
	public static final String MOD_ID = "automationera";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Map<UUID, Integer> tradeCounts = new HashMap<>();
	private static final Map<UUID, Long> netherEntryTimes = new HashMap<>();
	private static final Set<UUID> aboveNetherPlayers = new HashSet<>();

	private final long[] tickTimes = new long[100];
	private int tickIndex = 0;
	private long lastTickStartTime;
	private long tickDuration;

	// 以下状态都按玩家区分（以前是全局变量，多人服务器里会互相串：别人开的熔炉算到你头上、两个玩家相距很远会误触发“最快旅行”）
	private final Map<UUID, int[]> lastPos = new HashMap<>();       // 每 20 tick 记录的位置（地狱旅行距离）
	private final Map<UUID, int[]> afkCheckPos = new HashMap<>();   // 上一次整点挂机检查时的位置
	private final Map<UUID, Integer> afkPeriods = new HashMap<>();
	private final Set<UUID> afkPlayers = new HashSet<>();
	private final Map<UUID, Integer> furnaceOpens = new HashMap<>();
	private final Map<UUID, Integer> observerPlaces = new HashMap<>();

	@Override
	public void onInitialize() {
		//Automationera.register("full_stack", FullStackCriterion.INSTANCE);
		//Automationera.register("full_shulker_box", FullStackCriterion.INSTANCE);
		ModConfig.load();
		AdvancementsEnabledCondition.register();
		CriteriaTriggers.register(FullStackCriterion.ID.toString(), FullStackCriterion.INSTANCE);
		PayloadTypeRegistry.serverboundPlay().register(ClientModsPayload.ID, ClientModsPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ClientModsPayload.ID,
				(payload, context) -> onClientMods(context.player(), payload.mods()));
		PlacedBlockInNetherCriterion.register();
		AdvancementCommand.register();

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (afkPlayers.contains(newPlayer.getUUID())){
				MinecraftServer server = newPlayer.level().getServer();
				AdvancementHolder adv = server.getAdvancements().get(Identifier.parse(MOD_ID+":afkdie"));
				if (adv != null) {
					newPlayer.getAdvancements().award(adv, "afkdie");
				}
			}
		});

		// 添加每刻检查
		ServerTickEvents.START_SERVER_TICK.register(server -> {
			if (!ModConfig.advancementsEnabled()) return;
			// 放在玩家循环外：没人在线时也要更新，否则玩家重新进服时 MSPT 会被算成几十秒，误触发“卡服”
			lastTickStartTime = System.nanoTime();
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				if (server.getTickCount() % 216000 == 0){ //1h
					BlockPos pos = player.blockPosition();
					UUID uuid = player.getUUID();
					int[] now = new int[]{pos.getX(), pos.getZ()};
					// 和一小时前的位置比（以前比的是 1 秒前的位置，只要整点那一秒没动就算挂机一小时）
					if (Arrays.equals(afkCheckPos.get(uuid), now)){
						AdvancementHolder adv = server.getAdvancements().get(Identifier.parse(MOD_ID+":afk"));
						if (adv != null) {
							player.getAdvancements().award(adv, "afk");
						}
						int period = afkPeriods.merge(uuid, 1, Integer::sum);
						player.sendSystemMessage(Component.literal("You already AFK "+period+" Hours"));
						afkPlayers.add(uuid);
					}else{
						afkPeriods.remove(uuid);
						afkPlayers.remove(uuid);
					}
					afkCheckPos.put(uuid, now);
				}else if (server.getTickCount() % 200 == 0) {
					ChunkPos chunkPos = player.chunkPosition();
					if (isChunkAirSpace(player.level(), chunkPos, -64)){
						AdvancementHolder adv = server.getAdvancements().get(Identifier.parse(MOD_ID+":realperimeter"));
						if (adv != null) {
							player.getAdvancements().award(adv, "realperimeter");
						}
					}else if (isChunkAirSpace(player.level(), chunkPos, -59)) {
						if (player.level().dimension().equals(Level.OVERWORLD)) {
							AdvancementHolder adv = server.getAdvancements().get(Identifier.parse(MOD_ID+":perimeter"));
							if (adv != null) {
								player.getAdvancements().award(adv, "perimeter");
							}
						} else if (player.level().dimension().equals(Level.NETHER)) {
							AdvancementHolder adv = server.getAdvancements().get(Identifier.parse(MOD_ID+":netherperimeter"));
							if (adv != null) {
								player.getAdvancements().award(adv, "netherperimeter");
							}
						}
					}
				}else if (server.getTickCount() % 20 == 0) {
					int online = server.getPlayerCount();
					//LOGGER.info("Checking player {} for advancements", player.getName().getString());

					// 收集类进度：统计一次背包（含潜影盒）后判断所有条件
					FullStackCriterion.INSTANCE.trigger(player);
					// 经验等级：100 → 500 → 1000 级
					int level = player.experienceLevel;
					if (level >= 100) grant(player, "smallxpfarm", "level");
					if (level >= 500) grant(player, "endlessxp", "level");
					if (level >= 1000) grant(player, "impossiblexp", "level");

					BlockPos pos = player.blockPosition();
					// 检查地狱上层成就
					if (player.level().dimension().equals(Level.NETHER)) {
						// 确保玩家真的在地狱上层，并且已经在那里待了一段时间
						if (pos.getY() > 127) {
							LOGGER.debug("Player {} in Nether at Y: {}", player.getName().getString(), pos.getY());
							Long entryTime = netherEntryTimes.get(player.getUUID());
							// 检查玩家是否已经获得过这个成就
							AdvancementHolder adv = player.level().getServer().getAdvancements().get(Identifier.parse(MOD_ID+":abovenether"));
							//LOGGER.info("ADV:{}",!player.getAdvancementTracker().getProgress(adv).isDone());
							if (adv != null && !player.getAdvancements().getOrStartProgress(adv).isDone()) {
								//ABOVE_NETHER_CRITERION.trigger(player);
								player.getAdvancements().award(adv, "above_nether");
								//aboveNetherPlayers.add(player.getUuid());
								LOGGER.info("Triggered above nether advancement for player {}", player.getName().getString());
							}
						}
						int[] prev = lastPos.getOrDefault(player.getUUID(), new int[]{pos.getX(), pos.getZ()});
						long x = Math.abs(pos.getX()-prev[0]);
						long y = Math.abs(pos.getZ()-prev[1]);
						long distance = (long)Math.sqrt(x*x+y*y);
						//LOGGER.info("distance:{}, {}", distance, lastpos);
						if (distance > 10000){
							AdvancementHolder adv = server.getAdvancements().get(Identifier.parse(MOD_ID+":fastesttravel"));
							if (adv != null) {
								player.getAdvancements().award(adv, "fastest_travel");
							}
						} else if (Math.abs(pos.getX()) > 4285700 || Math.abs(pos.getZ()) > 4285700) {
							AdvancementHolder adv = server.getAdvancements().get(Identifier.parse(MOD_ID+":lighttravel"));
							if (adv != null) {
								player.getAdvancements().award(adv, "light_travel");
							}
						}
					}
					lastPos.put(player.getUUID(), new int[]{pos.getX(), pos.getZ()});

					double mspt = Arrays.stream(tickTimes).average().orElse(0.0);
					//LOGGER.info("MSPT:{}, ONLINE:{}",mspt,online);
					if (mspt > 100 && server.getTickCount()>600) {
						AdvancementHolder adv = server.getAdvancements().get(Identifier.parse(MOD_ID+":stuckserver"));
						if (adv != null) {
							player.getAdvancements().award(adv, "stuck_server");
						}
					} else if (online > 49) { // 先判断大的，以前 >9 在前面导致 >49 永远进不去
						AdvancementHolder adv = server.getAdvancements().get(Identifier.parse(MOD_ID+":giantserver"));
						if (adv != null) {
							player.getAdvancements().award(adv, "giant_server");
						}
					} else if (online > 9) {
						AdvancementHolder adv = server.getAdvancements().get(Identifier.parse(MOD_ID+":largeserver"));
						if (adv != null) {
							player.getAdvancements().award(adv, "large_server");
						}
					}

					int dragonKills = player.getStats().getValue(Stats.ENTITY_KILLED.get(EntityType.ENDER_DRAGON));
					//LOGGER.info("dragon: {}",dragonKills);
					if (dragonKills > 9) {
						AdvancementHolder adv = server.getAdvancements().get(Identifier.parse(MOD_ID+":killdragon10time"));
						if (adv != null) {
							player.getAdvancements().award(adv, "kill_10time");
						}
					}

					ServerLevel world = player.level();
					int entityCount = 0;
					for (Entity entity : world.getAllEntities()) {
						if (entity instanceof PrimedTnt) entityCount++;
					}
					if (entityCount  > 1000) {
						AdvancementHolder adv = server.getAdvancements().get(Identifier.parse(MOD_ID+":worldeater"));
						if (adv != null) {
							player.getAdvancements().award(adv, "worldeater");
						}
					}else if (entityCount  > 100) {
						AdvancementHolder adv = server.getAdvancements().get(Identifier.parse(MOD_ID+":tntquarry"));
						if (adv != null) {
							player.getAdvancements().award(adv, "tntquarry");
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
			if (!world.isClientSide()) {
				ItemStack stack = player.getItemInHand(hand);
				if (player instanceof ServerPlayer serverPlayer) {
					if(entity instanceof Villager villager){
						LOGGER.info("Player {} traded with villager", player.getName().getString());
						// 检查玩家是否已经获得过这个成就
						AdvancementHolder adv = serverPlayer.level().getServer().getAdvancements().get(Identifier.parse(MOD_ID+":tradingpost"));
						if (adv != null && !serverPlayer.getAdvancements().getOrStartProgress(adv).isDone()) {
							tradeCounts.merge(player.getUUID(), 1, Integer::sum);
							int tradeCount = tradeCounts.get(player.getUUID());
							LOGGER.info("Player {} trade count: {}", player.getName().getString(), tradeCount);
							if (tradeCount >= 60) {
								serverPlayer.getAdvancements().award(adv, "trading_post");
								LOGGER.info("Triggered trading post advancement for player {}", player.getName().getString());
							}
						}
					}
				}
			}
			return InteractionResult.PASS;
		});

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
				BlockState state = world.getBlockState(hitResult.getBlockPos());
				// 放置侦测器：以前挂在 UseEntityCallback（右键实体）上，放方块永远不会计数
				if (player.getItemInHand(hand).is(Items.OBSERVER)) {
					if (observerPlaces.merge(serverPlayer.getUUID(), 1, Integer::sum) > 9999) {
						AdvancementHolder adv = serverPlayer.level().getServer().getAdvancements().get(Identifier.parse(MOD_ID+":placeobserver"));
						if (adv != null) {
							serverPlayer.getAdvancements().award(adv, "placeobserver");
						}
					}
				}
				if (state.is(Blocks.BEACON) && isFullNetheriteBeacon(world, hitResult.getBlockPos())) {
					if (grant(serverPlayer, "netheritebeacon", "beacon") && ModConfig.advancementRewardsEnabled()) {
						serverPlayer.giveExperiencePoints(NETHERITE_BEACON_REWARD_XP);
					}
				}
				if (state.getBlock() == Blocks.FURNACE) {
					if(furnaceOpens.merge(serverPlayer.getUUID(), 1, Integer::sum) > 1999){
						AdvancementHolder adv = serverPlayer.level().getServer().getAdvancements().get(Identifier.parse(MOD_ID+":openfurnace"));
						if (adv != null) {
							serverPlayer.getAdvancements().award(adv, "openfurnace");
						}
					}
				}
			}
			return InteractionResult.PASS;
		});


	}

	private static final int NETHERITE_BEACON_REWARD_XP = 10000;
	private static final Set<String> MASA_MODS = Set.of("malilib");
	private static final Set<String> CARPET_MODS = Set.of("carpet");
	private static final Set<String> OPTIMIZE_MODS = Set.of("sodium", "iris", "entityculling");

	/** 发放由代码控制的进度；返回 true 表示这次才刚完成（用于发奖励） */
	public static boolean grant(ServerPlayer player, String id, String criterion) {
		MinecraftServer server = player.level().getServer();
		AdvancementHolder adv = server.getAdvancements().get(Identifier.fromNamespaceAndPath(MOD_ID, id));
		if (adv == null || player.getAdvancements().getOrStartProgress(adv).isDone()) return false;
		return player.getAdvancements().award(adv, criterion)
				&& player.getAdvancements().getOrStartProgress(adv).isDone();
	}

	/** 客户端进服时上报的生电模组 */
	private static void onClientMods(ServerPlayer player, List<String> mods) {
		if (!ModConfig.advancementsEnabled()) return;
		boolean masa = mods.stream().anyMatch(MASA_MODS::contains);
		boolean carpet = mods.stream().anyMatch(CARPET_MODS::contains);
		boolean optimize = mods.stream().anyMatch(OPTIMIZE_MODS::contains);
		if (!(masa || carpet || optimize)) return;
		grant(player, "notvanilla", "mod");
		if (masa) grant(player, "masamods", "mod");
		if (carpet) grant(player, "carpetmod", "mod");
		if (optimize) grant(player, "optimizemods", "mod");
	}

	/** 满级（4 层）且全部是下界合金块的信标金字塔 */
	public static boolean isFullNetheriteBeacon(Level world, BlockPos beacon) {
		for (int layer = 1; layer <= 4; layer++) {
			int y = beacon.getY() - layer;
			for (int x = -layer; x <= layer; x++) {
				for (int z = -layer; z <= layer; z++) {
					if (!world.getBlockState(new BlockPos(beacon.getX() + x, y, beacon.getZ() + z)).is(Blocks.NETHERITE_BLOCK)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/** 超平坦世界本来就是“空置域”，不算 */
	public static boolean isFlatWorld(Level world) {
		return world instanceof ServerLevel sw && sw.getChunkSource().getGenerator() instanceof FlatLevelSource;
	}

	public static boolean isChunkAirSpace(Level world, ChunkPos chunkPos, int miny) {
		if (isFlatWorld(world)) return false;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = miny; y <= 62; y++) {
					BlockPos pos = new BlockPos(chunkPos.getMinBlockX() + x, y, chunkPos.getMinBlockZ() + z);
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
