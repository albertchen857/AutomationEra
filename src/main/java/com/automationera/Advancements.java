package com.automationera;

import com.automationera.advance.FullShulkerBoxCriterion;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.*;
import com.automationera.advance.PlacedBlockInNetherCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Advancements implements Consumer<Consumer<Advancement>> {
    private static final String MOD_ID = "automationera";
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/Advancements");
    private final Map<String, Advancement> advancementMap = new HashMap<>();
    private int createAdvancement = 0;

    public void autoRoot(Consumer<Advancement> consumer){
        Advancement automaticFactory = Advancement.Builder.create()
                .display(
                        Items.REDSTONE,
                        Text.translatable("advancements.automaticfactory.title"),
                        Text.translatable("advancements.automaticfactory.descr"),
                        new Identifier("textures/block/redstone_ore.png"),
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("a", InventoryChangedCriterion.Conditions.items(Items.REDSTONE))
                .build(consumer, "automaticfactory");
        advancementMap.put("automaticfactory", automaticFactory);

        Advancement village = Advancement.Builder.create()//村民工程类
                .parent(automaticFactory)
                .display(
                        Items.BELL,
                        Text.translatable("advancements.village.title"),
                        Text.translatable("advancements.village.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("a", OnKilledCriterion.Conditions.createPlayerKilledEntity(
                        EntityPredicate.Builder.create().type(EntityType.IRON_GOLEM)
                ))
                .build(consumer, "village");
        advancementMap.put("village", village);
        registerSingleItemAdvancement(consumer, "stringfarm", Items.STRING, AdvancementFrame.TASK, Items.STRING, 9, village);
        registerSingleItemAdvancement(consumer, "tripwire", Items.TRIPWIRE_HOOK, AdvancementFrame.TASK, Items.TRIPWIRE_HOOK, 9, village);
        registerSingleItemAdvancement(consumer, "raidfarm", Items.EMERALD, AdvancementFrame.TASK, Items.EMERALD, 9, village);
        Advancement tradingpost = Advancement.Builder.create()
                .parent(village)
                .display(
                        Items.EMERALD,
                        Text.translatable("advancements.tradingpost.title"),
                        Text.translatable("advancements.tradingpost.descr"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("trading_post", new ImpossibleCriterion.Conditions())
                .build(consumer, "tradingpost");
        advancementMap.put("tradingpost", tradingpost);


        registerSingleItemAdvancement(consumer, "stonefarm", Items.COBBLESTONE, AdvancementFrame.GOAL, Items.COBBLESTONE, 9, automaticFactory); // 圆石农场
        registerSingleItemAdvancement(consumer, "icefarm", Items.ICE, AdvancementFrame.TASK, Items.ICE, 9, advancementMap.get("stonefarm")); // 冰农场
        registerSingleItemAdvancement(consumer, "basaltfarm", Items.BASALT, AdvancementFrame.TASK, Items.BASALT, 9, advancementMap.get("stonefarm")); // 玄武岩农场
        registerSingleItemAdvancement(consumer, "dripstonefarm", Items.DRIPSTONE_BLOCK, AdvancementFrame.TASK, Items.DRIPSTONE_BLOCK, 9, advancementMap.get("stonefarm")); // 滴水石农场
        registerItemSetAdvancement(consumer, "furnacegroup", Items.FURNACE, Set.of(Items.GLASS,Items.STONE), 9, AdvancementFrame.TASK, advancementMap.get("stonefarm")); // 熔炉组
        registerSingleItemAdvancement(consumer, "ironfarm", Items.IRON_INGOT, AdvancementFrame.GOAL, Items.IRON_INGOT, 9, automaticFactory); // 铁农场
        registerSingleItemAdvancement(consumer, "amethystfarm", Items.AMETHYST_SHARD, AdvancementFrame.TASK, Items.AMETHYST_SHARD, 9, advancementMap.get("ironfarm")); // 紫水晶农场
        registerSingleItemAdvancement(consumer, "sandfarm", Items.SAND, AdvancementFrame.GOAL, Items.SAND, 9, advancementMap.get("ironfarm")); // 沙子农场
        registerSingleItemAdvancement(consumer, "dirtfarm", Items.DIRT, AdvancementFrame.TASK, Items.DIRT, 9, advancementMap.get("sandfarm")); // 泥土农场
        registerSingleItemAdvancement(consumer, "clayfarm", Items.CLAY, AdvancementFrame.TASK, Items.CLAY, 9, advancementMap.get("dirtfarm")); // 粘土农场
        registerSingleItemAdvancement(consumer, "mudfarm", Items.MUD, AdvancementFrame.TASK, Items.MUD, 9, advancementMap.get("dirtfarm")); // 粘土农场
        registerSingleItemAdvancement(consumer, "bonefarm", Items.BONE_MEAL, AdvancementFrame.GOAL, Items.BONE_MEAL, 9, advancementMap.get("woodfarm")); // 骨粉农场
        registerSingleItemAdvancement(consumer, "obsidianfarm", Items.OBSIDIAN, AdvancementFrame.TASK, Items.OBSIDIAN, 9, advancementMap.get("sandfarm")); // 黑曜石农场

        Advancement abovenether = Advancement.Builder.create()
                .parent(automaticFactory)
                .display(
                        Items.BEDROCK,
                        Text.translatable("advancements.abovenether.title"),
                        Text.translatable("advancements.abovenether.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("above_nether", new ImpossibleCriterion.Conditions())
                .build(consumer, "abovenether");
        advancementMap.put("abovenether", abovenether);
        Advancement netherhighway = Advancement.Builder.create()
                .parent(abovenether)
                .display(
                        Items.BLUE_ICE,
                        Text.translatable("advancements.netherhighway.title"),
                        Text.translatable("advancements.netherhighway.descr"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("ice", PlacedBlockInNetherCriterion.conditions(
                        BlockPredicate.Builder.create().blocks(net.minecraft.block.Blocks.PACKED_ICE).build(),
                        LocationPredicate.Builder.create().dimension(World.NETHER).build()
                ))
                .build(consumer, "netherhighway");
        advancementMap.put("netherhighway", netherhighway);

        registerItemSetAdvancement(consumer, "woodfarm",Items.OAK_LOG, Set.of( // 木材农场
                Items.OAK_LOG,
                Items.SPRUCE_LOG,
                Items.BIRCH_LOG,
                Items.JUNGLE_LOG,
                Items.ACACIA_LOG,
                Items.DARK_OAK_LOG,
                Items.MANGROVE_LOG,
                Items.CHERRY_LOG,
                Items.CRIMSON_STEM,
                Items.WARPED_STEM
        ), 9, AdvancementFrame.GOAL, automaticFactory);

        registerItemSetAdvancement(consumer, "railfarm",Items.POWERED_RAIL, Set.of( // 铁轨农场
                Items.RAIL,
                Items.POWERED_RAIL,
                Items.DETECTOR_RAIL,
                Items.ACTIVATOR_RAIL
        ), 9, AdvancementFrame.TASK, advancementMap.get("ironfarm"));

        registerItemSetAdvancement(consumer, "copperfarm",Items.EXPOSED_COPPER, Set.of( // 铜农场
                Items.EXPOSED_COPPER,
                Items.WEATHERED_COPPER,
                Items.OXIDIZED_COPPER
        ), 4, AdvancementFrame.TASK, advancementMap.get("stonefarm"));

        registerItemSetAdvancement(consumer, "leaffarm",Items.OAK_LEAVES, Set.of( // 树叶农场
                Items.OAK_LEAVES,
                Items.SPRUCE_LEAVES,
                Items.BIRCH_LEAVES,
                Items.JUNGLE_LEAVES,
                Items.ACACIA_LEAVES,
                Items.DARK_OAK_LEAVES,
                Items.MANGROVE_LEAVES,
                Items.CHERRY_LEAVES,
                Items.AZALEA_LEAVES,
                Items.FLOWERING_AZALEA_LEAVES
        ), 9, AdvancementFrame.TASK, advancementMap.get("bonefarm"));

        registerItemSetAdvancement(consumer, "fungusfarm",Items.CRIMSON_STEM, Set.of( // 菌类农场
                Items.CRIMSON_STEM,
                Items.WARPED_STEM
        ), 9, AdvancementFrame.TASK, advancementMap.get("bonefarm"));

        registerItemSetAdvancement(consumer, "mushroomfarm",Items.RED_MUSHROOM_BLOCK, Set.of( // 菌类农场
                Items.BROWN_MUSHROOM,
                Items.RED_MUSHROOM,
                Items.RED_MUSHROOM_BLOCK,
                Items.BROWN_MUSHROOM_BLOCK
        ), 9, AdvancementFrame.TASK, advancementMap.get("bonefarm"));

        registerItemSetAdvancement(consumer, "concretefarm", Items.WHITE_CONCRETE, Set.of( // 混凝土农场
                Items.WHITE_CONCRETE,
                Items.ORANGE_CONCRETE,
                Items.MAGENTA_CONCRETE,
                Items.LIGHT_BLUE_CONCRETE,
                Items.YELLOW_CONCRETE,
                Items.LIME_CONCRETE,
                Items.PINK_CONCRETE,
                Items.GRAY_CONCRETE,
                Items.LIGHT_GRAY_CONCRETE,
                Items.CYAN_CONCRETE,
                Items.PURPLE_CONCRETE,
                Items.BLUE_CONCRETE,
                Items.BROWN_CONCRETE,
                Items.GREEN_CONCRETE,
                Items.RED_CONCRETE,
                Items.BLACK_CONCRETE
        ), 9, AdvancementFrame.TASK, advancementMap.get("sandfarm"));
    }

    public void agricultureRoot(Consumer<Advancement> consumer){
        Advancement agriculture = Advancement.Builder.create()
                .display(
                        Items.HAY_BLOCK,
                        Text.translatable("advancements.agriculture.title"),
                        Text.translatable("advancements.agriculture.descr"),
                        new Identifier("textures/block/smooth_stone.png"),
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("a", InventoryChangedCriterion.Conditions.items(Items.WHEAT_SEEDS))
                .build(consumer, "agriculture");
        advancementMap.put("agriculture", agriculture);


        Advancement animalfarm = Advancement.Builder.create()//动物农场类
                .parent(agriculture)
                .display(
                        Items.BEEF,
                        Text.translatable("advancements.animalfarm.title"),
                        Text.translatable("advancements.animalfarm.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("a", BredAnimalsCriterion.Conditions.create(EntityPredicate.ANY, EntityPredicate.ANY, EntityPredicate.ANY))
                .build(consumer, "animalfarm");
        advancementMap.put("animalfarm", animalfarm);
        registerSingleItemAdvancement(consumer, "turtlefarm", Items.TURTLE_EGG, AdvancementFrame.TASK, Items.TURTLE_EGG, 4, advancementMap.get("animalfarm")); // 海龟农场
        registerSingleItemAdvancement(consumer, "snowfarm", Items.SNOW_BLOCK, AdvancementFrame.TASK, Items.SNOW_BLOCK, 9, advancementMap.get("animalfarm")); // 雪农场
        registerItemSetAdvancement(consumer, "woolfarm",Items.WHITE_WOOL, Set.of( // 羊毛农场
                Items.WHITE_WOOL,
                Items.ORANGE_WOOL,
                Items.MAGENTA_WOOL,
                Items.LIGHT_BLUE_WOOL,
                Items.YELLOW_WOOL,
                Items.LIME_WOOL,
                Items.PINK_WOOL,
                Items.GRAY_WOOL,
                Items.LIGHT_GRAY_WOOL,
                Items.CYAN_WOOL,
                Items.PURPLE_WOOL,
                Items.BLUE_WOOL,
                Items.BROWN_WOOL,
                Items.GREEN_WOOL,
                Items.RED_WOOL,
                Items.BLACK_WOOL
        ), 9, AdvancementFrame.TASK, advancementMap.get("animalfarm"));
        registerItemSetAdvancement(consumer, "squidfarm",Items.INK_SAC, Set.of( // 鱿鱼农场
                Items.INK_SAC,
                Items.GLOW_INK_SAC
        ), 9, AdvancementFrame.TASK, advancementMap.get("animalfarm"));
        registerItemSetAdvancement(consumer, "snifferfarm",Items.INK_SAC, Set.of( // 嗅探兽农场
                Items.TORCHFLOWER_SEEDS,
                Items.PITCHER_POD
        ), 9, AdvancementFrame.TASK, advancementMap.get("woolfarm"));
        registerSingleItemAdvancement(consumer, "honeyfarm", Items.BEEHIVE, AdvancementFrame.TASK, Items.HONEY_BLOCK, 4, advancementMap.get("animalfarm")); // 骨粉农场


        registerSingleItemAdvancement(consumer, "mobfarm", Items.ROTTEN_FLESH, AdvancementFrame.TASK, Items.BONE, 4, agriculture); // 怪物农场
        registerSingleItemAdvancement(consumer, "shulkerfarm", Items.SHULKER_SHELL, AdvancementFrame.GOAL, Items.SHULKER_SHELL, 9, advancementMap.get("mobfarm")); // 潜影贝农场
        registerSingleItemAdvancement(consumer, "witchfarm", Items.REDSTONE, AdvancementFrame.TASK, Items.REDSTONE, 9, advancementMap.get("mobfarm")); // 女巫农场
        registerSingleItemAdvancement(consumer, "creeperfarm", Items.GUNPOWDER, AdvancementFrame.TASK, Items.GUNPOWDER, 9, advancementMap.get("mobfarm")); // 女巫农场
        registerSingleItemAdvancement(consumer, "ghastfarm", Items.GHAST_TEAR, AdvancementFrame.TASK, Items.GHAST_TEAR, 4, advancementMap.get("mobfarm")); // 女巫农场
        registerSingleItemAdvancement(consumer, "slimefarm", Items.SLIME_BALL, AdvancementFrame.TASK, Items.SLIME_BALL, 9, advancementMap.get("mobfarm")); // 史莱姆农场
        registerSingleItemAdvancement(consumer, "blaze", Items.BLAZE_ROD, AdvancementFrame.TASK, Items.BLAZE_ROD, 9, advancementMap.get("mobfarm")); // 烈焰人农场
        registerSingleItemAdvancement(consumer, "endermanfarm", Items.ENDER_PEARL, AdvancementFrame.TASK, Items.ENDER_PEARL, 9, advancementMap.get("mobfarm")); // 末影人农场
        registerSingleItemAdvancement(consumer, "pigmanfarm", Items.GOLD_INGOT, AdvancementFrame.GOAL, Items.GOLD_INGOT, 9, advancementMap.get("endermanfarm")); // 猪人农场
        registerSingleItemAdvancement(consumer, "witherrose", Items.WITHER_ROSE, AdvancementFrame.TASK, Items.WITHER_ROSE, 9, advancementMap.get("mobfarm")); // 凋零玫瑰农场
        registerSingleItemAdvancement(consumer, "witherskel", Items.WITHER_SKELETON_SKULL, AdvancementFrame.TASK, Items.WITHER_SKELETON_SKULL, 4, advancementMap.get("witherrose")); // 凋零骷髅农场
        registerSingleItemAdvancement(consumer, "witherkill", Items.NETHER_STAR, AdvancementFrame.CHALLENGE, Items.NETHER_STAR, 9, advancementMap.get("witherskel")); // 凋零击杀农场
        registerSingleItemAdvancement(consumer, "guardianfarm", Items.PRISMARINE_SHARD, AdvancementFrame.TASK, Items.PRISMARINE_SHARD, 9, advancementMap.get("mobfarm")); // 守卫者农场
        registerSingleItemAdvancement(consumer, "drownedfarm", Items.TRIDENT, AdvancementFrame.TASK, Items.TRIDENT, 4, advancementMap.get("guardianfarm")); // 溺尸塔
        registerSingleItemAdvancement(consumer, "magmacubefarm", Items.MAGMA_CREAM, AdvancementFrame.TASK, Items.MAGMA_CREAM, 9, advancementMap.get("witherrose")); // 岩浆怪农场
        registerItemSetAdvancement(consumer, "froglightfarm",Items.OCHRE_FROGLIGHT, Set.of( // 蛙明灯农场
                Items.OCHRE_FROGLIGHT,
                Items.PEARLESCENT_FROGLIGHT,
                Items.VERDANT_FROGLIGHT
        ), 9, AdvancementFrame.TASK, advancementMap.get("witherrose"));
        registerItemSetAdvancement(consumer, "piglintrade",Items.QUARTZ, Set.of( // 猪灵交易所
                Items.QUARTZ,
                Items.CRYING_OBSIDIAN
        ), 9, AdvancementFrame.TASK, advancementMap.get("pigmanfarm"));


        registerItemSetAdvancement(consumer, "autofarm",Items.CARROT, Set.of( // 自动农场
                Items.WHEAT,
                Items.POTATO,
                Items.CARROT
        ), 4, AdvancementFrame.TASK, agriculture);
        registerSingleItemAdvancement(consumer, "wheatfarm", Items.WHEAT, AdvancementFrame.TASK, Items.WHEAT, 9, advancementMap.get("autofarm")); // 小麦农场
        registerSingleItemAdvancement(consumer, "sugarcane", Items.SUGAR_CANE, AdvancementFrame.TASK, Items.SUGAR_CANE, 9, advancementMap.get("wheatfarm")); // 甘蔗农场
        registerSingleItemAdvancement(consumer, "cactusfarm", Items.CACTUS, AdvancementFrame.TASK, Items.CACTUS, 9, advancementMap.get("sugarcane")); // 仙人掌农场
        registerSingleItemAdvancement(consumer, "kelpfarm", Items.KELP, AdvancementFrame.TASK, Items.KELP, 9, advancementMap.get("sugarcane")); // 海带农场
        registerSingleItemAdvancement(consumer, "bamboo", Items.BAMBOO, AdvancementFrame.TASK, Items.BAMBOO, 9, advancementMap.get("sugarcane")); // 竹子农场
        registerSingleItemAdvancement(consumer, "pumpkinfarm", Items.PUMPKIN, AdvancementFrame.TASK, Items.PUMPKIN, 9, advancementMap.get("autofarm")); // 南瓜农场
        registerSingleItemAdvancement(consumer, "melonfarm", Items.MELON, AdvancementFrame.TASK, Items.MELON, 9, advancementMap.get("pumpkinfarm")); // 西瓜农场
        registerSingleItemAdvancement(consumer, "netherwartfarm", Items.NETHER_WART, AdvancementFrame.TASK, Items.NETHER_WART, 9, advancementMap.get("wheatfarm")); // 下界疣农场
        registerSingleItemAdvancement(consumer, "potionfarm", Items.POTION, AdvancementFrame.TASK, Items.POTION, 18, advancementMap.get("netherwartfarm")); // 炼药机
        registerSingleItemAdvancement(consumer, "sweetberryfarm", Items.SWEET_BERRIES, AdvancementFrame.TASK, Items.SWEET_BERRIES, 9, advancementMap.get("wheatfarm")); // 甜浆果农场
        registerSingleItemAdvancement(consumer, "seapicklefarm", Items.SEA_PICKLE, AdvancementFrame.TASK, Items.SEA_PICKLE, 9, advancementMap.get("autofarm")); // 海泡菜农场
        registerSingleItemAdvancement(consumer, "hoglinfarm", Items.COOKED_PORKCHOP, AdvancementFrame.GOAL, Items.COOKED_PORKCHOP, 9, advancementMap.get("autofarm")); // 疣猪兽农场
        registerSingleItemAdvancement(consumer, "lavachicken", Items.FEATHER, AdvancementFrame.TASK, Items.COOKED_CHICKEN, 9, advancementMap.get("hoglinfarm")); // 鸡农场
        registerSingleItemAdvancement(consumer, "cowfarm", Items.COOKED_BEEF, AdvancementFrame.TASK, Items.COOKED_BEEF, 9, advancementMap.get("hoglinfarm")); // 牛农场
        registerSingleItemAdvancement(consumer, "rabbitfarm", Items.RABBIT_HIDE, AdvancementFrame.TASK, Items.RABBIT_HIDE, 9, advancementMap.get("hoglinfarm")); // 兔农场
        registerSingleItemAdvancement(consumer, "beehive", Items.HONEY_BLOCK, AdvancementFrame.TASK, Items.HONEY_BLOCK, 9, advancementMap.get("autofarm")); // 蜂巢农场
        registerSingleItemAdvancement(consumer, "chorusfarm", Items.CHORUS_FLOWER, AdvancementFrame.TASK, Items.CHORUS_FRUIT, 9, advancementMap.get("autofarm")); // 紫菘果农场
        registerItemSetAdvancement(consumer, "coralfarm",Items.BRAIN_CORAL_FAN, Set.of( // 珊瑚农场
                Items.BRAIN_CORAL_FAN,
                Items.BUBBLE_CORAL_FAN,
                Items.FIRE_CORAL_FAN,
                Items.HORN_CORAL_FAN,
                Items.TUBE_CORAL_FAN
        ), 4, AdvancementFrame.TASK, advancementMap.get("seapicklefarm"));
        registerItemSetAdvancement(consumer, "flowerfarm",Items.POPPY, Set.of( // 花机
                Items.DANDELION,
                Items.POPPY,
                Items.BLUE_ORCHID,
                Items.ALLIUM,
                Items.AZURE_BLUET,
                Items.CORNFLOWER,
                Items.LILY_OF_THE_VALLEY,
                Items.ORANGE_TULIP,
                Items.PINK_TULIP,
                Items.RED_TULIP,
                Items.WHITE_TULIP,
                Items.WITHER_ROSE
        ), 4, AdvancementFrame.TASK, advancementMap.get("beehive"));
        registerItemSetAdvancement(consumer, "ripeningmachine",Items.DISPENSER, Set.of( // 催熟机
                Items.COCOA_BEANS,
                Items.GLOW_BERRIES,
                Items.VINE,
                Items.GLOW_LICHEN,
                Items.BIG_DRIPLEAF,
                Items.HANGING_ROOTS,
                Items.PINK_PETALS,
                Items.ROSE_BUSH,
                Items.LILAC,
                Items.PEONY,
                Items.SUNFLOWER

        ), 9, AdvancementFrame.TASK, advancementMap.get("autofarm"));
    }

    public void curcuitrevolutionRoot(Consumer<Advancement> consumer){
        Advancement curcuitrevolution = Advancement.Builder.create()
                .display(
                        Items.BEACON,
                        Text.translatable("advancements.curcuitrevolution.title"),
                        Text.translatable("advancements.curcuitrevolution.descr"),
                        new Identifier("textures/block/bedrock.png"),
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("a", InventoryChangedCriterion.Conditions.items(Items.SHULKER_BOX))
                .build(consumer, "curcuitrevolution");
        advancementMap.put("curcuitrevolution", curcuitrevolution);
        Advancement tntquarry = Advancement.Builder.create()
                .parent(curcuitrevolution)
                .display(
                        Items.TNT,
                        Text.translatable("advancements.tntquarry.title"),
                        Text.translatable("advancements.tntquarry.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("tntquarry", new ImpossibleCriterion.Conditions())
                .build(consumer, "tntquarry");
        Advancement worldeater = Advancement.Builder.create()
                .parent(tntquarry)
                .display(
                        Items.TNT_MINECART,
                        Text.translatable("advancements.worldeater.title"),
                        Text.translatable("advancements.worldeater.descr"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("worldeater", new ImpossibleCriterion.Conditions())
                .build(consumer, "worldeater");
        Advancement perimeter = Advancement.Builder.create()
                .parent(worldeater)
                .display(
                        Items.BEDROCK,
                        Text.translatable("advancements.perimeter.title"),
                        Text.translatable("advancements.perimeter.descr"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("perimeter", new ImpossibleCriterion.Conditions())
                .build(consumer, "perimeter");
        advancementMap.put("perimeter", perimeter);
        Advancement realperimeter = Advancement.Builder.create()
                .parent(perimeter)
                .display(
                        Items.STRUCTURE_VOID,
                        Text.translatable("advancements.realperimeter.title"),
                        Text.translatable("advancements.realperimeter.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("realperimeter", new ImpossibleCriterion.Conditions())
                .build(consumer, "realperimeter");
        Advancement netherperimeter = Advancement.Builder.create()
                .parent(realperimeter)
                .display(
                        Items.LAVA_BUCKET,
                        Text.translatable("advancements.netherperimeter.title"),
                        Text.translatable("advancements.netherperimeter.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("netherperimeter", new ImpossibleCriterion.Conditions())
                .build(consumer, "netherperimeter");

        Advancement fakepeaceful = Advancement.Builder.create()
                .parent(curcuitrevolution)
                .display(
                        Items.BARRIER,
                        Text.translatable("advancements.fakepeaceful.title"),
                        Text.translatable("advancements.fakepeaceful.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("fakepeaceful", new ImpossibleCriterion.Conditions())
                .build(consumer, "fakepeaceful");
        Advancement lightsuppression = Advancement.Builder.create()
                .parent(fakepeaceful)
                .display(
                        Items.LIGHT,
                        Text.translatable("advancements.lightsuppression.title"),
                        Text.translatable("advancements.lightsuppression.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("lightsuppression", new ImpossibleCriterion.Conditions())
                .build(consumer, "lightsuppression");
        registerSingleShulkerBoxAdvancement(consumer, "doublepigman", Items.GOLD_BLOCK, Items.GOLD_INGOT, 27, AdvancementFrame.TASK, curcuitrevolution); // 双倍猪人农场
        registerSingleShulkerBoxAdvancement(consumer, "bigfurnace", Items.BLAST_FURNACE, Items.SMOOTH_STONE, 27, AdvancementFrame.TASK, curcuitrevolution); // 大型熔炉
        registerMultiShulkerBoxAdvancement(consumer, "alltree", Items.OAK_WOOD, Set.of( // 全木材农场
                Items.OAK_LOG,
                Items.SPRUCE_LOG,
                Items.BIRCH_LOG,
                Items.JUNGLE_LOG,
                Items.ACACIA_LOG,
                Items.DARK_OAK_LOG,
                Items.MANGROVE_LOG,
                Items.CHERRY_LOG,
                Items.CRIMSON_STEM,
                Items.WARPED_STEM
        ), 27, AdvancementFrame.TASK, curcuitrevolution);
        registerMultiShulkerBoxAdvancement(consumer, "perimobfarm", Items.ROTTEN_FLESH, Set.of(Items.BONE, Items.ROTTEN_FLESH), 27, AdvancementFrame.CHALLENGE, perimeter);
        registerSingleShulkerBoxAdvancement(consumer, "pericreeperfarm", Items.GUNPOWDER, Items.GUNPOWDER, 27, AdvancementFrame.CHALLENGE, perimeter);
        registerSingleShulkerBoxAdvancement(consumer, "perislimefarm", Items.SLIME_BLOCK, Items.SLIME_BALL, 27, AdvancementFrame.CHALLENGE, perimeter);
        registerSingleShulkerBoxAdvancement(consumer, "periwitherskel", Items.WITHER_SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, 27, AdvancementFrame.CHALLENGE, perimeter);
        registerSingleShulkerBoxAdvancement(consumer, "periguardianfarm", Items.SEA_LANTERN, Items.SEA_LANTERN, 27, AdvancementFrame.CHALLENGE, perimeter);
    }

    public void Achivements(Consumer<Advancement> consumer){
        Advancement stuckserver = Advancement.Builder.create()
                .parent(advancementMap.get("curcuitrevolution"))
                .display(
                        Items.COMMAND_BLOCK,
                        Text.translatable("advancements.stuckserver.title"),
                        Text.translatable("advancements.stuckserver.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("stuck_server", new ImpossibleCriterion.Conditions())
                .build(consumer, "stuckserver");
        Advancement largeserver = Advancement.Builder.create()
                .parent(stuckserver)
                .display(
                        Items.CHAIN_COMMAND_BLOCK,
                        Text.translatable("advancements.largeserver.title"),
                        Text.translatable("advancements.largeserver.descr"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("large_server", new ImpossibleCriterion.Conditions())
                .build(consumer, "largeserver");
        Advancement giantserver = Advancement.Builder.create()
                .parent(largeserver)
                .display(
                        Items.REPEATING_COMMAND_BLOCK,
                        Text.translatable("advancements.giantserver.title"),
                        Text.translatable("advancements.giantserver.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("giant_server", new ImpossibleCriterion.Conditions())
                .build(consumer, "giantserver");
        Advancement fastesttravel = Advancement.Builder.create()
                .parent(advancementMap.get("netherhighway"))
                .display(
                        Items.ENDER_PEARL,
                        Text.translatable("advancements.fastesttravel.title"),
                        Text.translatable("advancements.fastesttravel.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("fastest_travel", new ImpossibleCriterion.Conditions())
                .build(consumer, "fastesttravel");
        Advancement lighttravel = Advancement.Builder.create()
                .parent(fastesttravel)
                .display(
                        Items.BARRIER,
                        Text.translatable("advancements.lighttravel.title"),
                        Text.translatable("advancements.lighttravel.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("light_travel", new ImpossibleCriterion.Conditions())
                .build(consumer, "lighttravel");
        Advancement killdragon10time = Advancement.Builder.create()
                .parent(advancementMap.get("agriculture"))
                .display(
                        Items.DRAGON_HEAD,
                        Text.translatable("advancements.killdragon10time.title"),
                        Text.translatable("advancements.killdragon10time.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("kill_10time", new ImpossibleCriterion.Conditions())
                .build(consumer, "killdragon10time");
        Advancement afk = Advancement.Builder.create()
                .parent(advancementMap.get("automaticfactory"))
                .display(
                        Items.STRUCTURE_VOID,
                        Text.translatable("advancements.afk.title"),
                        Text.translatable("advancements.afk.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("afk", new ImpossibleCriterion.Conditions())
                .build(consumer, "afk");
        Advancement afkdie = Advancement.Builder.create()
                .parent(afk)
                .display(
                        Items.BARRIER,
                        Text.translatable("advancements.afkdie.title"),
                        Text.translatable("advancements.afkdie.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("afkdie", new ImpossibleCriterion.Conditions())
                .build(consumer, "afkdie");
    }

    @Override
    public void accept(Consumer<Advancement> consumer) {
        LOGGER.info("Registering advancements...");
        autoRoot(consumer);
        agricultureRoot(consumer);
        curcuitrevolutionRoot(consumer);
        Achivements(consumer);
        LOGGER.info("成功注册{}成就",createAdvancement);
    }

    private Advancement registerInventorySingleItemAdvancement(
            Consumer<Advancement> consumer,
            String id,
            Item displayItem,
            AdvancementFrame frame,
            Item targetItem,
            int requiredStacks,
            Advancement parent) {
        Advancement advancement = Advancement.Builder.create()
                .parent(parent)
                .display(
                        displayItem,
                        Text.translatable("advancements." + id + ".title"),
                        Text.translatable("advancements." + id + ".descr"),
                        null,
                        frame,
                        true, true, false
                )
                .criterion("a1", InventoryChangedCriterion.Conditions.items(targetItem))
                .build(consumer, id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }

    private Advancement registerSingleItemAdvancement(
            Consumer<Advancement> consumer,
            String id,
            Item displayItem,
            AdvancementFrame frame,
            Item targetItem,
            int requiredStacks,
            Advancement parent) {
        Advancement advancement = Advancement.Builder.create()
                .parent(parent)
                .display(
                        displayItem,
                        Text.translatable("advancements." + id + ".title"),
                        Text.translatable("advancements." + id + ".descr"),
                        null,
                        frame,
                        true, true, false
                )
                .criterion("a1", FullStackCriterion.createCriterion(targetItem, requiredStacks))
                .build(consumer, id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }

    private Advancement registerItemSetAdvancement(
            Consumer<Advancement> consumer,
            String id,
            Item displayItem,
            Set<Item> targetItems,
            int requiredStacks,
            AdvancementFrame frame,
            Advancement parent) {
        Advancement advancement = Advancement.Builder.create()
                .parent(parent)
                .display(
                        displayItem,
                        Text.translatable("advancements." + id + ".title"),
                        Text.translatable("advancements." + id + ".descr"),
                        null,
                        frame,
                        true, true, false
                )
                .criterion("a1", FullStackCriterion.createCriterion(targetItems, requiredStacks))
                .build(consumer, id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }

    private Advancement registerSingleShulkerBoxAdvancement(
            Consumer<Advancement> consumer,
            String id,
            Item displayItem,
            Item targetItem,
            int requiredStacks,
            AdvancementFrame frame,
            Advancement parent) {
        Advancement advancement = Advancement.Builder.create()
                .parent(parent)
                .display(
                        displayItem,
                        Text.translatable("advancements." + id + ".title"),
                        Text.translatable("advancements." + id + ".descr"),
                        null,
                        frame,
                        true, true, false
                )
                .criterion("a1", FullShulkerBoxCriterion.createCriterion(targetItem, requiredStacks))
                .build(consumer, id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }

    private Advancement registerMultiShulkerBoxAdvancement(
            Consumer<Advancement> consumer,
            String id,
            Item displayItem,
            Set<Item> targetItems,
            int requiredStacks,
            AdvancementFrame frame,
            Advancement parent) {
        Advancement advancement = Advancement.Builder.create()
                .parent(parent)
                .display(
                        displayItem,
                        Text.translatable("advancements." + id + ".title"),
                        Text.translatable("advancements." + id + ".descr"),
                        null,
                        frame,
                        true, true, false
                )
                .criterion("a1", FullShulkerBoxCriterion.createCriterion(targetItems, requiredStacks))
                .build(consumer, id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }
}


