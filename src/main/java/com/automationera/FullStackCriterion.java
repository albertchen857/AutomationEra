package com.automationera;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.Item;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class FullStackCriterion extends AbstractCriterion<FullStackCriterion.Conditions> {
    public static final Identifier ID = new Identifier("automationera", "full_stack");

    @Override
    public Identifier getId() {
        return ID;
    }

    // 触发进度条件
    public void trigger(ServerPlayerEntity player, Item item, int requiredStacks) {
        this.trigger(player, conditions -> conditions.item == item && conditions.requiredStacks == requiredStacks);
    }

    // 进度条件实例
    public static class Conditions extends AbstractCriterionConditions {
        private final Item item;
        private final int requiredStacks;

        public Conditions(Item item, int requiredStacks) {
            super(ID, LootContextPredicate.EMPTY);
            this.item = item;
            this.requiredStacks = requiredStacks;
        }

        // 将条件转换为JSON（用于生成进度JSON文件）
        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            JsonObject json = super.toJson(serializer);

            // 创建物品条件
            JsonObject itemPredicate = new JsonObject();
            itemPredicate.addProperty("item", Registries.ITEM.getId(this.item).toString());

            // 设置数量范围（固定为64）
            JsonObject countObj = new JsonObject();
            countObj.addProperty("min", 64);
            countObj.addProperty("max", 64);
            itemPredicate.add("count", countObj);

            // 创建items数组
            JsonArray itemsArray = new JsonArray();
            itemsArray.add(itemPredicate);

            // 创建conditions对象
            JsonObject conditions = new JsonObject();
            conditions.add("items", itemsArray);
            conditions.addProperty("required_stacks", this.requiredStacks);
            json.add("conditions", conditions);

            return json;
        }
    }

    // 从JSON解析条件
    @Override
    protected Conditions conditionsFromJson(JsonObject json,
                                            LootContextPredicate player,
                                            AdvancementEntityPredicateDeserializer deserializer) {
        // 获取items数组
        JsonArray items = json.getAsJsonObject("conditions").getAsJsonArray("items");
        JsonObject firstItem = items.get(0).getAsJsonObject();

        // 获取物品ID
        String itemId = JsonHelper.getString(firstItem, "item");
        Identifier itemIdentifier = new Identifier(itemId);
        Item item = Registries.ITEM.get(itemIdentifier);

        if (item == null) {
            throw new IllegalStateException("未知物品: " + itemId);
        }

        // 从JSON中获取所需堆叠数量
        int requiredStacks = JsonHelper.getInt(json.getAsJsonObject("conditions"), "required_stacks");

        return new Conditions(item, requiredStacks);
    }

    // 创建进度条件实例
    public static AdvancementCriterion createCriterion(Item item, int requiredStacks) {
        return new AdvancementCriterion(new Conditions(item, requiredStacks));
    }
}