package com.automationera;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import com.automationera.advance.PlacedBlockInNetherCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class Advancements implements Consumer<Consumer<Advancement>> {
    private static final String MOD_ID = "automationera";
    @Override
    public void accept(Consumer<Advancement> consumer) {

        // 自动化工厂分支
        Advancement automaticFactory = Advancement.Builder.create()
                .display(
                        Items.REDSTONE,
                        Text.translatable("advancements.automaticfactory.title"),
                        Text.translatable("advancements.automaticfactory.descr"),
                        new Identifier("textures/block/redstone_ore.png"),
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("crafting", InventoryChangedCriterion.Conditions.items(Items.REDSTONE))
                .build(consumer, "automaticfactory");

        // 刷石机分支
        Advancement stonefarm = createStackAdvancement(consumer, automaticFactory, "stonefarm", Items.COBBLESTONE, 9, Items.COBBLESTONE, AdvancementFrame.TASK);
        Advancement copperfarm = Advancement.Builder.create()
                .parent(stonefarm)
                .display(
                        Items.EXPOSED_COPPER,
                        Text.translatable("advancements.copperfarm.title"),
                        Text.translatable("advancements.copperfarm.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("exposed", FullStackCriterion.createCriterion(Items.EXPOSED_COPPER, 2))
                .criterion("weathered", FullStackCriterion.createCriterion(Items.WEATHERED_COPPER, 2))
                .criterion("oxidized", FullStackCriterion.createCriterion(Items.OXIDIZED_COPPER, 2))
                .build(consumer, "copperfarm");
        Advancement icefarm = createStackAdvancement(consumer, stonefarm, "icefarm", Items.ICE, 9, Items.ICE, AdvancementFrame.TASK);
        Advancement basaltfarm = createStackAdvancement(consumer, stonefarm, "basaltfarm", Items.BASALT, 9, Items.BASALT, AdvancementFrame.TASK);
        Advancement dripstonefarm = createStackAdvancement(consumer, stonefarm, "dripstonefarm", Items.DRIPSTONE_BLOCK, 9, Items.DRIPSTONE_BLOCK, AdvancementFrame.TASK);

        // 熔炉组分支
        Advancement furnacegroup = Advancement.Builder.create()
                .parent(stonefarm)
                .display(
                        Items.FURNACE,
                        Text.translatable("advancements.furnacegroup.title"),
                        Text.translatable("advancements.furnacegroup.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("furnacegroup2", FullStackCriterion.createCriterion(Items.GLASS, 4))
                .build(consumer, MOD_ID + "/furnacegroup");
        Advancement bigfurnace = createStackAdvancement(consumer, furnacegroup, "bigfurnace", Items.SMOOTH_STONE, 27, Items.BLAST_FURNACE, AdvancementFrame.TASK);

        // 刷铁机分支
        Advancement ironfarm = createStackAdvancement(consumer, automaticFactory, "ironfarm", Items.IRON_INGOT, 4, Items.IRON_INGOT, AdvancementFrame.TASK);
        Advancement railfarm = createStackAdvancement(consumer, ironfarm, "railfarm", Items.RAIL, 9, Items.RAIL, AdvancementFrame.TASK);
        Advancement amethystfarm = createStackAdvancement(consumer, ironfarm, "amethystfarm", Items.AMETHYST_SHARD, 9, Items.AMETHYST_SHARD, AdvancementFrame.TASK);

        // 刷沙机分支
        Advancement sandfarm = createStackAdvancement(consumer, ironfarm, "sandfarm", Items.SAND, 9, Items.SAND, AdvancementFrame.TASK);
        Advancement clayfarm = createStackAdvancement(consumer, sandfarm, "clayfarm", Items.CLAY, 9, Items.CLAY, AdvancementFrame.TASK);
        Advancement dirtfarm = createStackAdvancement(consumer, sandfarm, "dirtfarm", Items.DIRT, 27, Items.DIRT, AdvancementFrame.TASK);

        // 树厂分支
        Advancement woodfarm = createStackAdvancement(consumer, automaticFactory, "woodfarm", Items.OAK_LOG, 9, Items.OAK_LOG, AdvancementFrame.TASK);
        Advancement bonefarm = createStackAdvancement(consumer, woodfarm, "bonefarm", Items.BONE_MEAL, 9, Items.BONE_MEAL, AdvancementFrame.TASK);
        Advancement alltree = createStackAdvancement(consumer, bonefarm, "alltree", Items.OAK_LOG, 27, Items.OAK_LOG, AdvancementFrame.TASK);

        // 骨粉机分支
        Advancement leaffarm = createStackAdvancement(consumer, bonefarm, "leaffarm", Items.OAK_LEAVES, 9, Items.OAK_LEAVES, AdvancementFrame.TASK);
        Advancement fungusfarm = createStackAdvancement(consumer, bonefarm, "fungusfarm", Items.CRIMSON_STEM, 9, Items.CRIMSON_STEM, AdvancementFrame.TASK);

        // 地狱高速
        Advancement netherhighway = Advancement.Builder.create()
                .parent(automaticFactory)
                .display(
                        Items.PACKED_ICE,
                        Text.translatable("advancements.netherhighway.title"),
                        Text.translatable("advancements.netherhighway.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("ice", PlacedBlockInNetherCriterion.conditions(
                        BlockPredicate.Builder.create().blocks(net.minecraft.block.Blocks.PACKED_ICE).build(),
                        LocationPredicate.Builder.create().dimension(World.NETHER).build()
                ))
                .build(consumer, "netherhighway");

        // 生物工程分支
        Advancement agriculture = Advancement.Builder.create()
                .display(
                        Items.HAY_BLOCK,
                        Text.translatable("advancements.agriculture.title"),
                        Text.translatable("advancements.agriculture.descr"),
                        new Identifier("textures/block/smooth_stone.png"),
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("agriculture", InventoryChangedCriterion.Conditions.items(Items.WHEAT_SEEDS))
                .build(consumer, "agriculture");
        Advancement mobfarm = Advancement.Builder.create()
                .parent(agriculture)
                .display(
                        Items.ROTTEN_FLESH,
                        Text.translatable("advancements.mobfarm.title"),
                        Text.translatable("advancements.mobfarm.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("a1",  FullStackCriterion.createCriterion(Items.BONE, 4))
                .criterion("a2",  FullStackCriterion.createCriterion(Items.ROTTEN_FLESH, 4))
                .criterion("a3",  FullStackCriterion.createCriterion(Items.GUNPOWDER, 4))
                .build(consumer, "mobfarm");

        // 畜牧业分支
        Advancement animalfarm = Advancement.Builder.create()
                .parent(agriculture)
                .display(
                        Items.MUTTON,
                        Text.translatable("advancements.animalfarm.title"),
                        Text.translatable("advancements.animalfarm.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("animalfarm", BredAnimalsCriterion.Conditions.create(EntityPredicate.Builder.create()))
                .build(consumer, "animalfarm");
        Advancement turtlefarm = createStackAdvancement(consumer, animalfarm, "turtlefarm", Items.TURTLE_EGG, 4, Items.TURTLE_EGG, AdvancementFrame.TASK);
        Advancement snowfarm = createStackAdvancement(consumer, animalfarm, "snowfarm", Items.SNOW_BLOCK, 9, Items.SNOW_BLOCK, AdvancementFrame.TASK);
        Advancement woolfarm = createStackAdvancement(consumer, animalfarm, "woolfarm", Items.WHITE_WOOL, 4, Items.WHITE_WOOL, AdvancementFrame.TASK);
        Advancement squidfarm = Advancement.Builder.create()
                .parent(turtlefarm)
                .display(
                        Items.INK_SAC,
                        Text.translatable("advancements.squidfarm.title"),
                        Text.translatable("advancements.squidfarm.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("ink", FullStackCriterion.createCriterion(Items.INK_SAC, 4))
                .criterion("glow", FullStackCriterion.createCriterion(Items.GLOW_INK_SAC, 4))
                .build(consumer, "squidfarm");
        Advancement snifferfarm = Advancement.Builder.create()
                .parent(woolfarm)
                .display(
                        Items.TORCHFLOWER_SEEDS,
                        Text.translatable("advancements.snifferfarm.title"),
                        Text.translatable("advancements.snifferfarm.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("torchflower", FullStackCriterion.createCriterion(Items.TORCHFLOWER_SEEDS, 4))
                .criterion("pitcher", FullStackCriterion.createCriterion(Items.PITCHER_POD, 4))
                .build(consumer, "snifferfarm");

        // 自动农场分支
        Advancement autofarm = Advancement.Builder.create()
                .parent(agriculture)
                .display(
                        Items.POTATO,
                        Text.translatable("advancements.autofarm.title"),
                        Text.translatable("advancements.autofarm.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("potato", FullStackCriterion.createCriterion(Items.POTATO, 4))
                .criterion("carrot", FullStackCriterion.createCriterion(Items.CARROT, 4))
                .build(consumer, "autofarm");
        Advancement wheatfarm = createStackAdvancement(consumer, autofarm, "wheatfarm", Items.WHEAT, 9, Items.WHEAT, AdvancementFrame.TASK);
        Advancement sugarcane = createStackAdvancement(consumer, wheatfarm, "sugarcane", Items.SUGAR_CANE, 4, Items.SUGAR_CANE, AdvancementFrame.TASK);
        Advancement cactusfarm = createStackAdvancement(consumer, sugarcane, "cactusfarm", Items.CACTUS, 4, Items.CACTUS, AdvancementFrame.TASK);
        Advancement kelpfarm = createStackAdvancement(consumer, sugarcane, "kelpfarm", Items.KELP, 9, Items.KELP, AdvancementFrame.TASK);
        Advancement bamboo = createStackAdvancement(consumer, sugarcane, "bamboo", Items.BAMBOO, 4, Items.BAMBOO, AdvancementFrame.TASK);
        Advancement pumpkinfarm = createStackAdvancement(consumer, autofarm, "pumpkinfarm", Items.PUMPKIN, 4, Items.PUMPKIN, AdvancementFrame.TASK);
        Advancement melonfarm = createStackAdvancement(consumer, pumpkinfarm, "melonfarm", Items.MELON, 4, Items.MELON, AdvancementFrame.TASK);
        Advancement netherwartfarm = createStackAdvancement(consumer, wheatfarm, "netherwartfarm", Items.NETHER_WART, 9, Items.NETHER_WART, AdvancementFrame.TASK);
        Advancement sweetberryfarm = createStackAdvancement(consumer, wheatfarm, "sweetberryfarm", Items.SWEET_BERRIES, 9, Items.SWEET_BERRIES, AdvancementFrame.TASK);
        Advancement seapicklefarm = createStackAdvancement(consumer, autofarm, "seapicklefarm", Items.SEA_PICKLE, 4, Items.SEA_PICKLE, AdvancementFrame.TASK);
        Advancement coralfarm = createStackAdvancement(consumer, seapicklefarm, "coralfarm", Items.BRAIN_CORAL_FAN, 4, Items.BRAIN_CORAL_FAN, AdvancementFrame.TASK);
        Advancement hoglinfarm = createStackAdvancement(consumer, autofarm, "hoglinfarm", Items.COOKED_PORKCHOP, 9, Items.COOKED_PORKCHOP, AdvancementFrame.GOAL);
        Advancement lavachicken = Advancement.Builder.create()
                .parent(hoglinfarm)
                .display(
                        Items.COOKED_CHICKEN,
                        Text.translatable("advancements.lavachicken.title"),
                        Text.translatable("advancements.lavachicken.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("chicken", FullStackCriterion.createCriterion(Items.COOKED_CHICKEN, 4))
                .criterion("egg", FullStackCriterion.createCriterion(Items.EGG, 16))
                .build(consumer, "lavachicken");
        Advancement cowfarm = createStackAdvancement(consumer, hoglinfarm, "cowfarm", Items.COOKED_BEEF, 4, Items.COOKED_BEEF, AdvancementFrame.TASK);
        Advancement beehive = Advancement.Builder.create()
                .parent(autofarm)
                .display(
                        Items.HONEY_BLOCK,
                        Text.translatable("advancements.beehive.title"),
                        Text.translatable("advancements.beehive.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("honey_block", FullStackCriterion.createCriterion(Items.HONEY_BLOCK, 4))
                .criterion("honeycomb", FullStackCriterion.createCriterion(Items.HONEYCOMB, 2))
                .build(consumer, "beehive");
        Advancement flowerfarm = createStackAdvancement(consumer, beehive, "flowerfarm", Items.DANDELION, 4, Items.DANDELION, AdvancementFrame.TASK);

        // 刷怪塔分支
        Advancement shulkerfarm = createStackAdvancement(consumer, mobfarm, "shulkerfarm", Items.SHULKER_SHELL, 9, Items.SHULKER_SHELL, AdvancementFrame.GOAL);
        Advancement witchfarm = createStackAdvancement(consumer, mobfarm, "witchfarm", Items.REDSTONE, 9, Items.REDSTONE, AdvancementFrame.TASK);
        Advancement slimefarm = createStackAdvancement(consumer, mobfarm, "slimefarm", Items.SLIME_BALL, 9, Items.SLIME_BALL, AdvancementFrame.TASK);
        Advancement blaze = createStackAdvancement(consumer, mobfarm, "blaze", Items.BLAZE_ROD, 9, Items.BLAZE_ROD, AdvancementFrame.TASK);
        Advancement endermanfarm = createStackAdvancement(consumer, mobfarm, "endermanfarm", Items.ENDER_PEARL, 9, Items.ENDER_PEARL, AdvancementFrame.TASK);
        Advancement pigmanfarm = createStackAdvancement(consumer, endermanfarm, "pigmanfarm", Items.GOLD_INGOT, 9, Items.GOLD_INGOT, AdvancementFrame.GOAL);
        Advancement piglintrade = Advancement.Builder.create()
                .parent(endermanfarm)
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
                .build(consumer, "piglintrade");
        Advancement doublepigman = createStackAdvancement(consumer, pigmanfarm, "doublepigman", Items.GOLD_BLOCK, 27, Items.GOLD_BLOCK, AdvancementFrame.CHALLENGE);
        Advancement witherrose = createStackAdvancement(consumer, mobfarm, "witherrose", Items.WITHER_ROSE, 9, Items.WITHER_ROSE, AdvancementFrame.TASK);
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
                .build(consumer, "witherskel");
        Advancement witherkill = createStackAdvancement(consumer, witherskel, "witherkill", Items.NETHER_STAR, 4, Items.NETHER_STAR, AdvancementFrame.CHALLENGE);
        Advancement guardianfarm = Advancement.Builder.create()
                .parent(mobfarm)
                .display(
                        Items.PRISMARINE_SHARD,
                        Text.translatable("advancements.guardianfarm.title"),
                        Text.translatable("advancements.guardianfarm.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("guardian", FullStackCriterion.createCriterion(Items.PRISMARINE_SHARD, 4))
                .build(consumer, "guardianfarm");
        Advancement drownedfarm = Advancement.Builder.create()
                .parent(guardianfarm)
                .display(
                        Items.TRIDENT,
                        Text.translatable("advancements.drownedfarm.title"),
                        Text.translatable("advancements.drownedfarm.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("flesh", FullStackCriterion.createCriterion(Items.ROTTEN_FLESH, 4))
                .criterion("copper", FullStackCriterion.createCriterion(Items.COPPER_INGOT, 4))
                .criterion("trident", InventoryChangedCriterion.Conditions.items(Items.TRIDENT))
                .build(consumer, "drownedfarm");

        // 村民工程分支
        Advancement village = Advancement.Builder.create()
                .parent(mobfarm)
                .display(
                        Items.BELL,
                        Text.translatable("advancements.village.title"),
                        Text.translatable("advancements.village.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("killed_iron_golem", OnKilledCriterion.Conditions.createPlayerKilledEntity(
                        EntityPredicate.Builder.create()
                                .type(EntityType.IRON_GOLEM)
                                .build()
                ))
                .build(consumer, "village");
        Advancement tradingpost = Advancement.Builder.create()
                .parent(village)
                .display(
                        Items.VILLAGER_SPAWN_EGG,
                        Text.translatable("advancements.tradingpost.title"),
                        Text.translatable("advancements.tradingpost.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("trading", InventoryChangedCriterion.Conditions.items(Items.EMERALD))
                .build(consumer, "tradingpost");
        Advancement raidfarm = createStackAdvancement(consumer, village, "raidfarm", Items.EMERALD, 9, Items.EMERALD, AdvancementFrame.TASK);

        // 空置域分支
        Advancement perimeter = Advancement.Builder.create()
                .display(
                        Items.BEDROCK,
                        Text.translatable("advancements.perimeter.title"),
                        Text.translatable("advancements.perimeter.descr"),
                        new Identifier("textures/block/bedrock.png"),
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("bedrock", InventoryChangedCriterion.Conditions.items(Items.BEDROCK))
                .build(consumer, "perimeter");
    }

    private Advancement createStackAdvancement(
            Consumer<Advancement> consumer,
            Advancement parent,
            String advancementId,
            Item targetItem,
            int requiredStacks,
            Item displayItem,
            AdvancementFrame type
    ) {
        return Advancement.Builder.create()
                .parent(parent)
                .display(
                        displayItem,
                        Text.translatable("advancements."  + advancementId + ".title"),
                        Text.translatable("advancements."  + advancementId + ".descr"),
                        null,
                        type,
                        true, true, false
                )
                .criterion("a1",  FullStackCriterion.createCriterion(targetItem, requiredStacks))
                .build(consumer, advancementId);
    }
}


