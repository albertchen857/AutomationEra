package com.automationera.advance;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class FullShulkerBoxCriterion extends AbstractCriterion<FullShulkerBoxCriterion.Conditions> {
    private static final Identifier ID = new Identifier("automationera", "full_shulker_box");
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/FullShulkerBoxCriterion");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject json, LootContextPredicate player, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(json.get("item"));
        int requiredStacks = JsonHelper.getInt(json, "required_stacks", 1);
        return new Conditions(player, itemPredicate, requiredStacks);
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, conditions -> {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (stack.isOf(Items.SHULKER_BOX)) {
                    if (conditions.matches(stack)) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final ItemPredicate itemPredicate;
        private final int requiredStacks;

        public Conditions(LootContextPredicate player, ItemPredicate itemPredicate, int requiredStacks) {
            super(ID, player);
            this.itemPredicate = itemPredicate;
            this.requiredStacks = requiredStacks;
        }

        public boolean matches(ItemStack shulkerBox) {
            if (shulkerBox == null || !shulkerBox.hasNbt()) {
                return false;
            }

            NbtCompound nbt = shulkerBox.getNbt();
            if (!nbt.contains("BlockEntityTag")) {
                return false;
            }

            NbtCompound blockEntityTag = nbt.getCompound("BlockEntityTag");
            if (!blockEntityTag.contains("Items")) {
                return false;
            }

            NbtList items = blockEntityTag.getList("Items", 10);

            int matchingStacks = 0;
            for (int i = 0; i < items.size(); i++) {
                NbtCompound item = items.getCompound(i);
                ItemStack itemStack = ItemStack.fromNbt(item);
                if (itemPredicate.test(itemStack)) {
                    matchingStacks++;
                }
            }

            return matchingStacks >= requiredStacks;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject json = super.toJson(predicateSerializer);
            json.add("item", this.itemPredicate.toJson());
            json.addProperty("required_stacks", this.requiredStacks);
            return json;
        }
    }

    public static Conditions createCriterion(Item targetItem, int requiredStacks) {
        return new Conditions(
            LootContextPredicate.EMPTY,
            ItemPredicate.Builder.create().items(targetItem).build(),
            requiredStacks
        );
    }

    public static Conditions createCriterion(Set<Item> targetItems, int requiredStacks) {
        return new Conditions(
            LootContextPredicate.EMPTY,
            ItemPredicate.Builder.create().items(targetItems.toArray(new Item[0])).build(),
            requiredStacks
        );
    }

    public boolean matches(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(Items.SHULKER_BOX)) {
                if (stack == null || !stack.hasNbt()) {
                    continue;
                }

                NbtCompound nbt = stack.getNbt();
                if (!nbt.contains("BlockEntityTag")) {
                    continue;
                }

                NbtCompound blockEntityTag = nbt.getCompound("BlockEntityTag");
                if (!blockEntityTag.contains("Items")) {
                    continue;
                }

                NbtList items = blockEntityTag.getList("Items", 10);

                int matchingStacks = 0;
                for (int j = 0; j < items.size(); j++) {
                    NbtCompound item = items.getCompound(j);
                    ItemStack itemStack = ItemStack.fromNbt(item);
                    if (itemStack.getCount() == 64) {
                        matchingStacks++;
                    }
                }
                if (matchingStacks >= 27) {
                    return true;
                }
            }
        }
        return false;
    }
} 