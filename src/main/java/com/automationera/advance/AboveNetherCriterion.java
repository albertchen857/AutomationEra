package com.automationera.advance;

import com.automationera.AutomationEra;
import com.google.gson.JsonObject;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AboveNetherCriterion extends AbstractCriterion<AboveNetherCriterion.Conditions> {
    private static final Identifier ID = new Identifier(AutomationEra.MOD_ID, "above_nether");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions(playerPredicate);
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, conditions -> true);
    }

    public static class Conditions extends AbstractCriterionConditions implements CriterionConditions {
        public Conditions(LootContextPredicate playerPredicate) {
            super(ID, playerPredicate);
        }

        @Override
        public Identifier getId() {
            return ID;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            return super.toJson(predicateSerializer);
        }
    }

    public static AdvancementCriterion createCriterion() {
        return new AdvancementCriterion(new Conditions(LootContextPredicate.EMPTY));
    }
} 