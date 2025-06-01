package com.automationera;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class Advancements implements Consumer<Consumer<Advancement>> {
    private static final String MOD_ID = "automationera";
    @Override
    public void accept(Consumer<Advancement> consumer) {
        // 根成就
        Advancement root = Advancement.Builder.create()
                .display(
                        Items.REDSTONE,
                        Text.translatable("advancements.root.title"),
                        Text.translatable("advancements.root.descr"),
                        new Identifier("textures/block/redstone_ore.png"),
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("root", InventoryChangedCriterion.Conditions.items(Items.REDSTONE))
                .build(consumer, MOD_ID + "/root");


        // 村庄主分支
        Advancement findvillage = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.EMERALD,
                        Text.translatable("advancements.findvillage.title"),
                        Text.translatable("advancements.findvillage.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("findvillage", InventoryChangedCriterion.Conditions.items(Items.EMERALD)) // 可自定义村庄判定
                .build(consumer, MOD_ID + "/findvillage");

        // 农场主分支
        Advancement farm = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.WHEAT,
                        Text.translatable("advancements.farm.title"),
                        Text.translatable("advancements.farm.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("farm", InventoryChangedCriterion.Conditions.items(Items.WHEAT, Items.POTATO, Items.CARROT, Items.BEETROOT))
                .build(consumer, MOD_ID + "/farm");

        // 打怪主分支
        Advancement fight = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.IRON_SWORD,
                        Text.translatable("advancements.fight.title"),
                        Text.translatable("advancements.fight.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("fight", InventoryChangedCriterion.Conditions.items(Items.BONE, Items.ROTTEN_FLESH, Items.STRING))
                .build(consumer, MOD_ID + "/fight");

        // 刷石机分支
        Advancement stonefarm = createStackAdvancement(consumer, root, "stonefarm", Items.COBBLESTONE, 9, Items.COBBLESTONE);
        Advancement mobfarm = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.BONE,
                        Text.translatable("advancements.mobfarm.title"),
                        Text.translatable("advancements.mobfarm.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("a1",  FullStackCriterion.createCriterion(Items.BONE, 9))
                .criterion("a2",  FullStackCriterion.createCriterion(Items.BONE, 9))
                .build(consumer, MOD_ID + "/" + "mobfarm");
        Advancement sandfarm = createStackAdvancement(consumer, mobfarm, "sandfarm", Items.SAND, 9, Items.SAND);
        Advancement shulkerfarm = createStackAdvancement(consumer, mobfarm, "shulkerfarm", Items.SHULKER_SHELL, 27, Items.SHULKER_SHELL);
        Advancement witchfarm = createStackAdvancement(consumer, mobfarm, "witchfarm", Items.REDSTONE, 27, Items.REDSTONE);
        Advancement slimefarm = createStackAdvancement(consumer, mobfarm, "slimefarm", Items.SLIME_BALL, 27, Items.SLIME_BALL);

        // 刷铁机分支
        Advancement ironfarm = createStackAdvancement(consumer, root, "ironfarm", Items.IRON_INGOT, 4, Items.IRON_INGOT);
        Advancement furnacegroup = createStackAdvancement(consumer, ironfarm, "furnacegroup", Items.COBBLESTONE, 9, Items.FURNACE);
        Advancement bigfurnace = createStackAdvancement(consumer, furnacegroup, "bigfurnace", Items.SMOOTH_STONE, 27, Items.BLAST_FURNACE);

        // 树厂分支（任意原木/树种需自定义ItemPredicate）
        Advancement woodfarm = createStackAdvancement(consumer, root, "woodfarm", Items.OAK_LOG, 9, Items.OAK_LOG);
        Advancement bonefarm = createStackAdvancement(consumer, woodfarm, "bonefarm", Items.BONE_MEAL, 27, Items.BONE_MEAL);
        Advancement alltree = createStackAdvancement(consumer, bonefarm, "alltree", Items.OAK_LOG, 27, Items.OAK_LOG);

        // 村庄-农场-交易所分支
        Advancement autofarm = createStackAdvancement(consumer, findvillage, "autofarm", Items.WHEAT, 4, Items.WHEAT); // 任意农作物需自定义
        Advancement tradingpost = Advancement.Builder.create()
                .parent(autofarm)
                .display(
                        Items.EMERALD_BLOCK,
                        Text.translatable("advancements.tradingpost.title"),
                        Text.translatable("advancements.tradingpost.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("tradingpost", new ImpossibleCriterion.Conditions()) // 需自定义交易次数判定
                .build(consumer, MOD_ID + "/tradingpost");
        Advancement silk = Advancement.Builder.create()
                .parent(tradingpost)
                .display(
                        Items.SHEARS,
                        Text.translatable("advancements.silk.title"),
                        Text.translatable("advancements.silk.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("silk", InventoryChangedCriterion.Conditions.items(Items.SHEARS)) // 精准采集
                .build(consumer, MOD_ID + "/silk");
        Advancement hoglinfarm = createStackAdvancement(consumer, silk, "hoglinfarm", Items.PORKCHOP, 9, Items.PORKCHOP);
        Advancement railfarm = createStackAdvancement(consumer, silk, "railfarm", Items.RAIL, 4, Items.RAIL); // 需自定义所有铁轨类型
        Advancement netherhighway = Advancement.Builder.create()
                .parent(silk)
                .display(
                        Items.BLUE_ICE,
                        Text.translatable("advancements.netherhighway.title"),
                        Text.translatable("advancements.netherhighway.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("netherhighway", new ImpossibleCriterion.Conditions()) // 需自定义下界放置浮冰判定
                .build(consumer, MOD_ID + "/netherhighway");
        Advancement tripwire = createStackAdvancement(consumer, tradingpost, "tripwire", Items.TRIPWIRE_HOOK, 27, Items.TRIPWIRE_HOOK);
        Advancement stringfarm = createStackAdvancement(consumer, tradingpost, "stringfarm", Items.STRING, 27, Items.STRING);
        Advancement raidfarm = createStackAdvancement(consumer, tradingpost, "raidfarm", Items.EMERALD, 9, Items.EMERALD);

        // 农场分支
        Advancement bamboo = createStackAdvancement(consumer, farm, "bamboo", Items.BAMBOO, 4, Items.BAMBOO);
        Advancement sugarcane = createStackAdvancement(consumer, farm, "sugarcane", Items.SUGAR_CANE, 4, Items.SUGAR_CANE);
        Advancement pumpkinfarm = createStackAdvancement(consumer, farm, "pumpkinfarm", Items.PUMPKIN, 4, Items.PUMPKIN);
        Advancement melonfarm = createStackAdvancement(consumer, farm, "melonfarm", Items.MELON, 4, Items.MELON);

        // 打怪分支
        Advancement blaze = createStackAdvancement(consumer, fight, "blaze", Items.BLAZE_ROD, 27, Items.BLAZE_ROD);
        Advancement expcap = Advancement.Builder.create()
                .parent(blaze)
                .display(
                        Items.EXPERIENCE_BOTTLE,
                        Text.translatable("advancements.expcap.title"),
                        Text.translatable("advancements.expcap.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("expcap", new ImpossibleCriterion.Conditions()) // 需自定义经验等级判定
                .build(consumer, MOD_ID + "/expcap");
        Advancement endermanfarm = createStackAdvancement(consumer, expcap, "endermanfarm", Items.ENDER_PEARL, 27, Items.ENDER_PEARL);
        Advancement pigmanfarm = createStackAdvancement(consumer, endermanfarm, "pigmanfarm", Items.GOLD_INGOT, 27, Items.GOLD_INGOT);
        Advancement piglintrade = Advancement.Builder.create()
                .parent(pigmanfarm)
                .display(
                        Items.QUARTZ,
                        Text.translatable("advancements.piglintrade.title"),
                        Text.translatable("advancements.piglintrade.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("quartz", FullStackCriterion.createCriterion(Items.QUARTZ, 4))
                .criterion("obsidian", FullStackCriterion.createCriterion(Items.OBSIDIAN, 4))
                .criterion("enchanted_book", InventoryChangedCriterion.Conditions.items(Items.ENCHANTED_BOOK))
                .build(consumer, MOD_ID + "/piglintrade");
        Advancement doublepigman = createStackAdvancement(consumer, piglintrade, "doublepigman", Items.GOLD_INGOT, 27, Items.GOLD_INGOT);
        Advancement witherrose = createStackAdvancement(consumer, pigmanfarm, "witherrose", Items.WITHER_ROSE, 9, Items.WITHER_ROSE);
        Advancement witherskel = Advancement.Builder.create()
                .parent(witherrose)
                .display(
                        Items.WITHER_SKELETON_SKULL,
                        Text.translatable("advancements.witherskel.title"),
                        Text.translatable("advancements.witherskel.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("skull", FullStackCriterion.createCriterion(Items.WITHER_SKELETON_SKULL, 4))
                .criterion("coal", FullStackCriterion.createCriterion(Items.COAL, 4))
                .build(consumer, MOD_ID + "/witherskel");
        Advancement witherkill = createStackAdvancement(consumer, witherskel, "witherkill", Items.NETHER_STAR, 4, Items.NETHER_STAR);
    }

    private Advancement createStackAdvancement(
            Consumer<Advancement> consumer,
            Advancement parent,
            String advancementId,
            Item targetItem,
            int requiredStacks,
            Item displayItem
    ) {
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
                .criterion("a1",  FullStackCriterion.createCriterion(targetItem, requiredStacks))
                .build(consumer, MOD_ID + "/" + advancementId);
        return parent;
    }
}


