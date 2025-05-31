// CustomCriterion.java
package com.automationera.advance;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FullStackCriterion extends AbstractCriterion<FullStackCriterion.Conditions> {
    public static final Identifier ID = new Identifier("automationera", "full_stack_count");


    // 关键修复：添加静态条件类
    public static class Conditions extends AbstractCriterionConditions {
        public Conditions() {
            super(ID, LootContextPredicate.EMPTY);
        }
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions(); // 返回新条件实例
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    // 添加创建条件的方法 - 这是您需要的
    public Conditions createConditions() {
        return new Conditions();
    }

    public void trigger(ServerPlayerEntity player, int fullStackCount) {
        this.trigger(player, conditions -> true); // 实际检测在外部完成
    }
}