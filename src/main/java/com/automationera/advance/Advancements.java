package com.automationera.advance;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 进度定义（datagen 生成 src/main/generated/data/automationera/advancement/*.json）。
 * <p>
 * 0.1.9.1 机器进度统一规则：原 4 组 → 9 组；原 9 组 → 27 组或 1 盒；原 1 盒 → 9 盒。
 * 以下保持原条件：恶魂农场、凋零骷髅农场、杀凋机、兔农场、炼药机、海龟农场、晒铜机、混凝土工厂。
 */
public class Advancements {
    private static final String MOD_ID = "automationera";
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/Advancements");
    private final Map<String, AdvancementEntry> advancementMap = new HashMap<>();
    private int createAdvancement = 0;
    private RegistryEntryLookup<Item> itemTypeRegistry;
    private RegistryEntryLookup<Block> blockTypeRegistry;
    private RegistryEntryLookup<Structure> structureRegistry;

    private AdvancementEntry curcuitrevolution;
    private AdvancementEntry netherhighway;
    private AdvancementEntry attribute;
    private AdvancementEntry automaticFactory;
    private AdvancementEntry furnacegroup;

    // ---------------------------------------------------------------- 条件
    /** 原 9 组 → 27 组或 1 盒 */
    private static AdvancementCriterion<?> stacks27(Item... items) {
        return FullStackCriterion.stacksOrBoxes(Set.of(items), 27, 1);
    }

    private static AdvancementCriterion<?> stacks(int n, Item... items) {
        return FullStackCriterion.stacks(Set.of(items), n);
    }

    /** 原 1 盒 → 9 盒 */
    private static AdvancementCriterion<?> boxes9(Item... items) {
        return FullStackCriterion.boxes(Set.of(items), 9);
    }

    private static final Item[] LOGS = {Items.OAK_LOG, Items.SPRUCE_LOG, Items.BIRCH_LOG, Items.JUNGLE_LOG, Items.ACACIA_LOG,
            Items.DARK_OAK_LOG, Items.MANGROVE_LOG, Items.CHERRY_LOG, Items.PALE_OAK_LOG, Items.CRIMSON_STEM, Items.WARPED_STEM};

