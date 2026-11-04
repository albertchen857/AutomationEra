package com.automationera.advance;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlacedBlockInNetherCriterion extends SimpleCriterionTrigger<PlacedBlockInNetherCriterion.Conditions> {
    public static final Identifier ID = Identifier.fromNamespaceAndPath("automationera", "placed_block_in_nether");
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/PlacedBlockInNetherCriterion");
    private static PlacedBlockInNetherCriterion INSTANCE;
    private static final double ICE_BOAT_SPEED = 5; // 每秒方块数
    private static final int CHECK_INTERVAL = 10; // 每5个tick检查一次
    private static int tickCounter = 0;
    
    // 存储玩家上一次的位置
    private static final Map<UUID, Vec3> lastPositions = new HashMap<>();
    private static final Map<UUID, Long> lastCheckTimes = new HashMap<>();

    public PlacedBlockInNetherCriterion() {}

    public static Criterion<Conditions> conditions(BlockPredicate block, LocationPredicate location) {
        return PlacedBlockInNetherCriterion.INSTANCE.createCriterion(new Conditions(location));
    }

    protected Conditions conditionsFromJson(JsonObject obj,BlockPredicate playerPredicate) {
        LocationPredicate locationPredicate = LocationPredicate.CODEC
                .parse(JsonOps.INSTANCE, obj.get("location"))
                .result()
                .orElseThrow(() -> new IllegalArgumentException("无法解析 location predicate"));

        return new Conditions(locationPredicate);
    }

    public void trigger(ServerPlayer player, ServerLevel world, BlockPos pos) {
        if (player == null || world == null || pos == null) {
            LOGGER.warn("Invalid trigger parameters");
            return;
        }

        LOGGER.info("Checking boat speed for player {} at position {}", player.getName().getString(), pos);
        this.trigger(player, conditions -> conditions.matches(world, pos));
    }

    private static boolean isInNether(ServerLevel world) {
        return world.dimension() == Level.NETHER;
    }

    private static boolean isInBoat(Player player) {
        return player.getVehicle() instanceof Boat;
    }

    private static boolean isSpeedOverIceBoat(Player player) {
        if (player.getVehicle() instanceof Boat boat) {
            UUID playerId = player.getUUID();
            Vec3 currentPos = boat.position();
            long currentTime = System.currentTimeMillis();
            
            Vec3 lastPos = lastPositions.get(playerId);
            Long lastTime = lastCheckTimes.get(playerId);
            // 每次都更新位置和时间（以前算出速度后直接 return，之后一直和第一次的位置比，算的是“上船以来的平均速度”）
            lastPositions.put(playerId, currentPos);
            lastCheckTimes.put(playerId, currentTime);

            if (lastPos != null && lastTime != null) {
                long timeDiff = currentTime - lastTime;
                if (timeDiff > 0) {
                    double distance = Math.sqrt(
                        Math.pow(currentPos.x - lastPos.x, 2) +
                        Math.pow(currentPos.z - lastPos.z, 2)
                    );

                    // 转换为每秒方块数
                    double speed = (distance / timeDiff) * 1000.0;

                    LOGGER.debug("Player {} boat speed: {} blocks/second", player.getName().getString(), speed);
                    return speed > ICE_BOAT_SPEED;
                }
            }
        }
        return false;
    }

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public static class Conditions implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Conditions> CODEC = LocationPredicate.CODEC
                .fieldOf("location")
                .xmap(Conditions::new, c -> c.location)
                .codec();
        private final LocationPredicate location;

        public Conditions(LocationPredicate location) {
            this.location = location;
        }

        public boolean matches(ServerLevel world, BlockPos pos) {
            return this.location.matches(world, pos.getX(), pos.getY(), pos.getZ());
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            var encoded = LocationPredicate.CODEC.encodeStart(JsonOps.INSTANCE, this.location);
            encoded.result().ifPresent(jsonElement -> jsonObject.add("location", jsonElement));
            return jsonObject;
        }

        @Override
        public Optional<Holder<LootItemCondition>> player() {
            return Optional.empty();
        }

        public Codec getConditionsCodec() {
            return null;
        }
    }

    public static PlacedBlockInNetherCriterion register() {
        if (INSTANCE == null) {
            INSTANCE = new PlacedBlockInNetherCriterion();
            Registry.register(BuiltInRegistries.TRIGGER_TYPES, ID, INSTANCE);
            LOGGER.info("!!!成功 registered PlacedBlockInNetherCriterion");
            
            // 注册服务器tick事件监听器
            ServerTickEvents.START_SERVER_TICK.register(server -> {
                tickCounter++;
                if (tickCounter >= CHECK_INTERVAL) {
                    tickCounter = 0;
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        ServerLevel serverWorld = player.level();
                            // 检查是否在地狱
                            if (isInNether(serverWorld)) {
                                // 检查是否在船上
                                if (isInBoat(player)) {
                                    // 检查速度是否超过冰船速度
                                    if (isSpeedOverIceBoat(player)) {
                                        LOGGER.info("Player {} is in boat in nether with speed over ice boat", player.getName().getString());
                                        INSTANCE.trigger(player, serverWorld, player.blockPosition());
                                    }
                                }
                            }
                    }
                }
            });
            LOGGER.info("!!!成功 registered boat speed event handler");
        }
        return INSTANCE;
    }
}
