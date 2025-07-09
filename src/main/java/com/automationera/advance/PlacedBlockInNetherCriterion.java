package com.automationera.advance;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlacedBlockInNetherCriterion extends AbstractCriterion<PlacedBlockInNetherCriterion.Conditions> {
    public static final Identifier ID = Identifier.of("automationera", "placed_block_in_nether");
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/PlacedBlockInNetherCriterion");
    private static PlacedBlockInNetherCriterion INSTANCE;
    private static final double ICE_BOAT_SPEED = 5; // 每秒方块数
    private static final int CHECK_INTERVAL = 10; // 每5个tick检查一次
    private static int tickCounter = 0;
    
    // 存储玩家上一次的位置
    private static final Map<UUID, Vec3d> lastPositions = new HashMap<>();
    private static final Map<UUID, Long> lastCheckTimes = new HashMap<>();

    public PlacedBlockInNetherCriterion() {}

    public static AdvancementCriterion<Conditions> conditions(BlockPredicate block, LocationPredicate location) {
        return PlacedBlockInNetherCriterion.INSTANCE.create(new Conditions(location));
    }

    protected Conditions conditionsFromJson(JsonObject obj,BlockPredicate playerPredicate) {
        LocationPredicate locationPredicate = LocationPredicate.CODEC
                .parse(JsonOps.INSTANCE, obj.get("location"))
                .result()
                .orElseThrow(() -> new IllegalArgumentException("无法解析 location predicate"));

        return new Conditions(locationPredicate);
    }

    public void trigger(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        if (player == null || world == null || pos == null) {
            LOGGER.warn("Invalid trigger parameters");
            return;
        }

        LOGGER.info("Checking boat speed for player {} at position {}", player.getName().getString(), pos);
        this.trigger(player, conditions -> conditions.matches(world, pos));
    }

    private static boolean isInNether(ServerWorld world) {
        return world.getRegistryKey() == World.NETHER;
    }

    private static boolean isInBoat(PlayerEntity player) {
        return player.getVehicle() instanceof BoatEntity;
    }

    private static boolean isSpeedOverIceBoat(PlayerEntity player) {
        if (player.getVehicle() instanceof BoatEntity boat) {
            UUID playerId = player.getUuid();
            Vec3d currentPos = boat.getPos();
            long currentTime = System.currentTimeMillis();
            
            if (lastPositions.containsKey(playerId) && lastCheckTimes.containsKey(playerId)) {
                Vec3d lastPos = lastPositions.get(playerId);
                long lastTime = lastCheckTimes.get(playerId);
                long timeDiff = currentTime - lastTime;
                
                if (timeDiff > 0) {
                    double distance = Math.sqrt(
                        Math.pow(currentPos.x - lastPos.x, 2) +
                        Math.pow(currentPos.z - lastPos.z, 2)
                    );
                    
                    // 转换为每秒方块数
                    double speed = (distance / timeDiff) * 1000.0;
                    
                    LOGGER.info("Player {} boat speed: {} blocks/second", player.getName().getString(), speed);
                    return speed > ICE_BOAT_SPEED;
                }
            }
            
            // 更新位置和时间
            lastPositions.put(playerId, currentPos);
            lastCheckTimes.put(playerId, currentTime);
        }
        return false;
    }

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public static class Conditions implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = LocationPredicate.CODEC
                .fieldOf("location")
                .xmap(Conditions::new, c -> c.location)
                .codec();
        private final LocationPredicate location;

        public Conditions(LocationPredicate location) {
            this.location = location;
        }

        public boolean matches(ServerWorld world, BlockPos pos) {
            return this.location.test(world, pos.getX(), pos.getY(), pos.getZ());
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            var encoded = LocationPredicate.CODEC.encodeStart(JsonOps.INSTANCE, this.location);
            encoded.result().ifPresent(jsonElement -> jsonObject.add("location", jsonElement));
            return jsonObject;
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return Optional.empty();
        }

        public Codec getConditionsCodec() {
            return null;
        }
    }

    public static PlacedBlockInNetherCriterion register() {
        if (INSTANCE == null) {
            INSTANCE = new PlacedBlockInNetherCriterion();
            Criteria.register(String.valueOf(ID), INSTANCE);
            LOGGER.info("!!!成功 registered PlacedBlockInNetherCriterion");
            
            // 注册服务器tick事件监听器
            ServerTickEvents.START_SERVER_TICK.register(server -> {
                tickCounter++;
                if (tickCounter >= CHECK_INTERVAL) {
                    tickCounter = 0;
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        if (player.getWorld() instanceof ServerWorld serverWorld) {
                            // 检查是否在地狱
                            if (isInNether(serverWorld)) {
                                // 检查是否在船上
                                if (isInBoat(player)) {
                                    // 检查速度是否超过冰船速度
                                    if (isSpeedOverIceBoat(player)) {
                                        LOGGER.info("Player {} is in boat in nether with speed over ice boat", player.getName().getString());
                                        INSTANCE.trigger(player, serverWorld, player.getBlockPos());
                                    }
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