    // ---------------------------------------------------------------- 自动化工厂
    public void autoRoot(Consumer<AdvancementEntry> consumer) {
        automaticFactory = Advancement.Builder.create()
                .display(Items.REDSTONE,
                        Text.translatable("advancements.automaticfactory.title"),
                        Text.translatable("advancements.automaticfactory.descr"),
                        Identifier.of("automationera", "gui/advancements/backgrounds/rs"),
                        AdvancementFrame.TASK, true, true, false)
                .criterion("a", InventoryChangedCriterion.Conditions.items(Items.REDSTONE))
                .build(consumer, MOD_ID + ":" + "automaticfactory");
        advancementMap.put("automaticfactory", automaticFactory);

        AdvancementEntry woodfarm = collect(consumer, "woodfarm", Items.OAK_LOG, AdvancementFrame.GOAL, automaticFactory, stacks27(LOGS));
        AdvancementEntry stonefarm = collect(consumer, "stonefarm", Items.COBBLESTONE, AdvancementFrame.GOAL, automaticFactory, stacks27(Items.COBBLESTONE));
        collect(consumer, "icefarm", Items.ICE, AdvancementFrame.TASK, stonefarm, stacks27(Items.ICE));
        collect(consumer, "basaltfarm", Items.BASALT, AdvancementFrame.TASK, stonefarm, stacks27(Items.BASALT));
        collect(consumer, "dripstonefarm", Items.DRIPSTONE_BLOCK, AdvancementFrame.TASK, stonefarm, stacks27(Items.DRIPSTONE_BLOCK));
        // 熔炉组：玻璃或石头（任意一种）
        furnacegroup = collect(consumer, "furnacegroup", Items.FURNACE, AdvancementFrame.TASK, stonefarm, stacks27(Items.GLASS, Items.STONE));
        AdvancementEntry ironfarm = collect(consumer, "ironfarm", Items.IRON_INGOT, AdvancementFrame.GOAL, automaticFactory, stacks27(Items.IRON_INGOT));
        collect(consumer, "amethystfarm", Items.AMETHYST_SHARD, AdvancementFrame.TASK, ironfarm, stacks27(Items.AMETHYST_SHARD));
        AdvancementEntry sandfarm = collect(consumer, "sandfarm", Items.SAND, AdvancementFrame.GOAL, ironfarm, stacks27(Items.SAND));
        AdvancementEntry dirtfarm = collect(consumer, "dirtfarm", Items.DIRT, AdvancementFrame.TASK, sandfarm, stacks27(Items.DIRT));
        collect(consumer, "clayfarm", Items.CLAY, AdvancementFrame.TASK, dirtfarm, stacks27(Items.CLAY));
        collect(consumer, "mudfarm", Items.MUD, AdvancementFrame.TASK, dirtfarm, stacks27(Items.MUD));
        AdvancementEntry bonefarm = collect(consumer, "bonefarm", Items.BONE_MEAL, AdvancementFrame.GOAL, woodfarm, stacks27(Items.BONE_MEAL));
        collect(consumer, "obsidianfarm", Items.OBSIDIAN, AdvancementFrame.TASK, sandfarm, stacks27(Items.OBSIDIAN));

        AdvancementEntry abovenether = impossible(consumer, "abovenether", Items.BEDROCK, AdvancementFrame.TASK, automaticFactory, "above_nether");
        netherhighway = Advancement.Builder.create()
                .parent(abovenether)
                .display(Items.BLUE_ICE,
                        Text.translatable("advancements.netherhighway.title"),
                        Text.translatable("advancements.netherhighway.descr"),
                        null, AdvancementFrame.GOAL, true, true, false)
                .criterion("ice", PlacedBlockInNetherCriterion.conditions(
                        BlockPredicate.Builder.create().blocks(blockTypeRegistry, Blocks.PACKED_ICE).build(),
                        LocationPredicate.Builder.create().dimension(World.NETHER).build()))
                .build(consumer, MOD_ID + ":" + "netherhighway");
        advancementMap.put("netherhighway", netherhighway);

        collect(consumer, "railfarm", Items.POWERED_RAIL, AdvancementFrame.TASK, ironfarm,
                stacks27(Items.RAIL, Items.POWERED_RAIL, Items.DETECTOR_RAIL, Items.ACTIVATOR_RAIL));
        // 晒铜机：保持原条件
        collect(consumer, "copperfarm", Items.EXPOSED_COPPER, AdvancementFrame.TASK, stonefarm,
                stacks(4, Items.EXPOSED_COPPER, Items.WEATHERED_COPPER, Items.OXIDIZED_COPPER));
        collect(consumer, "leaffarm", Items.OAK_LEAVES, AdvancementFrame.TASK, bonefarm, stacks27(
                Items.OAK_LEAVES, Items.SPRUCE_LEAVES, Items.BIRCH_LEAVES, Items.JUNGLE_LEAVES, Items.ACACIA_LEAVES,
                Items.DARK_OAK_LEAVES, Items.MANGROVE_LEAVES, Items.CHERRY_LEAVES, Items.PALE_OAK_LEAVES,
                Items.AZALEA_LEAVES, Items.FLOWERING_AZALEA_LEAVES));
        collect(consumer, "fungusfarm", Items.CRIMSON_STEM, AdvancementFrame.TASK, bonefarm, stacks27(Items.CRIMSON_STEM, Items.WARPED_STEM));
        collect(consumer, "mushroomfarm", Items.RED_MUSHROOM_BLOCK, AdvancementFrame.TASK, bonefarm, stacks27(
                Items.BROWN_MUSHROOM, Items.RED_MUSHROOM, Items.RED_MUSHROOM_BLOCK, Items.BROWN_MUSHROOM_BLOCK));
        // 混凝土工厂：保持原条件
        collect(consumer, "concretefarm", Items.WHITE_CONCRETE, AdvancementFrame.TASK, sandfarm, stacks(9,
                Items.WHITE_CONCRETE, Items.ORANGE_CONCRETE, Items.MAGENTA_CONCRETE, Items.LIGHT_BLUE_CONCRETE,
                Items.YELLOW_CONCRETE, Items.LIME_CONCRETE, Items.PINK_CONCRETE, Items.GRAY_CONCRETE,
                Items.LIGHT_GRAY_CONCRETE, Items.CYAN_CONCRETE, Items.PURPLE_CONCRETE, Items.BLUE_CONCRETE,
                Items.BROWN_CONCRETE, Items.GREEN_CONCRETE, Items.RED_CONCRETE, Items.BLACK_CONCRETE));
    }

