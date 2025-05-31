package com.automationera.advance;

import com.automationera.AutomationEra;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class Advancements implements Consumer<Consumer<Advancement>> {
    @Override
    public void accept(Consumer<Advancement> consumer) {
        var name = "root";
        //root
        Advancement rootAdvancement = Advancement.Builder.create()
                .display(
                        Items.REDSTONE, // 显示的图标
                        Text.translatable("advancements."+name+".title"), // 标题
                        Text.translatable("advancements."+name+".descr"), // 描述
                        new Identifier("textures/block/redstone_ore.png"), // 使用的背景图片
                        AdvancementFrame.TASK, // 选项: TASK, CHALLENGE, GOAL
                        true, // 在右上角显示
                        true, // 在聊天框中提示
                        false // 在进度页面里隐藏
                )
                // Criterion 中使用的第一个字符串是其他进度在需要 'requirements' 时引用的名字
                .criterion("a1", InventoryChangedCriterion.Conditions.items(Items.REDSTONE))
                .build(consumer, "automationera/" + name);
        //IronFarm
        name = "ironfarm";
        Advancement IFAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                .display(
                        Items.IRON_INGOT, // 显示的图标
                        Text.translatable("advancements."+name+".title"), // 标题
                        Text.translatable("advancements."+name+".descr"), // 描述
                        null, // 使用的背景图片
                        AdvancementFrame.TASK, // 选项: TASK, GOAL, CHALLENGE
                        true, // 在右上角显示
                        true, // 在聊天框中提示
                        false // 在进度页面里隐藏
                )
                // Criterion 中使用的第一个字符串是其他进度在需要 'requirements' 时引用的名字
                .criterion("a1", AutomationEra.FULL_STACK_CRITERION.createConditions())
                .build(consumer, "automationera/" + name);
        //StoneFarm
        name = "stonefarm";
        Advancement SFAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                .display(
                        Items.COBBLESTONE, // 显示的图标
                        Text.translatable("advancements."+name+".title"), // 标题
                        Text.translatable("advancements."+name+".descr"), // 描述
                        null, // 使用的背景图片
                        AdvancementFrame.TASK, // 选项: TASK, GOAL, CHALLENGE
                        true, // 在右上角显示
                        true, // 在聊天框中提示
                        false // 在进度页面里隐藏
                )
                // Criterion 中使用的第一个字符串是其他进度在需要 'requirements' 时引用的名字
                .criterion("a1", InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create()
                        .items(Items.COBBLESTONE) // 指定物品
                        .count(NumberRange.IntRange.atLeast(256)) // 最小数量
                        .build()))
                .build(consumer, "automationera/" + name);
    }
}


