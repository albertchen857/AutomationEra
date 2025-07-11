package com.automationera.advance;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Advancements{
    private static final String MOD_ID = "automationera";
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/Advancements");
    private final Map<String, AdvancementEntry> advancementMap = new HashMap<>();
    private int createAdvancement = 0;
    private RegistryEntryLookup<EntityType<?>> entityTypeLookup;
    private RegistryEntryLookup<Item> itemTypeRegistry;
    private RegistryEntryLookup<Block> blockTypeRegistry;

    private AdvancementEntry curcuitrevolution;
    private AdvancementEntry netherhighway;
    private AdvancementEntry attribute;
    private AdvancementEntry automaticFactory;
    private AdvancementEntry furnacegroup;

    public void autoRoot(Consumer<AdvancementEntry> consumer){
        automaticFactory = Advancement.Builder.create()
                .display(
                        Items.REDSTONE,
                        Text.translatable("advancements.automaticfactory.title"),
                        Text.translatable("advancements.automaticfactory.descr"),
                        Identifier.of("textures/block/redstone_ore.png"),
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("a", InventoryChangedCriterion.Conditions.items(Items.REDSTONE))
                .build(consumer, MOD_ID+":"+"automaticfactory");
        advancementMap.put("automaticfactory", automaticFactory);

        AdvancementEntry village = Advancement.Builder.create()//村民工程类
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
                        EntityPredicate.Builder.create().type(
                                EntityTypePredicate.create(entityTypeLookup, EntityType.IRON_GOLEM)
                        )
                ))
                .build(consumer, MOD_ID+":"+"village");
        advancementMap.put("village", village);
        AdvancementEntry stringfarm = registerSingleItemAdvancement(consumer, "stringfarm", Items.STRING, AdvancementFrame.TASK, Items.STRING, 9, village);
        AdvancementEntry tripwire = registerSingleItemAdvancement(consumer, "tripwire", Items.TRIPWIRE_HOOK, AdvancementFrame.TASK, Items.TRIPWIRE_HOOK, 9, village);
        AdvancementEntry raidfarm = registerSingleItemAdvancement(consumer, "raidfarm", Items.EMERALD, AdvancementFrame.TASK, Items.EMERALD, 9, village);
        AdvancementEntry tradingpost = Advancement.Builder.create()
                .parent(village)
                .display(
                        Items.EMERALD,
                        Text.translatable("advancements.tradingpost.title"),
                        Text.translatable("advancements.tradingpost.descr"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("trading_post", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"tradingpost");
        advancementMap.put("tradingpost", tradingpost);


        AdvancementEntry woodfarm = registerItemSetAdvancement(consumer, "woodfarm",Items.OAK_LOG, Set.of( // 木材农场
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
        AdvancementEntry stonefarm = registerSingleItemAdvancement(consumer, "stonefarm", Items.COBBLESTONE, AdvancementFrame.GOAL, Items.COBBLESTONE, 9, automaticFactory); // 圆石农场
        AdvancementEntry icefarm = registerSingleItemAdvancement(consumer, "icefarm", Items.ICE, AdvancementFrame.TASK, Items.ICE, 9, stonefarm); // 冰农场
        AdvancementEntry basaltfarm = registerSingleItemAdvancement(consumer, "basaltfarm", Items.BASALT, AdvancementFrame.TASK, Items.BASALT, 9, stonefarm); // 玄武岩农场
        AdvancementEntry dripstonefarm = registerSingleItemAdvancement(consumer, "dripstonefarm", Items.DRIPSTONE_BLOCK, AdvancementFrame.TASK, Items.DRIPSTONE_BLOCK, 9, stonefarm); // 滴水石农场
        furnacegroup = registerItemSetAdvancement(consumer, "furnacegroup", Items.FURNACE, Set.of(Items.GLASS,Items.STONE), 9, AdvancementFrame.TASK, stonefarm); // 熔炉组
        AdvancementEntry ironfarm = registerSingleItemAdvancement(consumer, "ironfarm", Items.IRON_INGOT, AdvancementFrame.GOAL, Items.IRON_INGOT, 9, automaticFactory); // 铁农场
        AdvancementEntry amethystfarm = registerSingleItemAdvancement(consumer, "amethystfarm", Items.AMETHYST_SHARD, AdvancementFrame.TASK, Items.AMETHYST_SHARD, 9, ironfarm); // 紫水晶农场
        AdvancementEntry sandfarm = registerSingleItemAdvancement(consumer, "sandfarm", Items.SAND, AdvancementFrame.GOAL, Items.SAND, 9, ironfarm); // 沙子农场
        AdvancementEntry dirtfarm = registerSingleItemAdvancement(consumer, "dirtfarm", Items.DIRT, AdvancementFrame.TASK, Items.DIRT, 9, sandfarm); // 泥土农场
        AdvancementEntry clayfarm = registerSingleItemAdvancement(consumer, "clayfarm", Items.CLAY, AdvancementFrame.TASK, Items.CLAY, 9, dirtfarm); // 粘土农场
        AdvancementEntry mudfarm = registerSingleItemAdvancement(consumer, "mudfarm", Items.MUD, AdvancementFrame.TASK, Items.MUD, 9, dirtfarm); // 粘土农场
        AdvancementEntry bonefarm = registerSingleItemAdvancement(consumer, "bonefarm", Items.BONE_MEAL, AdvancementFrame.GOAL, Items.BONE_MEAL, 9, woodfarm); // 骨粉农场
        AdvancementEntry obsidianfarm = registerSingleItemAdvancement(consumer, "obsidianfarm", Items.OBSIDIAN, AdvancementFrame.TASK, Items.OBSIDIAN, 9, sandfarm); // 黑曜石农场

        AdvancementEntry abovenether = Advancement.Builder.create()
                .parent(automaticFactory)
                .display(
                        Items.BEDROCK,
                        Text.translatable("advancements.abovenether.title"),
                        Text.translatable("advancements.abovenether.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("above_nether", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"abovenether");
        advancementMap.put("abovenether", abovenether);
        BlockPredicate.Builder blockPredicateBuilder = BlockPredicate.Builder.create()
                .blocks(blockTypeRegistry, Blocks.PACKED_ICE);
        netherhighway = Advancement.Builder.create()
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
                        BlockPredicate.Builder.create().blocks(blockTypeRegistry, Blocks.PACKED_ICE).build(),
                        LocationPredicate.Builder.create().dimension(World.NETHER).build()
                ))
                .build(consumer, MOD_ID+":"+"netherhighway");
        advancementMap.put("netherhighway", netherhighway);


        AdvancementEntry railfarm = registerItemSetAdvancement(consumer, "railfarm",Items.POWERED_RAIL, Set.of( // 铁轨农场
                Items.RAIL,
                Items.POWERED_RAIL,
                Items.DETECTOR_RAIL,
                Items.ACTIVATOR_RAIL
        ), 9, AdvancementFrame.TASK, ironfarm);

        AdvancementEntry copperfarm = registerItemSetAdvancement(consumer, "copperfarm",Items.EXPOSED_COPPER, Set.of( // 铜农场
                Items.EXPOSED_COPPER,
                Items.WEATHERED_COPPER,
                Items.OXIDIZED_COPPER
        ), 4, AdvancementFrame.TASK, stonefarm);

        AdvancementEntry leaffarm = registerItemSetAdvancement(consumer, "leaffarm",Items.OAK_LEAVES, Set.of( // 树叶农场
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
        ), 9, AdvancementFrame.TASK, bonefarm);

        AdvancementEntry fungusfarm = registerItemSetAdvancement(consumer, "fungusfarm",Items.CRIMSON_STEM, Set.of( // 菌类农场
                Items.CRIMSON_STEM,
                Items.WARPED_STEM
        ), 9, AdvancementFrame.TASK, bonefarm);

        AdvancementEntry mushroomfarm = registerItemSetAdvancement(consumer, "mushroomfarm",Items.RED_MUSHROOM_BLOCK, Set.of( // 菌类农场
                Items.BROWN_MUSHROOM,
                Items.RED_MUSHROOM,
                Items.RED_MUSHROOM_BLOCK,
                Items.BROWN_MUSHROOM_BLOCK
        ), 9, AdvancementFrame.TASK, bonefarm);

        AdvancementEntry concretefarm = registerItemSetAdvancement(consumer, "concretefarm", Items.WHITE_CONCRETE, Set.of( // 混凝土农场
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
        ), 9, AdvancementFrame.TASK, sandfarm);
    }

    public void attributeRoot(Consumer<AdvancementEntry> consumer){
        attribute = Advancement.Builder.create()
                .display(
                        Items.VILLAGER_SPAWN_EGG,
                        Text.translatable("advancements.attribute.title"),
                        Text.translatable("advancements.attribute.descr"),
                        Identifier.of("textures/block/smooth_stone.png"),
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("a", InventoryChangedCriterion.Conditions.items(Items.WHEAT_SEEDS))
                .build(consumer, MOD_ID+":"+"attribute");
        advancementMap.put("attribute", attribute);

        AdvancementEntry animalfarm = Advancement.Builder.create()//动物农场类
                .parent(attribute)
                .display(
                        Items.BEEF,
                        Text.translatable("advancements.animalfarm.title"),
                        Text.translatable("advancements.animalfarm.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("a", BredAnimalsCriterion.Conditions.any())
                .build(consumer, MOD_ID+":"+"animalfarm");
        advancementMap.put("animalfarm", animalfarm);

        AdvancementEntry turtlefarm = registerSingleItemAdvancement(consumer, "turtlefarm", Items.TURTLE_EGG, AdvancementFrame.TASK, Items.TURTLE_EGG, 4, animalfarm); // 海龟农场
        AdvancementEntry snowfarm = registerSingleItemAdvancement(consumer, "snowfarm", Items.SNOW_BLOCK, AdvancementFrame.TASK, Items.SNOW_BLOCK, 9, animalfarm); // 雪农场
        AdvancementEntry woolfarm = registerItemSetAdvancement(consumer, "woolfarm",Items.WHITE_WOOL, Set.of( // 羊毛农场
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
        ), 9, AdvancementFrame.TASK, animalfarm);
        AdvancementEntry squidfarm = registerItemSetAdvancement(consumer, "squidfarm",Items.INK_SAC, Set.of( // 鱿鱼农场
                Items.INK_SAC,
                Items.GLOW_INK_SAC
        ), 9, AdvancementFrame.TASK, animalfarm);
        AdvancementEntry snifferfarm = registerItemSetAdvancement(consumer, "snifferfarm",Items.SNIFFER_EGG, Set.of( // 嗅探兽农场
                Items.TORCHFLOWER_SEEDS,
                Items.PITCHER_POD
        ), 9, AdvancementFrame.TASK, woolfarm);
        AdvancementEntry honeyfarm = registerSingleItemAdvancement(consumer, "honeyfarm", Items.BEEHIVE, AdvancementFrame.TASK, Items.HONEY_BLOCK, 4, animalfarm); // 骨粉农场


        AdvancementEntry mobfarm = registerSingleItemAdvancement(consumer, "mobfarm", Items.ROTTEN_FLESH, AdvancementFrame.TASK, Items.BONE, 4, attribute); // 怪物农场
        AdvancementEntry shulkerfarm = registerSingleItemAdvancement(consumer, "shulkerfarm", Items.SHULKER_SHELL, AdvancementFrame.GOAL, Items.SHULKER_SHELL, 9, mobfarm); // 潜影贝农场
        AdvancementEntry witchfarm = registerSingleItemAdvancement(consumer, "witchfarm", Items.REDSTONE, AdvancementFrame.TASK, Items.REDSTONE, 9, mobfarm); // 女巫农场
        AdvancementEntry creeperfarm = registerSingleItemAdvancement(consumer, "creeperfarm", Items.GUNPOWDER, AdvancementFrame.TASK, Items.GUNPOWDER, 9, mobfarm); // 女巫农场
        AdvancementEntry ghastfarm = registerSingleItemAdvancement(consumer, "ghastfarm", Items.GHAST_TEAR, AdvancementFrame.TASK, Items.GHAST_TEAR, 4, mobfarm); // 女巫农场
        AdvancementEntry slimefarm = registerSingleItemAdvancement(consumer, "slimefarm", Items.SLIME_BALL, AdvancementFrame.TASK, Items.SLIME_BALL, 9, mobfarm); // 史莱姆农场
        AdvancementEntry blaze = registerSingleItemAdvancement(consumer, "blaze", Items.BLAZE_ROD, AdvancementFrame.TASK, Items.BLAZE_ROD, 9, mobfarm); // 烈焰人农场
        AdvancementEntry endermanfarm = registerSingleItemAdvancement(consumer, "endermanfarm", Items.ENDER_PEARL, AdvancementFrame.TASK, Items.ENDER_PEARL, 9, mobfarm); // 末影人农场
        AdvancementEntry pigmanfarm = registerSingleItemAdvancement(consumer, "pigmanfarm", Items.GOLD_INGOT, AdvancementFrame.GOAL, Items.GOLD_INGOT, 9, endermanfarm); // 猪人农场
        AdvancementEntry witherrose = registerSingleItemAdvancement(consumer, "witherrose", Items.WITHER_ROSE, AdvancementFrame.TASK, Items.WITHER_ROSE, 9, mobfarm); // 凋零玫瑰农场
        AdvancementEntry witherskel = registerSingleItemAdvancement(consumer, "witherskel", Items.WITHER_SKELETON_SKULL, AdvancementFrame.TASK, Items.WITHER_SKELETON_SKULL, 4, witherrose); // 凋零骷髅农场
        AdvancementEntry witherkill = registerSingleItemAdvancement(consumer, "witherkill", Items.NETHER_STAR, AdvancementFrame.CHALLENGE, Items.NETHER_STAR, 9, witherskel); // 凋零击杀农场
        AdvancementEntry guardianfarm = registerSingleItemAdvancement(consumer, "guardianfarm", Items.PRISMARINE_SHARD, AdvancementFrame.TASK, Items.PRISMARINE_SHARD, 9, mobfarm); // 守卫者农场
        AdvancementEntry drownedfarm = registerSingleItemAdvancement(consumer, "drownedfarm", Items.TRIDENT, AdvancementFrame.TASK, Items.TRIDENT, 4, guardianfarm); // 溺尸塔
        AdvancementEntry magmacubefarm = registerSingleItemAdvancement(consumer, "magmacubefarm", Items.MAGMA_CREAM, AdvancementFrame.TASK, Items.MAGMA_CREAM, 9, witherrose); // 岩浆怪农场
        AdvancementEntry froglightfarm = registerItemSetAdvancement(consumer, "froglightfarm",Items.OCHRE_FROGLIGHT, Set.of( // 蛙明灯农场
                Items.OCHRE_FROGLIGHT,
                Items.PEARLESCENT_FROGLIGHT,
                Items.VERDANT_FROGLIGHT
        ), 9, AdvancementFrame.TASK, witherrose);
        AdvancementEntry piglintrade = registerItemSetAdvancement(consumer, "piglintrade",Items.QUARTZ, Set.of( // 猪灵交易所
                Items.QUARTZ,
                Items.CRYING_OBSIDIAN
        ), 9, AdvancementFrame.TASK, pigmanfarm);


        AdvancementEntry autofarm = registerItemSetAdvancement(consumer, "autofarm",Items.CARROT, Set.of( // 自动农场
                Items.WHEAT,
                Items.POTATO,
                Items.CARROT
        ), 4, AdvancementFrame.TASK, attribute);
        AdvancementEntry wheatfarm = registerSingleItemAdvancement(consumer, "wheatfarm", Items.WHEAT, AdvancementFrame.TASK, Items.WHEAT, 9, autofarm); // 小麦农场
        AdvancementEntry sugarcane = registerSingleItemAdvancement(consumer, "sugarcane", Items.SUGAR_CANE, AdvancementFrame.TASK, Items.SUGAR_CANE, 9, wheatfarm); // 甘蔗农场
        AdvancementEntry cactusfarm = registerSingleItemAdvancement(consumer, "cactusfarm", Items.CACTUS, AdvancementFrame.TASK, Items.CACTUS, 9, sugarcane); // 仙人掌农场
        AdvancementEntry kelpfarm = registerSingleItemAdvancement(consumer, "kelpfarm", Items.KELP, AdvancementFrame.TASK, Items.KELP, 9, sugarcane); // 海带农场
        AdvancementEntry bamboo = registerSingleItemAdvancement(consumer, "bamboo", Items.BAMBOO, AdvancementFrame.TASK, Items.BAMBOO, 9, sugarcane); // 竹子农场
        AdvancementEntry pumpkinfarm = registerSingleItemAdvancement(consumer, "pumpkinfarm", Items.PUMPKIN, AdvancementFrame.TASK, Items.PUMPKIN, 9, autofarm); // 南瓜农场
        AdvancementEntry melonfarm = registerSingleItemAdvancement(consumer, "melonfarm", Items.MELON, AdvancementFrame.TASK, Items.MELON, 9, pumpkinfarm); // 西瓜农场
        AdvancementEntry netherwartfarm = registerSingleItemAdvancement(consumer, "netherwartfarm", Items.NETHER_WART, AdvancementFrame.TASK, Items.NETHER_WART, 9, wheatfarm); // 下界疣农场
        AdvancementEntry potionfarm = registerSingleItemAdvancement(consumer, "potionfarm", Items.POTION, AdvancementFrame.TASK, Items.POTION, 18, netherwartfarm); // 炼药机
        AdvancementEntry sweetberryfarm = registerSingleItemAdvancement(consumer, "sweetberryfarm", Items.SWEET_BERRIES, AdvancementFrame.TASK, Items.SWEET_BERRIES, 9, wheatfarm); // 甜浆果农场
        AdvancementEntry seapicklefarm = registerSingleItemAdvancement(consumer, "seapicklefarm", Items.SEA_PICKLE, AdvancementFrame.TASK, Items.SEA_PICKLE, 9, autofarm); // 海泡菜农场
        AdvancementEntry hoglinfarm = registerSingleItemAdvancement(consumer, "hoglinfarm", Items.COOKED_PORKCHOP, AdvancementFrame.GOAL, Items.COOKED_PORKCHOP, 9, autofarm); // 疣猪兽农场
        AdvancementEntry lavachicken = registerSingleItemAdvancement(consumer, "lavachicken", Items.FEATHER, AdvancementFrame.TASK, Items.COOKED_CHICKEN, 9, hoglinfarm); // 鸡农场
        AdvancementEntry cowfarm = registerSingleItemAdvancement(consumer, "cowfarm", Items.COOKED_BEEF, AdvancementFrame.TASK, Items.COOKED_BEEF, 9, hoglinfarm); // 牛农场
        AdvancementEntry rabbitfarm = registerSingleItemAdvancement(consumer, "rabbitfarm", Items.RABBIT_HIDE, AdvancementFrame.TASK, Items.RABBIT_HIDE, 9, hoglinfarm); // 兔农场
        AdvancementEntry beehive = registerSingleItemAdvancement(consumer, "beehive", Items.HONEY_BLOCK, AdvancementFrame.TASK, Items.HONEY_BLOCK, 9, autofarm); // 蜂巢农场
        AdvancementEntry chorusfarm = registerSingleItemAdvancement(consumer, "chorusfarm", Items.CHORUS_FLOWER, AdvancementFrame.TASK, Items.CHORUS_FRUIT, 9, autofarm); // 紫菘果农场
        AdvancementEntry coralfarm = registerItemSetAdvancement(consumer, "coralfarm",Items.BRAIN_CORAL_FAN, Set.of( // 珊瑚农场
                Items.BRAIN_CORAL_FAN,
                Items.BUBBLE_CORAL_FAN,
                Items.FIRE_CORAL_FAN,
                Items.HORN_CORAL_FAN,
                Items.TUBE_CORAL_FAN
        ), 4, AdvancementFrame.TASK, seapicklefarm);
        AdvancementEntry flowerfarm = registerItemSetAdvancement(consumer, "flowerfarm",Items.POPPY, Set.of( // 花机
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
        ), 4, AdvancementFrame.TASK, beehive);
        AdvancementEntry ripeningmachine = registerItemSetAdvancement(consumer, "ripeningmachine",Items.DISPENSER, Set.of( // 催熟机
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

        ), 9, AdvancementFrame.TASK, autofarm);
    }

    public void curcuitrevolutionRoot(Consumer<AdvancementEntry> consumer){
        curcuitrevolution = Advancement.Builder.create()
                .display(
                        Items.BEACON,
                        Text.translatable("advancements.curcuitrevolution.title"),
                        Text.translatable("advancements.curcuitrevolution.descr"),
                        Identifier.of("textures/block/bedrock.png"),
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("a", InventoryChangedCriterion.Conditions.items(Items.SHULKER_BOX))
                .build(consumer, MOD_ID+":"+"curcuitrevolution");
        advancementMap.put("curcuitrevolution", curcuitrevolution);

        AdvancementEntry tntquarry = Advancement.Builder.create()
                .parent(curcuitrevolution)
                .display(
                        Items.TNT,
                        Text.translatable("advancements.tntquarry.title"),
                        Text.translatable("advancements.tntquarry.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("tntquarry", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"tntquarry");
        AdvancementEntry worldeater = Advancement.Builder.create()
                .parent(tntquarry)
                .display(
                        Items.TNT_MINECART,
                        Text.translatable("advancements.worldeater.title"),
                        Text.translatable("advancements.worldeater.descr"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("worldeater", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"worldeater");
        AdvancementEntry perimeter = Advancement.Builder.create(
)
                .parent(worldeater)
                .display(
                        Items.BEDROCK,
                        Text.translatable("advancements.perimeter.title"),
                        Text.translatable("advancements.perimeter.descr"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("perimeter", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"perimeter");
        advancementMap.put("perimeter", perimeter);

        AdvancementEntry realperimeter = Advancement.Builder.create(
)
                .parent(perimeter)
                .display(
                        Items.STRUCTURE_VOID,
                        Text.translatable("advancements.realperimeter.title"),
                        Text.translatable("advancements.realperimeter.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("realperimeter", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"realperimeter");
        AdvancementEntry netherperimeter = Advancement.Builder.create(
)
                .parent(realperimeter)
                .display(
                        Items.LAVA_BUCKET,
                        Text.translatable("advancements.netherperimeter.title"),
                        Text.translatable("advancements.netherperimeter.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("netherperimeter", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"netherperimeter");
        AdvancementEntry fakepeaceful = Advancement.Builder.create(
)
                .parent(curcuitrevolution)
                .display(
                        Items.BARRIER,
                        Text.translatable("advancements.fakepeaceful.title"),
                        Text.translatable("advancements.fakepeaceful.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("fakepeaceful", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"fakepeaceful");
        AdvancementEntry lightsuppression = Advancement.Builder.create(
)
                .parent(fakepeaceful)
                .display(
                        Items.LIGHT,
                        Text.translatable("advancements.lightsuppression.title"),
                        Text.translatable("advancements.lightsuppression.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("lightsuppression", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"lightsuppression");

        AdvancementEntry doublepigman = registerSingleShulkerBoxAdvancement(consumer, "doublepigman", Items.GOLD_BLOCK, Items.GOLD_INGOT, 27, AdvancementFrame.TASK, curcuitrevolution); // 双倍猪人农场
        AdvancementEntry bigfurnace = registerSingleShulkerBoxAdvancement(consumer, "bigfurnace", Items.BLAST_FURNACE, Items.SMOOTH_STONE, 27, AdvancementFrame.TASK, curcuitrevolution); // 大型熔炉
        AdvancementEntry alltree = registerMultiShulkerBoxAdvancement(consumer, "alltree", Items.OAK_WOOD, Set.of( // 全木材农场
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
        AdvancementEntry perimobfarm = registerMultiShulkerBoxAdvancement(consumer, "perimobfarm", Items.ROTTEN_FLESH, Set.of(Items.BONE, Items.ROTTEN_FLESH), 27, AdvancementFrame.CHALLENGE, perimeter);
        AdvancementEntry pericreeperfarm = registerSingleShulkerBoxAdvancement(consumer, "pericreeperfarm", Items.GUNPOWDER, Items.GUNPOWDER, 27, AdvancementFrame.CHALLENGE, perimeter);
        AdvancementEntry perislimefarm = registerSingleShulkerBoxAdvancement(consumer, "perislimefarm", Items.SLIME_BLOCK, Items.SLIME_BALL, 27, AdvancementFrame.CHALLENGE, perimeter);
        AdvancementEntry periwitherskel = registerSingleShulkerBoxAdvancement(consumer, "periwitherskel", Items.WITHER_SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, 27, AdvancementFrame.CHALLENGE, perimeter);
        AdvancementEntry periguardianfarm = registerSingleShulkerBoxAdvancement(consumer, "periguardianfarm", Items.SEA_LANTERN, Items.SEA_LANTERN, 27, AdvancementFrame.CHALLENGE, perimeter);
    }

    public void Achivements(Consumer<AdvancementEntry> consumer){
        AdvancementEntry stuckserver = Advancement.Builder.create(
)
                .parent(curcuitrevolution)
                .display(
                        Items.COMMAND_BLOCK,
                        Text.translatable("advancements.stuckserver.title"),
                        Text.translatable("advancements.stuckserver.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("stuck_server", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"stuckserver");

        AdvancementEntry largeserver = Advancement.Builder.create()
                .parent(stuckserver)
                .display(
                        Items.CHAIN_COMMAND_BLOCK,
                        Text.translatable("advancements.largeserver.title"),
                        Text.translatable("advancements.largeserver.descr"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("large_server", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"largeserver");

        AdvancementEntry giantserver = Advancement.Builder.create(
)
                .parent(largeserver)
                .display(
                        Items.REPEATING_COMMAND_BLOCK,
                        Text.translatable("advancements.giantserver.title"),
                        Text.translatable("advancements.giantserver.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("giant_server", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"giantserver");

        AdvancementEntry fastesttravel = Advancement.Builder.create(
)
                .parent(netherhighway)
                .display(
                        Items.ENDER_PEARL,
                        Text.translatable("advancements.fastesttravel.title"),
                        Text.translatable("advancements.fastesttravel.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("fastest_travel", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"fastesttravel");

        AdvancementEntry lighttravel = Advancement.Builder.create(
)
                .parent(fastesttravel)
                .display(
                        Items.BARRIER,
                        Text.translatable("advancements.lighttravel.title"),
                        Text.translatable("advancements.lighttravel.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("light_travel", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"lighttravel");

        AdvancementEntry killdragon10time = Advancement.Builder.create(
)
                .parent(attribute)
                .display(
                        Items.DRAGON_HEAD,
                        Text.translatable("advancements.killdragon10time.title"),
                        Text.translatable("advancements.killdragon10time.descr"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("kill_10time", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"killdragon10time");

        AdvancementEntry afk = Advancement.Builder.create(
)
                .parent(automaticFactory)
                .display(
                        Items.STRUCTURE_VOID,
                        Text.translatable("advancements.afk.title"),
                        Text.translatable("advancements.afk.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("afk", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"afk");

        AdvancementEntry afkdie = Advancement.Builder.create(
)
                .parent(afk)
                .display(
                        Items.BARRIER,
                        Text.translatable("advancements.afkdie.title"),
                        Text.translatable("advancements.afkdie.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("afkdie", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"afkdie");

        AdvancementEntry openfurnace = Advancement.Builder.create(
)
                .parent(furnacegroup)
                .display(
                        Items.BLAST_FURNACE,
                        Text.translatable("advancements.openfurnace.title"),
                        Text.translatable("advancements.openfurnace.descr"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("openfurnace", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"openfurnace");

        AdvancementEntry placeobserver = Advancement.Builder.create(
)
                .parent(automaticFactory)
                .display(
                        Items.OBSERVER,
                        Text.translatable("advancements.placeobserver.title"),
                        Text.translatable("advancements.placeobserver.descr"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("placeobserver", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID+":"+"placeobserver"); }


    public void accept(Consumer<AdvancementEntry> consumer, RegistryWrapper.WrapperLookup registryLookup) {
        LOGGER.info("Registering advancements...");
        entityTypeLookup = registryLookup.getOrThrow(RegistryKeys.ENTITY_TYPE);
        blockTypeRegistry = registryLookup.getOrThrow(RegistryKeys.BLOCK);
        itemTypeRegistry = registryLookup.getOrThrow(RegistryKeys.ITEM);
        autoRoot(consumer);
        attributeRoot(consumer);
        curcuitrevolutionRoot(consumer);
        Achivements(consumer);
        LOGGER.info("成功注册{}成就",createAdvancement);
    }

    private AdvancementEntry registerInventorySingleItemAdvancement(
            Consumer<AdvancementEntry> consumer,
            String id,
            Item displayItem,
            AdvancementFrame frame,
            Item targetItem,
            int requiredStacks,
            AdvancementEntry parent) {
        AdvancementEntry advancement = Advancement.Builder.create(
)
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
                .build(consumer, MOD_ID+":"+id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }

    private AdvancementEntry registerSingleItemAdvancement(
            Consumer<AdvancementEntry> consumer,
            String id,
            Item displayItem,
            AdvancementFrame frame,
            Item targetItem,
            int requiredStacks,
            AdvancementEntry parent) {
        AdvancementEntry advancement = Advancement.Builder.create(
)
                .parent(parent)
                .display(
                        displayItem,
                        Text.translatable("advancements." + id + ".title"),
                        Text.translatable("advancements." + id + ".descr"),
                        null,
                        frame,
                        true, true, false
                )
                .criterion("a1", FullStackCriterion.createCriterion(Set.of(targetItem), requiredStacks))
                .build(consumer, MOD_ID+":"+id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }

    private AdvancementEntry registerItemSetAdvancement(
            Consumer<AdvancementEntry> consumer,
            String id,
            Item displayItem,
            Set<Item> targetItems,
            int requiredStacks,
            AdvancementFrame frame,
            AdvancementEntry parent) {
        AdvancementEntry advancement = Advancement.Builder.create(
)
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
                .build(consumer, MOD_ID+":"+id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }

    private AdvancementEntry registerSingleShulkerBoxAdvancement(
            Consumer<AdvancementEntry> consumer,
            String id,
            Item displayItem,
            Item targetItem,
            int requiredStacks,
            AdvancementFrame frame,
            AdvancementEntry parent) {
        AdvancementEntry advancement = Advancement.Builder.create(
)
                .parent(parent)
                .display(
                        displayItem,
                        Text.translatable("advancements." + id + ".title"),
                        Text.translatable("advancements." + id + ".descr"),
                        null,
                        frame,
                        true, true, false
                )
                .criterion("a1", FullShulkerBoxCriterion.createCriterion(ItemPredicate.Builder.create().items(itemTypeRegistry, targetItem).build(), requiredStacks))
                .build(consumer, MOD_ID+":"+id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }

    private AdvancementEntry registerMultiShulkerBoxAdvancement(
            Consumer<AdvancementEntry> consumer,
            String id,
            Item displayItem,
            Set<Item> targetItems,
            int requiredStacks,
            AdvancementFrame frame,
            AdvancementEntry parent) {
        AdvancementEntry advancement = Advancement.Builder.create(
)
                .parent(parent)
                .display(
                        displayItem,
                        Text.translatable("advancements." + id + ".title"),
                        Text.translatable("advancements." + id + ".descr"),
                        null,
                        frame,
                        true, true, false
                )
                .criterion("a1", FullShulkerBoxCriterion.createCriterion(ItemPredicate.Builder.create().items(itemTypeRegistry, targetItems.toArray(new ItemConvertible[0])).build(), requiredStacks))
                .build(consumer, MOD_ID+":"+id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }
}