    // ---------------------------------------------------------------- 生物农场（原“村民工程”根）
    public void attributeRoot(Consumer<AdvancementEntry> consumer) {
        // id 保持 attribute，玩家已有进度不丢；获得小麦/土豆/胡萝卜或红石粉即可
        attribute = Advancement.Builder.create()
                .display(Items.VILLAGER_SPAWN_EGG,
                        Text.translatable("advancements.attribute.title"),
                        Text.translatable("advancements.attribute.descr"),
                        Identifier.of("automationera", "gui/advancements/backgrounds/ss"),
                        AdvancementFrame.TASK, true, true, false)
                .criterion("a", InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create()
                        .items(itemTypeRegistry, Items.WHEAT, Items.POTATO, Items.CARROT, Items.REDSTONE)))
                .build(consumer, MOD_ID + ":" + "attribute");
        advancementMap.put("attribute", attribute);

        // 村民工程：二级成就（与畜牧业同级），到达村庄触发；自动农场挂在它下面
        AdvancementEntry village = Advancement.Builder.create()
                .parent(attribute)
                .display(Items.BELL,
                        Text.translatable("advancements.village.title"),
                        Text.translatable("advancements.village.descr"),
                        null, AdvancementFrame.TASK, true, true, false)
                .criterion("village", TickCriterion.Conditions.createLocation(
                        LocationPredicate.Builder.create().structure(structureRegistry.getOrThrow(StructureTags.VILLAGE))))
                .build(consumer, MOD_ID + ":" + "village");
        advancementMap.put("village", village);
        collect(consumer, "stringfarm", Items.STRING, AdvancementFrame.TASK, village, stacks27(Items.STRING));
        collect(consumer, "tripwire", Items.TRIPWIRE_HOOK, AdvancementFrame.TASK, village, stacks27(Items.TRIPWIRE_HOOK));
        collect(consumer, "raidfarm", Items.EMERALD, AdvancementFrame.TASK, village, stacks27(Items.EMERALD));
        impossible(consumer, "tradingpost", Items.EMERALD, AdvancementFrame.GOAL, village, "trading_post");

        AdvancementEntry animalfarm = Advancement.Builder.create()
                .parent(attribute)
                .display(Items.BEEF,
                        Text.translatable("advancements.animalfarm.title"),
                        Text.translatable("advancements.animalfarm.descr"),
                        null, AdvancementFrame.TASK, true, true, false)
                .criterion("a", BredAnimalsCriterion.Conditions.any())
                .build(consumer, MOD_ID + ":" + "animalfarm");
        advancementMap.put("animalfarm", animalfarm);

        collect(consumer, "turtlefarm", Items.TURTLE_EGG, AdvancementFrame.TASK, animalfarm, stacks(4, Items.TURTLE_EGG)); // 保持原条件
        collect(consumer, "snowfarm", Items.SNOW_BLOCK, AdvancementFrame.TASK, animalfarm, stacks27(Items.SNOW_BLOCK));
        AdvancementEntry woolfarm = collect(consumer, "woolfarm", Items.WHITE_WOOL, AdvancementFrame.TASK, animalfarm, stacks27(
                Items.WHITE_WOOL, Items.ORANGE_WOOL, Items.MAGENTA_WOOL, Items.LIGHT_BLUE_WOOL, Items.YELLOW_WOOL,
                Items.LIME_WOOL, Items.PINK_WOOL, Items.GRAY_WOOL, Items.LIGHT_GRAY_WOOL, Items.CYAN_WOOL,
                Items.PURPLE_WOOL, Items.BLUE_WOOL, Items.BROWN_WOOL, Items.GREEN_WOOL, Items.RED_WOOL, Items.BLACK_WOOL));
        // 鱿鱼农场拆成普通/发光两个
        collect(consumer, "squidfarm", Items.INK_SAC, AdvancementFrame.TASK, animalfarm, stacks27(Items.INK_SAC));
        collect(consumer, "glowsquidfarm", Items.GLOW_INK_SAC, AdvancementFrame.TASK, animalfarm, stacks27(Items.GLOW_INK_SAC));
        collect(consumer, "snifferfarm", Items.SNIFFER_EGG, AdvancementFrame.TASK, woolfarm, stacks27(Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD));
        // 蜂箱图标：蜜脾农场，9 组蜜脾
        collect(consumer, "honeyfarm", Items.BEEHIVE, AdvancementFrame.TASK, animalfarm, stacks(9, Items.HONEYCOMB));

