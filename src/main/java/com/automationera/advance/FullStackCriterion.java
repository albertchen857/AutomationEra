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

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FullStackCriterion extends AbstractCriterion<FullStackCriterion.Conditions> {
    public static final Identifier ID = Identifier.of("automationera", "full_stack");
    public static final FullStackCriterion INSTANCE = new FullStackCriterion();

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    // 触发进度条件 - 单物品版本
    public void trigger(ServerPlayerEntity player, Item item, int requiredStacks) {
        this.trigger(player, conditions -> conditions.items.contains(item) && conditions.requiredStacks == requiredStacks);
    }

    // 触发进度条件 - 多物品版本
    public void trigger(ServerPlayerEntity player, Set<Item> items, int requiredStacks) {
        this.trigger(player, conditions -> conditions.items.equals(items) && conditions.requiredStacks == requiredStacks);
    }

    // 进度条件实例
    public static class Conditions implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registries.ITEM.getCodec().listOf().fieldOf("items").forGetter(c -> List.copyOf(c.items)),
            Codec.INT.fieldOf("required_stacks").forGetter(c -> c.requiredStacks)
        ).apply(instance, Conditions::new));

        private final Set<Item> items;
        private final int requiredStacks;

        public Conditions(List<Item> items, int requiredStacks) {
            super(); // null 或 LootContextPredicate.ALWAYS
            this.items = Set.copyOf(items);
            this.requiredStacks = requiredStacks;
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();

            // 创建items数组
            JsonArray itemsArray = new JsonArray();
            for (Item item : items) {
                JsonObject itemPredicate = new JsonObject();
                itemPredicate.addProperty("item", Registries.ITEM.getId(item).toString());

                // 设置数量范围（固定为64）
                JsonObject countObj = new JsonObject();
                countObj.addProperty("min", 64);
                countObj.addProperty("max", 64);
                itemPredicate.add("count", countObj);

                itemsArray.add(itemPredicate);
            }

            // 创建conditions对象
            JsonObject conditions = new JsonObject();
            conditions.add("items", itemsArray);
            conditions.addProperty("required_stacks", this.requiredStacks);
            json.add("conditions", conditions);

            return json;
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return Optional.empty();
        }
    }

    protected Conditions conditionsFromJson(JsonObject json, LootContextPredicate player) {
        JsonArray itemsArray = json.getAsJsonObject("conditions").getAsJsonArray("items");
        List<Item> itemSet = List.of();

        for (JsonElement itemElement : itemsArray) {
            JsonObject itemObj = itemElement.getAsJsonObject();
            String itemId = JsonHelper.getString(itemObj, "item");
            Identifier itemIdentifier = Identifier.of(itemId);
            Item item = Registries.ITEM.get(itemIdentifier);

            if (item == null) {
                throw new IllegalStateException("未知物品: " + itemId);
            }
            itemSet.add(item);
        }

        int requiredStacks = JsonHelper.getInt(json.getAsJsonObject("conditions"), "required_stacks");

        return new Conditions(itemSet, requiredStacks);
    }


    // 创建进度条件实例 - 单物品版本
    public static AdvancementCriterion<Conditions> createCriterion(Item item, int requiredStacks) {
        return new AdvancementCriterion<>(new FullStackCriterion(), new Conditions(List.of(item), requiredStacks));
    }

    public static AdvancementCriterion<Conditions> createCriterion(Set<Item> items, int requiredStacks) {
        return new AdvancementCriterion<>(new FullStackCriterion(), new Conditions(List.copyOf(items), requiredStacks));
    }
}