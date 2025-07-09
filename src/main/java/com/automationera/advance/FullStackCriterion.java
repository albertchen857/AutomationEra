package com.automationera.advance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.item.Item;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;

public class FullStackCriterion extends AbstractCriterion<FullStackCriterion.Conditions> {
    public static final Identifier ID = Identifier.of("automationera", "full_stack");
    public static final FullStackCriterion INSTANCE = new FullStackCriterion();
    public static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/FullStackCriterion");

    @Override
    public Codec<Conditions> getConditionsCodec() {
        LOGGER.info("FullStackCriterion CODEC");
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Set<Item> items, int requiredStacks) {
        LOGGER.info("FullStackCriterion TRIGGER");
        // FullStackCriterion.java
        this.trigger(player, conditions -> {
            // 只要当前 items 里有任意一个达标就触发
            for (Item item : conditions.items) {
                int fullStackCount = 0;
                for (var stack : player.getInventory().main) {
                    if (!stack.isEmpty() && stack.getItem() == item && stack.getCount() == item.getMaxCount()) {
                        fullStackCount++;
                    }
                }
                if (fullStackCount >= conditions.requiredStacks) {
                    return true;
                }
            }
            return false;
        });

    }

    public static class Conditions implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Registries.ITEM.getCodec().listOf().fieldOf("items").forGetter(c -> List.copyOf(c.items)),
                Codec.INT.fieldOf("required_stacks").forGetter(c -> c.requiredStacks)
        ).apply(instance, (items, requiredStacks) -> new Conditions(Set.copyOf(items), requiredStacks)));

        private final Set<Item> items;
        private final int requiredStacks;

        public Conditions(Set<Item> items, int requiredStacks) {
            this.items = items;
            this.requiredStacks = requiredStacks;
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return Optional.empty();
        }
    }

    // 工厂方法，支持自动注册
    public static AdvancementCriterion<Conditions> createCriterion(Set<Item> items, int requiredStacks) {
        LOGGER.info("FullStackCriterion CREATE");
        return INSTANCE.create(new Conditions(items, requiredStacks));
    }
}