        AdvancementEntry mobfarm = collect(consumer, "mobfarm", Items.ROTTEN_FLESH, AdvancementFrame.TASK, attribute,
                stacks(9, Items.BONE, Items.ROTTEN_FLESH, Items.GUNPOWDER));
        collect(consumer, "shulkerfarm", Items.SHULKER_SHELL, AdvancementFrame.GOAL, mobfarm, stacks27(Items.SHULKER_SHELL));
        collect(consumer, "witchfarm", Items.REDSTONE, AdvancementFrame.TASK, mobfarm, stacks27(Items.REDSTONE));
        collect(consumer, "creeperfarm", Items.GUNPOWDER, AdvancementFrame.TASK, mobfarm, stacks27(Items.GUNPOWDER));
        collect(consumer, "ghastfarm", Items.GHAST_TEAR, AdvancementFrame.TASK, mobfarm, stacks(4, Items.GHAST_TEAR)); // 保持原条件
        collect(consumer, "slimefarm", Items.SLIME_BALL, AdvancementFrame.TASK, mobfarm, stacks27(Items.SLIME_BALL));
        collect(consumer, "blaze", Items.BLAZE_ROD, AdvancementFrame.TASK, mobfarm, stacks27(Items.BLAZE_ROD));
        AdvancementEntry endermanfarm = collect(consumer, "endermanfarm", Items.ENDER_PEARL, AdvancementFrame.TASK, mobfarm, stacks27(Items.ENDER_PEARL));
        AdvancementEntry pigmanfarm = collect(consumer, "pigmanfarm", Items.GOLD_INGOT, AdvancementFrame.GOAL, endermanfarm, stacks27(Items.GOLD_INGOT));
        AdvancementEntry witherrose = collect(consumer, "witherrose", Items.WITHER_ROSE, AdvancementFrame.TASK, mobfarm, stacks27(Items.WITHER_ROSE));
        AdvancementEntry witherskel = collect(consumer, "witherskel", Items.WITHER_SKELETON_SKULL, AdvancementFrame.TASK, witherrose,
                stacks(4, Items.WITHER_SKELETON_SKULL)); // 保持原条件（简介去掉煤炭）
        collect(consumer, "witherkill", Items.NETHER_STAR, AdvancementFrame.CHALLENGE, witherskel, stacks(9, Items.NETHER_STAR)); // 保持原条件
        AdvancementEntry guardianfarm = collect(consumer, "guardianfarm", Items.PRISMARINE_SHARD, AdvancementFrame.TASK, mobfarm, stacks27(Items.PRISMARINE_SHARD));
        collect(consumer, "drownedfarm", Items.TRIDENT, AdvancementFrame.TASK, guardianfarm, stacks(9, Items.TRIDENT));
        collect(consumer, "magmacubefarm", Items.MAGMA_CREAM, AdvancementFrame.TASK, witherrose, stacks27(Items.MAGMA_CREAM));
        collect(consumer, "froglightfarm", Items.OCHRE_FROGLIGHT, AdvancementFrame.TASK, witherrose,
                stacks27(Items.OCHRE_FROGLIGHT, Items.PEARLESCENT_FROGLIGHT, Items.VERDANT_FROGLIGHT));
        collect(consumer, "piglintrade", Items.QUARTZ, AdvancementFrame.TASK, pigmanfarm, stacks27(Items.QUARTZ, Items.CRYING_OBSIDIAN));

