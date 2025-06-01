package com.automationera;

import com.automationera.advance.StackTriggerTemplate;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.logging.ILogger;

import java.util.function.Consumer;

public class Advancements implements Consumer<Consumer<Advancement>> {
    private static final String MOD_ID = "automationera";
    @Override
    public void accept(Consumer<Advancement> consumer) {
        // 创建根进度
        Advancement root = Advancement.Builder.create()
                .display(
                        Items.REDSTONE,
                        Text.translatable("advancements.root.title"),
                        Text.translatable("advancements.root.descr"),
                        new Identifier("textures/block/redstone_ore.png"),
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("get_redstone",
                        InventoryChangedCriterion.Conditions.items(Items.REDSTONE))
                .build(consumer, MOD_ID + "/root");

        // 创建铁农场进度
        createStackAdvancement(consumer, root,
                "ironfarm",
                Items.IRON_INGOT,
                4,
                Items.IRON_INGOT);

        // 创建石农场进度
        createStackAdvancement(consumer, root,
                "stonefarm",
                Items.COBBLESTONE,
                9,
                Items.COBBLESTONE);
    }

    private void createStackAdvancement(
            Consumer<Advancement> consumer,
            Advancement parent,
            String advancementId,
            Item targetItem,
            int requiredStacks,
            Item displayItem
    ) {
        System.out.println(advancementId);
        Advancement.Builder.create()
                .parent(parent)
                .display(
                        displayItem,
                        Text.translatable("advancements."  + advancementId + ".title"),
                        Text.translatable("advancements."  + advancementId + ".descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("stacks_" + requiredStacks, 
                    InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create()
                            .items(targetItem)
                            .count(NumberRange.IntRange.atLeast(64 * requiredStacks))
                            .build()
                    ))
                .build(consumer, MOD_ID + "/" + advancementId);
    }
}


