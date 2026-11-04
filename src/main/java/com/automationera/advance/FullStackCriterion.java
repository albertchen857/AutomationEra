package com.automationera.advance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

/**
 * “收集 N 组 / M 盒某物品”的进度条件。
 * <p>
 * 组 = 数量达到该物品最大堆叠数的一格（鸡蛋 16、珍珠 16、药水 1……），潜影盒里的满组也算；
 * 盒 = 任意颜色的潜影盒，27 格全是该物品的满组。
 * items 里任意一种物品满足 (组数 >= requiredStacks) 或 (盒数 >= requiredBoxes) 即达成；值为 0 表示不检查这一项。
 * <p>
 * 0.1.9.1 合并了原来的 FullShulkerBoxCriterion（它用潜影盒本身去匹配目标物品，所以永远不会触发）。
 */
public class FullStackCriterion extends SimpleCriterionTrigger<FullStackCriterion.Conditions> {
    public static final Identifier ID = Identifier.fromNamespaceAndPath("automationera", "full_stack");
    public static final FullStackCriterion INSTANCE = new FullStackCriterion();
    private static final int SHULKER_SLOTS = 27;

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    /** 统计一次背包，再判断所有监听中的条件（以前每种物品都调一次、每次都重新扫描全部条件） */
    public void trigger(ServerPlayer player) {
        Summary summary = Summary.of(player.getInventory());
        this.trigger(player, conditions -> conditions.matches(summary));
    }

    /** 背包里每种物品的满组数、满盒数 */
    public record Summary(Map<Item, Integer> stacks, Map<Item, Integer> boxes) {
        static Summary of(Inventory inv) {
            Map<Item, Integer> stacks = new HashMap<>();
            Map<Item, Integer> boxes = new HashMap<>();
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if (stack.isEmpty()) continue;
                countFull(stack, stacks);
                if (isShulkerBox(stack)) {
                    ItemContainerContents container = stack.get(DataComponents.CONTAINER);
                    if (container == null) continue;
                    Item only = null;
                    int full = 0;
                    boolean uniform = true;
                    for (ItemStack inner : container.nonEmptyItems()) {
                        countFull(inner, stacks);
                        if (inner.getCount() != inner.getMaxStackSize()) { uniform = false; continue; }
                        if (only == null) only = inner.getItem();
                        else if (only != inner.getItem()) uniform = false;
                        full++;
                    }
                    if (uniform && only != null && full == SHULKER_SLOTS) {
                        boxes.merge(only, 1, Integer::sum);
                    }
                }
            }
            return new Summary(stacks, boxes);
        }

        private static void countFull(ItemStack stack, Map<Item, Integer> stacks) {
            if (stack.getCount() == stack.getMaxStackSize()) {
                stacks.merge(stack.getItem(), 1, Integer::sum);
            }
        }

        private static boolean isShulkerBox(ItemStack stack) {
            return Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock;
        }
    }

    public static class Conditions implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("items").forGetter(c -> List.copyOf(c.items)),
                Codec.INT.optionalFieldOf("required_stacks", 0).forGetter(c -> c.requiredStacks),
                Codec.INT.optionalFieldOf("required_boxes", 0).forGetter(c -> c.requiredBoxes)
        ).apply(instance, (items, stacks, boxes) -> new Conditions(Set.copyOf(items), stacks, boxes)));

        private final Set<Item> items;
        private final int requiredStacks;
        private final int requiredBoxes;

        public Conditions(Set<Item> items, int requiredStacks, int requiredBoxes) {
            this.items = items;
            this.requiredStacks = requiredStacks;
            this.requiredBoxes = requiredBoxes;
        }

        public boolean matches(Summary summary) {
            for (Item item : items) {
                if (requiredStacks > 0 && summary.stacks().getOrDefault(item, 0) >= requiredStacks) return true;
                if (requiredBoxes > 0 && summary.boxes().getOrDefault(item, 0) >= requiredBoxes) return true;
            }
            return false;
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }
    }

    /** 收集 requiredStacks 组（任意一种物品达到即可） */
    public static Criterion<Conditions> stacks(Set<Item> items, int requiredStacks) {
        return INSTANCE.createCriterion(new Conditions(items, requiredStacks, 0));
    }

    /** 收集 requiredStacks 组 或 requiredBoxes 盒 */
    public static Criterion<Conditions> stacksOrBoxes(Set<Item> items, int requiredStacks, int requiredBoxes) {
        return INSTANCE.createCriterion(new Conditions(items, requiredStacks, requiredBoxes));
    }

    /** 收集 requiredBoxes 盒 */
    public static Criterion<Conditions> boxes(Set<Item> items, int requiredBoxes) {
        return INSTANCE.createCriterion(new Conditions(items, 0, requiredBoxes));
    }
}