        AdvancementEntry autofarm = collect(consumer, "autofarm", Items.CARROT, AdvancementFrame.TASK, village,
                stacks(9, Items.WHEAT, Items.POTATO, Items.CARROT));
        AdvancementEntry wheatfarm = collect(consumer, "wheatfarm", Items.WHEAT, AdvancementFrame.TASK, autofarm, stacks27(Items.WHEAT));
        AdvancementEntry sugarcane = collect(consumer, "sugarcane", Items.SUGAR_CANE, AdvancementFrame.TASK, wheatfarm, stacks27(Items.SUGAR_CANE));
        collect(consumer, "cactusfarm", Items.CACTUS, AdvancementFrame.TASK, sugarcane, stacks27(Items.CACTUS));
        collect(consumer, "kelpfarm", Items.KELP, AdvancementFrame.TASK, sugarcane, stacks27(Items.KELP));
        collect(consumer, "bamboo", Items.BAMBOO, AdvancementFrame.TASK, sugarcane, stacks27(Items.BAMBOO));
        AdvancementEntry pumpkinfarm = collect(consumer, "pumpkinfarm", Items.PUMPKIN, AdvancementFrame.TASK, autofarm, stacks27(Items.PUMPKIN));
        collect(consumer, "melonfarm", Items.MELON, AdvancementFrame.TASK, pumpkinfarm, stacks27(Items.MELON));
        AdvancementEntry netherwartfarm = collect(consumer, "netherwartfarm", Items.NETHER_WART, AdvancementFrame.TASK, wheatfarm, stacks27(Items.NETHER_WART));
        collect(consumer, "potionfarm", Items.POTION, AdvancementFrame.TASK, netherwartfarm, stacks(18, Items.POTION)); // 保持原条件（18 个）
        collect(consumer, "sweetberryfarm", Items.SWEET_BERRIES, AdvancementFrame.TASK, wheatfarm, stacks27(Items.SWEET_BERRIES));
        AdvancementEntry seapicklefarm = collect(consumer, "seapicklefarm", Items.SEA_PICKLE, AdvancementFrame.TASK, autofarm, stacks27(Items.SEA_PICKLE));
        AdvancementEntry hoglinfarm = collect(consumer, "hoglinfarm", Items.COOKED_PORKCHOP, AdvancementFrame.GOAL, autofarm, stacks27(Items.COOKED_PORKCHOP));
        // 烤鸡机：9 组烤鸡 或 9 组羽毛 或 9 组鸡蛋（鸡蛋 16 个一组）
        collect(consumer, "lavachicken", Items.FEATHER, AdvancementFrame.TASK, hoglinfarm, stacks(9, Items.COOKED_CHICKEN, Items.FEATHER, Items.EGG));
        collect(consumer, "cowfarm", Items.COOKED_BEEF, AdvancementFrame.TASK, hoglinfarm, stacks27(Items.COOKED_BEEF));
        collect(consumer, "rabbitfarm", Items.RABBIT_HIDE, AdvancementFrame.TASK, hoglinfarm, stacks(9, Items.RABBIT_HIDE)); // 保持原条件
        // 蜂蜜块图标：蜂蜜农场，9 组蜂蜜块
        AdvancementEntry beehive = collect(consumer, "beehive", Items.HONEY_BLOCK, AdvancementFrame.TASK, autofarm, stacks(9, Items.HONEY_BLOCK));
        collect(consumer, "chorusfarm", Items.CHORUS_FLOWER, AdvancementFrame.TASK, autofarm, stacks27(Items.CHORUS_FRUIT));
        collect(consumer, "coralfarm", Items.BRAIN_CORAL_FAN, AdvancementFrame.TASK, seapicklefarm, stacks(9,
                Items.BRAIN_CORAL_FAN, Items.BUBBLE_CORAL_FAN, Items.FIRE_CORAL_FAN, Items.HORN_CORAL_FAN, Items.TUBE_CORAL_FAN));
        collect(consumer, "flowerfarm", Items.POPPY, AdvancementFrame.TASK, beehive, stacks(9,
                Items.DANDELION, Items.POPPY, Items.BLUE_ORCHID, Items.ALLIUM, Items.AZURE_BLUET, Items.CORNFLOWER,
                Items.LILY_OF_THE_VALLEY, Items.ORANGE_TULIP, Items.PINK_TULIP, Items.RED_TULIP, Items.WHITE_TULIP, Items.WITHER_ROSE));
        collect(consumer, "ripeningmachine", Items.DISPENSER, AdvancementFrame.TASK, autofarm, stacks27(
                Items.COCOA_BEANS, Items.GLOW_BERRIES, Items.VINE, Items.GLOW_LICHEN, Items.BIG_DRIPLEAF, Items.HANGING_ROOTS,
                Items.PINK_PETALS, Items.ROSE_BUSH, Items.LILAC, Items.PEONY, Items.SUNFLOWER));
    }

    // ---------------------------------------------------------------- 生电革命
    public void curcuitrevolutionRoot(Consumer<AdvancementEntry> consumer) {
        curcuitrevolution = Advancement.Builder.create()
                .display(Items.BEACON,
                        Text.translatable("advancements.curcuitrevolution.title"),
                        Text.translatable("advancements.curcuitrevolution.descr"),
                        Identifier.of("automationera", "gui/advancements/backgrounds/br"),
                        AdvancementFrame.TASK, true, true, false)
                .criterion("a", InventoryChangedCriterion.Conditions.items(Items.SHULKER_BOX)) // 普通（未染色）潜影盒
                .build(consumer, MOD_ID + ":" + "curcuitrevolution");
        advancementMap.put("curcuitrevolution", curcuitrevolution);

        AdvancementEntry tntquarry = impossible(consumer, "tntquarry", Items.TNT, AdvancementFrame.TASK, curcuitrevolution, "tntquarry");
        AdvancementEntry worldeater = impossible(consumer, "worldeater", Items.TNT_MINECART, AdvancementFrame.GOAL, tntquarry, "worldeater");
        AdvancementEntry perimeter = impossible(consumer, "perimeter", Items.BEDROCK, AdvancementFrame.GOAL, worldeater, "perimeter");
        AdvancementEntry realperimeter = impossible(consumer, "realperimeter", Items.STRUCTURE_VOID, AdvancementFrame.CHALLENGE, perimeter, "realperimeter");
        impossible(consumer, "netherperimeter", Items.LAVA_BUCKET, AdvancementFrame.CHALLENGE, realperimeter, "netherperimeter");
        AdvancementEntry fakepeaceful = impossible(consumer, "fakepeaceful", Items.BARRIER, AdvancementFrame.CHALLENGE, curcuitrevolution, "fakepeaceful");
        impossible(consumer, "lightsuppression", Items.LIGHT, AdvancementFrame.CHALLENGE, fakepeaceful, "lightsuppression");

        // 原 1 盒 → 9 盒
        collect(consumer, "doublepigman", Items.GOLD_BLOCK, AdvancementFrame.TASK, curcuitrevolution, boxes9(Items.GOLD_INGOT));
        collect(consumer, "bigfurnace", Items.BLAST_FURNACE, AdvancementFrame.TASK, curcuitrevolution, boxes9(Items.SMOOTH_STONE, Items.GLASS));
        collect(consumer, "alltree", Items.OAK_WOOD, AdvancementFrame.TASK, curcuitrevolution, boxes9(LOGS));
        collect(consumer, "perimobfarm", Items.ROTTEN_FLESH, AdvancementFrame.CHALLENGE, perimeter, boxes9(Items.BONE, Items.ROTTEN_FLESH));
        collect(consumer, "pericreeperfarm", Items.GUNPOWDER, AdvancementFrame.CHALLENGE, perimeter, boxes9(Items.GUNPOWDER));
        collect(consumer, "perislimefarm", Items.SLIME_BLOCK, AdvancementFrame.CHALLENGE, perimeter, boxes9(Items.SLIME_BALL));
        collect(consumer, "periwitherskel", Items.WITHER_SKELETON_SKULL, AdvancementFrame.CHALLENGE, perimeter, boxes9(Items.WITHER_SKELETON_SKULL));
        collect(consumer, "periguardianfarm", Items.SEA_LANTERN, AdvancementFrame.CHALLENGE, perimeter, boxes9(Items.SEA_LANTERN));

        // 经验农场：100 → 500 → 1000 级（链式）
        AdvancementEntry xp100 = impossible(consumer, "smallxpfarm", Items.EXPERIENCE_BOTTLE, AdvancementFrame.TASK, curcuitrevolution, "level");
        AdvancementEntry xp500 = impossible(consumer, "endlessxp", Items.ENCHANTED_BOOK, AdvancementFrame.GOAL, xp100, "level");
        impossible(consumer, "impossiblexp", Items.ENCHANTING_TABLE, AdvancementFrame.CHALLENGE, xp500, "level");

        // 生电模组（客户端上报已安装的模组）
        AdvancementEntry notvanilla = impossible(consumer, "notvanilla", Items.KNOWLEDGE_BOOK, AdvancementFrame.TASK, curcuitrevolution, "mod");
        impossible(consumer, "masamods", Items.WRITABLE_BOOK, AdvancementFrame.TASK, notvanilla, "mod");
        impossible(consumer, "carpetmod", Items.WHITE_CARPET, AdvancementFrame.TASK, notvanilla, "mod");
        impossible(consumer, "optimizemods", Items.CLOCK, AdvancementFrame.TASK, notvanilla, "mod");

        // 顶级成就：满级全下界合金信标（奖励由代码发放，受开发者选项控制）
        impossible(consumer, "netheritebeacon", Items.NETHERITE_BLOCK, AdvancementFrame.CHALLENGE, curcuitrevolution, "beacon");
    }

    public void Achivements(Consumer<AdvancementEntry> consumer) {
        AdvancementEntry stuckserver = impossible(consumer, "stuckserver", Items.COMMAND_BLOCK, AdvancementFrame.TASK, curcuitrevolution, "stuck_server");
        AdvancementEntry largeserver = impossible(consumer, "largeserver", Items.CHAIN_COMMAND_BLOCK, AdvancementFrame.GOAL, stuckserver, "large_server");
        impossible(consumer, "giantserver", Items.REPEATING_COMMAND_BLOCK, AdvancementFrame.CHALLENGE, largeserver, "giant_server");
        AdvancementEntry fastesttravel = impossible(consumer, "fastesttravel", Items.ENDER_PEARL, AdvancementFrame.CHALLENGE, netherhighway, "fastest_travel");
        impossible(consumer, "lighttravel", Items.BARRIER, AdvancementFrame.CHALLENGE, fastesttravel, "light_travel");
        impossible(consumer, "killdragon10time", Items.DRAGON_HEAD, AdvancementFrame.CHALLENGE, attribute, "kill_10time");
        AdvancementEntry afk = impossible(consumer, "afk", Items.STRUCTURE_VOID, AdvancementFrame.TASK, automaticFactory, "afk");
        impossible(consumer, "afkdie", Items.BARRIER, AdvancementFrame.TASK, afk, "afkdie");
        impossible(consumer, "openfurnace", Items.BLAST_FURNACE, AdvancementFrame.TASK, furnacegroup, "openfurnace");
        impossible(consumer, "placeobserver", Items.OBSERVER, AdvancementFrame.GOAL, automaticFactory, "placeobserver");
    }

    public void accept(Consumer<AdvancementEntry> consumer, RegistryWrapper.WrapperLookup registryLookup) {
        LOGGER.info("Registering advancements...");
        blockTypeRegistry = registryLookup.getOrThrow(RegistryKeys.BLOCK);
        itemTypeRegistry = registryLookup.getOrThrow(RegistryKeys.ITEM);
        structureRegistry = registryLookup.getOrThrow(RegistryKeys.STRUCTURE);
        autoRoot(consumer);
        attributeRoot(consumer);
        curcuitrevolutionRoot(consumer);
        Achivements(consumer);
        LOGGER.info("成功注册{}成就", createAdvancement);
    }

    // ---------------------------------------------------------------- 工具
    private AdvancementEntry collect(Consumer<AdvancementEntry> consumer, String id, Item displayItem, AdvancementFrame frame,
                                     AdvancementEntry parent, AdvancementCriterion<?> criterion) {
        AdvancementEntry advancement = Advancement.Builder.create()
                .parent(parent)
                .display(displayItem,
                        Text.translatable("advancements." + id + ".title"),
                        Text.translatable("advancements." + id + ".descr"),
                        null, frame, true, true, false)
                .criterion("a1", criterion)
                .build(consumer, MOD_ID + ":" + id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }

    /** 由服务端代码 grantCriterion(adv, criterionName) 发放的进度 */
    private AdvancementEntry impossible(Consumer<AdvancementEntry> consumer, String id, Item displayItem, AdvancementFrame frame,
                                        AdvancementEntry parent, String criterionName) {
        AdvancementEntry advancement = Advancement.Builder.create()
                .parent(parent)
                .display(displayItem,
                        Text.translatable("advancements." + id + ".title"),
                        Text.translatable("advancements." + id + ".descr"),
                        null, frame, true, true, false)
                .criterion(criterionName, Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, MOD_ID + ":" + id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }
}
