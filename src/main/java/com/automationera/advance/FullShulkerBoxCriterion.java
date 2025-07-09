package com.automationera.advance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.inventory.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.DataComponentTypes;

import java.util.Optional;

public class FullShulkerBoxCriterion extends AbstractCriterion<FullShulkerBoxCriterion.Conditions> {
    public static final Identifier ID = Identifier.of("automationera", "full_shulker_box");
    public static final FullShulkerBoxCriterion INSTANCE = new FullShulkerBoxCriterion();
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/FullShulkerBoxCriterion");


    @Override
    public Codec<Conditions> getConditionsCodec() {
        LOGGER.info("FullStackCriterion CODEC");
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player) {
        LOGGER.info("FullShulkerBoxCriterion TRIGGER");
        this.trigger(player, conditions -> {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (stack.isOf(Items.SHULKER_BOX)) {
                    if (conditions.itemPredicate.test(stack)) { // 必须用 itemPredicate 判断
                        if (conditions.matches(stack)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
    }



    public static class Conditions implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemPredicate.CODEC.fieldOf("item").forGetter(c -> c.itemPredicate),
                Codec.INT.fieldOf("required_stacks").forGetter(c -> c.requiredStacks)
        ).apply(instance, Conditions::new));

        private final ItemPredicate itemPredicate;
        private final int requiredStacks;

        public Conditions(ItemPredicate itemPredicate, int requiredStacks) {
            this.itemPredicate = itemPredicate;
            this.requiredStacks = requiredStacks;
        }

        public ItemPredicate getItemPredicate() {
            return itemPredicate;
        }

        public int getRequiredStacks() {
            return requiredStacks;
        }

        public boolean matches(ItemStack shulkerBox) {
            if (shulkerBox == null || shulkerBox.isEmpty()) {
                return false;
            }
            ContainerComponent container = shulkerBox.get(DataComponentTypes.CONTAINER);
            if (container == null) {
                return false;
            }
            int matchingStacks = 0;
            for (ItemStack itemStack : container.iterateNonEmpty()) {
                if (itemStack.getCount() == 64) {
                    matchingStacks++;
                }
            }
            return matchingStacks >= requiredStacks;
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return Optional.empty();
        }
    }

    public static AdvancementCriterion<Conditions> createCriterion(ItemPredicate itemPredicate, int requiredStacks) {
        LOGGER.info("FullStackCriterion CREATE");
        return INSTANCE.create(new Conditions(itemPredicate, requiredStacks));
    }
}