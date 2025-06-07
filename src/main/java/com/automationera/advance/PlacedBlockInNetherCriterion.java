package com.automationera.advance;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
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
import java.util.UUID;

public class PlacedBlockInNetherCriterion extends AbstractCriterion<PlacedBlockInNetherCriterion.Conditions> {
    private static final Identifier ID = new Identifier("automationera", "placed_block_in_nether");
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/PlacedBlockInNetherCriterion");
    private static PlacedBlockInNetherCriterion INSTANCE;
    private static final double ICE_BOAT_SPEED = 5; // 每秒方块数
    private static final int CHECK_INTERVAL = 10; // 每5个tick检查一次
    private static int tickCounter = 0;
    
    // 存储玩家上一次的位置
    private static final Map<UUID, Vec3d> lastPositions = new HashMap<>();
    private static final Map<UUID, Long> lastCheckTimes = new HashMap<>();

    public PlacedBlockInNetherCriterion() {}

    @Override
    public Identifier getId() {
        return ID;
    }

    public static Conditions conditions(BlockPredicate build, LocationPredicate location) {
        return new Conditions(ID, LootContextPredicate.EMPTY, location);
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        LocationPredicate locationPredicate = LocationPredicate.fromJson(obj.get("location"));
        return new Conditions(ID, playerPredicate, locationPredicate);
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

    public static class Conditions extends AbstractCriterionConditions {
        private final LocationPredicate location;

        public Conditions(Identifier id, LootContextPredicate playerPredicate, LocationPredicate location) {
            super(id, playerPredicate);
            this.location = location;
        }

        public boolean matches(ServerWorld world, BlockPos pos) {
            return this.location.test(world, pos.getX(), pos.getY(), pos.getZ());
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("location", this.location.toJson());
            return jsonObject;
        }
    }

    public static PlacedBlockInNetherCriterion register() {
        if (INSTANCE == null) {
            INSTANCE = new PlacedBlockInNetherCriterion();
            Criteria.register(INSTANCE);
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
