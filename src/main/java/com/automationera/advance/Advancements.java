package com.automationera.advance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.advancements.triggers.BredAnimalsTrigger;
import net.minecraft.advancements.triggers.ImpossibleTrigger;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * 进度定义（datagen 生成 src/main/generated/data/automationera/advancement/*.json）。
 * <p>
 * 0.1.9.1 机器进度统一规则：原 4 组 → 9 组；原 9 组 → 27 组或 1 盒；原 1 盒 → 9 盒。
 * 以下保持原条件：恶魂农场、凋零骷髅农场、杀凋机、兔农场、炼药机、海龟农场、晒铜机、混凝土工厂。
 */
public class Advancements {
    private static final String MOD_ID = "automationera";
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/Advancements");
    private final Map<String, AdvancementHolder> advancementMap = new HashMap<>();
    private int createAdvancement = 0;
    private HolderGetter<Item> itemTypeRegistry;
    private HolderGetter<Block> blockTypeRegistry;
    private HolderGetter<Structure> structureRegistry;

    private AdvancementHolder curcuitrevolution;
    private AdvancementHolder netherhighway;
    private AdvancementHolder attribute;
    private AdvancementHolder automaticFactory;
    private AdvancementHolder furnacegroup;

    // ---------------------------------------------------------------- 条件
    /** 原 9 组 → 27 组或 1 盒 */
    private static Criterion<?> stacks27(Item... items) {
        return FullStackCriterion.stacksOrBoxes(Set.of(items), 27, 1);
    }

    private static Criterion<?> stacks(int n, Item... items) {
        return FullStackCriterion.stacks(Set.of(items), n);
    }

    /** 原 1 盒 → 9 盒 */
    private static Criterion<?> boxes9(Item... items) {
        return FullStackCriterion.boxes(Set.of(items), 9);
    }

    private static final Item[] LOGS = {Items.OAK_LOG, Items.SPRUCE_LOG, Items.BIRCH_LOG, Items.JUNGLE_LOG, Items.ACACIA_LOG,
            Items.DARK_OAK_LOG, Items.MANGROVE_LOG, Items.CHERRY_LOG, Items.PALE_OAK_LOG, Items.CRIMSON_STEM, Items.WARPED_STEM};

    // ---------------------------------------------------------------- 自动化工厂
    public void autoRoot(Consumer<AdvancementHolder> consumer) {
        automaticFactory = Advancement.Builder.advancement()
                .display(Items.REDSTONE,
                        Component.translatable("advancements.automaticfactory.title"),
                        Component.translatable("advancements.automaticfactory.descr"),
                        Identifier.fromNamespaceAndPath("automationera", "gui/advancements/backgrounds/rs"),
                        AdvancementType.TASK, true, true, false)
                .addCriterion("a", InventoryChangeTrigger.TriggerInstance.hasItems(Items.REDSTONE))
                .save(consumer, MOD_ID + ":" + "automaticfactory");
        advancementMap.put("automaticfactory", automaticFactory);

        AdvancementHolder woodfarm = collect(consumer, "woodfarm", Items.OAK_LOG, AdvancementType.GOAL, automaticFactory, stacks27(LOGS));
        AdvancementHolder stonefarm = collect(consumer, "stonefarm", Items.COBBLESTONE, AdvancementType.GOAL, automaticFactory, stacks27(Items.COBBLESTONE));
        collect(consumer, "icefarm", Items.ICE, AdvancementType.TASK, stonefarm, stacks27(Items.ICE));
        collect(consumer, "basaltfarm", Items.BASALT, AdvancementType.TASK, stonefarm, stacks27(Items.BASALT));
        collect(consumer, "dripstonefarm", Items.DRIPSTONE_BLOCK, AdvancementType.TASK, stonefarm, stacks27(Items.DRIPSTONE_BLOCK));
        // 熔炉组：玻璃或石头（任意一种）
        furnacegroup = collect(consumer, "furnacegroup", Items.FURNACE, AdvancementType.TASK, stonefarm, stacks27(Items.GLASS, Items.STONE));
        AdvancementHolder ironfarm = collect(consumer, "ironfarm", Items.IRON_INGOT, AdvancementType.GOAL, automaticFactory, stacks27(Items.IRON_INGOT));
        collect(consumer, "amethystfarm", Items.AMETHYST_SHARD, AdvancementType.TASK, ironfarm, stacks27(Items.AMETHYST_SHARD));
        AdvancementHolder sandfarm = collect(consumer, "sandfarm", Items.SAND, AdvancementType.GOAL, ironfarm, stacks27(Items.SAND));
        AdvancementHolder dirtfarm = collect(consumer, "dirtfarm", Items.DIRT, AdvancementType.TASK, sandfarm, stacks27(Items.DIRT));
        collect(consumer, "clayfarm", Items.CLAY, AdvancementType.TASK, dirtfarm, stacks27(Items.CLAY));
        collect(consumer, "mudfarm", Items.MUD, AdvancementType.TASK, dirtfarm, stacks27(Items.MUD));
        AdvancementHolder bonefarm = collect(consumer, "bonefarm", Items.BONE_MEAL, AdvancementType.GOAL, woodfarm, stacks27(Items.BONE_MEAL));
        collect(consumer, "obsidianfarm", Items.OBSIDIAN, AdvancementType.TASK, sandfarm, stacks27(Items.OBSIDIAN));

        AdvancementHolder abovenether = impossible(consumer, "abovenether", Items.BEDROCK, AdvancementType.TASK, automaticFactory, "above_nether");
        netherhighway = Advancement.Builder.advancement()
                .parent(abovenether)
                .display(Items.BLUE_ICE,
                        Component.translatable("advancements.netherhighway.title"),
                        Component.translatable("advancements.netherhighway.descr"),
                        null, AdvancementType.GOAL, true, true, false)
                .addCriterion("ice", PlacedBlockInNetherCriterion.conditions(
                        BlockPredicate.Builder.block().of(blockTypeRegistry, Blocks.PACKED_ICE).build(),
                        LocationPredicate.Builder.location().setDimension(Level.NETHER).build()))
                .save(consumer, MOD_ID + ":" + "netherhighway");
        advancementMap.put("netherhighway", netherhighway);

        collect(consumer, "railfarm", Items.POWERED_RAIL, AdvancementType.TASK, ironfarm,
                stacks27(Items.RAIL, Items.POWERED_RAIL, Items.DETECTOR_RAIL, Items.ACTIVATOR_RAIL));
        // 晒铜机：保持原条件
        collect(consumer, "copperfarm", Items.COPPER_BLOCK.weathering().exposed(), AdvancementType.TASK, stonefarm,
                stacks(4, Items.COPPER_BLOCK.weathering().exposed(), Items.COPPER_BLOCK.weathering().weathered(), Items.COPPER_BLOCK.weathering().oxidized()));
        collect(consumer, "leaffarm", Items.OAK_LEAVES, AdvancementType.TASK, bonefarm, stacks27(
                Items.OAK_LEAVES, Items.SPRUCE_LEAVES, Items.BIRCH_LEAVES, Items.JUNGLE_LEAVES, Items.ACACIA_LEAVES,
                Items.DARK_OAK_LEAVES, Items.MANGROVE_LEAVES, Items.CHERRY_LEAVES, Items.PALE_OAK_LEAVES,
                Items.AZALEA_LEAVES, Items.FLOWERING_AZALEA_LEAVES));
        collect(consumer, "fungusfarm", Items.CRIMSON_STEM, AdvancementType.TASK, bonefarm, stacks27(Items.CRIMSON_STEM, Items.WARPED_STEM));
        collect(consumer, "mushroomfarm", Items.RED_MUSHROOM_BLOCK, AdvancementType.TASK, bonefarm, stacks27(
                Items.BROWN_MUSHROOM, Items.RED_MUSHROOM, Items.RED_MUSHROOM_BLOCK, Items.BROWN_MUSHROOM_BLOCK));
        // 混凝土工厂：保持原条件
        collect(consumer, "concretefarm", Items.CONCRETE.white(), AdvancementType.TASK, sandfarm, stacks(9,
                Items.CONCRETE.white(), Items.CONCRETE.orange(), Items.CONCRETE.magenta(), Items.CONCRETE.lightBlue(),
                Items.CONCRETE.yellow(), Items.CONCRETE.lime(), Items.CONCRETE.pink(), Items.CONCRETE.gray(),
                Items.CONCRETE.lightGray(), Items.CONCRETE.cyan(), Items.CONCRETE.purple(), Items.CONCRETE.blue(),
                Items.CONCRETE.brown(), Items.CONCRETE.green(), Items.CONCRETE.red(), Items.CONCRETE.black()));
    }

    // ---------------------------------------------------------------- 生物农场（原“村民工程”根）
    public void attributeRoot(Consumer<AdvancementHolder> consumer) {
        // id 保持 attribute，玩家已有进度不丢；获得小麦/土豆/胡萝卜或红石粉即可
        attribute = Advancement.Builder.advancement()
                .display(Items.VILLAGER_SPAWN_EGG,
                        Component.translatable("advancements.attribute.title"),
                        Component.translatable("advancements.attribute.descr"),
                        Identifier.fromNamespaceAndPath("automationera", "gui/advancements/backgrounds/ss"),
                        AdvancementType.TASK, true, true, false)
                .addCriterion("a", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
                        .of(itemTypeRegistry, Items.WHEAT, Items.POTATO, Items.CARROT, Items.REDSTONE)))
                .save(consumer, MOD_ID + ":" + "attribute");
        advancementMap.put("attribute", attribute);

        // 村民工程：二级成就（与畜牧业同级），到达村庄触发；自动农场挂在它下面
        AdvancementHolder village = Advancement.Builder.advancement()
                .parent(attribute)
                .display(Items.BELL,
                        Component.translatable("advancements.village.title"),
                        Component.translatable("advancements.village.descr"),
                        null, AdvancementType.TASK, true, true, false)
                .addCriterion("village", PlayerTrigger.TriggerInstance.located(
                        LocationPredicate.Builder.location().setStructures(structureRegistry.getOrThrow(StructureTags.VILLAGE))))
                .save(consumer, MOD_ID + ":" + "village");
        advancementMap.put("village", village);
        collect(consumer, "stringfarm", Items.STRING, AdvancementType.TASK, village, stacks27(Items.STRING));
        collect(consumer, "tripwire", Items.TRIPWIRE_HOOK, AdvancementType.TASK, village, stacks27(Items.TRIPWIRE_HOOK));
        collect(consumer, "raidfarm", Items.EMERALD, AdvancementType.TASK, village, stacks27(Items.EMERALD));
        impossible(consumer, "tradingpost", Items.EMERALD, AdvancementType.GOAL, village, "trading_post");

        AdvancementHolder animalfarm = Advancement.Builder.advancement()
                .parent(attribute)
                .display(Items.BEEF,
                        Component.translatable("advancements.animalfarm.title"),
                        Component.translatable("advancements.animalfarm.descr"),
                        null, AdvancementType.TASK, true, true, false)
                .addCriterion("a", BredAnimalsTrigger.TriggerInstance.bredAnimals())
                .save(consumer, MOD_ID + ":" + "animalfarm");
        advancementMap.put("animalfarm", animalfarm);

        collect(consumer, "turtlefarm", Items.TURTLE_EGG, AdvancementType.TASK, animalfarm, stacks(4, Items.TURTLE_EGG)); // 保持原条件
        collect(consumer, "snowfarm", Items.SNOW_BLOCK, AdvancementType.TASK, animalfarm, stacks27(Items.SNOW_BLOCK));
        AdvancementHolder woolfarm = collect(consumer, "woolfarm", Items.WOOL.white(), AdvancementType.TASK, animalfarm, stacks27(
                Items.WOOL.white(), Items.WOOL.orange(), Items.WOOL.magenta(), Items.WOOL.lightBlue(), Items.WOOL.yellow(),
                Items.WOOL.lime(), Items.WOOL.pink(), Items.WOOL.gray(), Items.WOOL.lightGray(), Items.WOOL.cyan(),
                Items.WOOL.purple(), Items.WOOL.blue(), Items.WOOL.brown(), Items.WOOL.green(), Items.WOOL.red(), Items.WOOL.black()));
        // 鱿鱼农场拆成普通/发光两个
        collect(consumer, "squidfarm", Items.INK_SAC, AdvancementType.TASK, animalfarm, stacks27(Items.INK_SAC));
        collect(consumer, "glowsquidfarm", Items.GLOW_INK_SAC, AdvancementType.TASK, animalfarm, stacks27(Items.GLOW_INK_SAC));
        collect(consumer, "snifferfarm", Items.SNIFFER_EGG, AdvancementType.TASK, woolfarm, stacks27(Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD));
        // 蜂箱图标：蜜脾农场，9 组蜜脾
        collect(consumer, "honeyfarm", Items.BEEHIVE, AdvancementType.TASK, animalfarm, stacks(9, Items.HONEYCOMB));

        AdvancementHolder mobfarm = collect(consumer, "mobfarm", Items.ROTTEN_FLESH, AdvancementType.TASK, attribute,
                stacks(9, Items.BONE, Items.ROTTEN_FLESH, Items.GUNPOWDER));
        collect(consumer, "shulkerfarm", Items.SHULKER_SHELL, AdvancementType.GOAL, mobfarm, stacks27(Items.SHULKER_SHELL));
        collect(consumer, "witchfarm", Items.REDSTONE, AdvancementType.TASK, mobfarm, stacks27(Items.REDSTONE));
        collect(consumer, "creeperfarm", Items.GUNPOWDER, AdvancementType.TASK, mobfarm, stacks27(Items.GUNPOWDER));
        collect(consumer, "ghastfarm", Items.GHAST_TEAR, AdvancementType.TASK, mobfarm, stacks(4, Items.GHAST_TEAR)); // 保持原条件
        collect(consumer, "slimefarm", Items.SLIME_BALL, AdvancementType.TASK, mobfarm, stacks27(Items.SLIME_BALL));
        collect(consumer, "blaze", Items.BLAZE_ROD, AdvancementType.TASK, mobfarm, stacks27(Items.BLAZE_ROD));
        AdvancementHolder endermanfarm = collect(consumer, "endermanfarm", Items.ENDER_PEARL, AdvancementType.TASK, mobfarm, stacks27(Items.ENDER_PEARL));
        AdvancementHolder pigmanfarm = collect(consumer, "pigmanfarm", Items.GOLD_INGOT, AdvancementType.GOAL, endermanfarm, stacks27(Items.GOLD_INGOT));
        AdvancementHolder witherrose = collect(consumer, "witherrose", Items.WITHER_ROSE, AdvancementType.TASK, mobfarm, stacks27(Items.WITHER_ROSE));
        AdvancementHolder witherskel = collect(consumer, "witherskel", Items.WITHER_SKELETON_SKULL, AdvancementType.TASK, witherrose,
                stacks(4, Items.WITHER_SKELETON_SKULL)); // 保持原条件（简介去掉煤炭）
        collect(consumer, "witherkill", Items.NETHER_STAR, AdvancementType.CHALLENGE, witherskel, stacks(9, Items.NETHER_STAR)); // 保持原条件
        AdvancementHolder guardianfarm = collect(consumer, "guardianfarm", Items.PRISMARINE_SHARD, AdvancementType.TASK, mobfarm, stacks27(Items.PRISMARINE_SHARD));
        collect(consumer, "drownedfarm", Items.TRIDENT, AdvancementType.TASK, guardianfarm, stacks(9, Items.TRIDENT));
        collect(consumer, "magmacubefarm", Items.MAGMA_CREAM, AdvancementType.TASK, witherrose, stacks27(Items.MAGMA_CREAM));
        collect(consumer, "froglightfarm", Items.OCHRE_FROGLIGHT, AdvancementType.TASK, witherrose,
                stacks27(Items.OCHRE_FROGLIGHT, Items.PEARLESCENT_FROGLIGHT, Items.VERDANT_FROGLIGHT));
        collect(consumer, "piglintrade", Items.QUARTZ, AdvancementType.TASK, pigmanfarm, stacks27(Items.QUARTZ, Items.CRYING_OBSIDIAN));

        AdvancementHolder autofarm = collect(consumer, "autofarm", Items.CARROT, AdvancementType.TASK, village,
                stacks(9, Items.WHEAT, Items.POTATO, Items.CARROT));
        AdvancementHolder wheatfarm = collect(consumer, "wheatfarm", Items.WHEAT, AdvancementType.TASK, autofarm, stacks27(Items.WHEAT));
        AdvancementHolder sugarcane = collect(consumer, "sugarcane", Items.SUGAR_CANE, AdvancementType.TASK, wheatfarm, stacks27(Items.SUGAR_CANE));
        collect(consumer, "cactusfarm", Items.CACTUS, AdvancementType.TASK, sugarcane, stacks27(Items.CACTUS));
        collect(consumer, "kelpfarm", Items.KELP, AdvancementType.TASK, sugarcane, stacks27(Items.KELP));
        collect(consumer, "bamboo", Items.BAMBOO, AdvancementType.TASK, sugarcane, stacks27(Items.BAMBOO));
        AdvancementHolder pumpkinfarm = collect(consumer, "pumpkinfarm", Items.PUMPKIN, AdvancementType.TASK, autofarm, stacks27(Items.PUMPKIN));
        collect(consumer, "melonfarm", Items.MELON, AdvancementType.TASK, pumpkinfarm, stacks27(Items.MELON));
        AdvancementHolder netherwartfarm = collect(consumer, "netherwartfarm", Items.NETHER_WART, AdvancementType.TASK, wheatfarm, stacks27(Items.NETHER_WART));
        collect(consumer, "potionfarm", Items.POTION, AdvancementType.TASK, netherwartfarm, stacks(18, Items.POTION)); // 保持原条件（18 个）
        collect(consumer, "sweetberryfarm", Items.SWEET_BERRIES, AdvancementType.TASK, wheatfarm, stacks27(Items.SWEET_BERRIES));
        AdvancementHolder seapicklefarm = collect(consumer, "seapicklefarm", Items.SEA_PICKLE, AdvancementType.TASK, autofarm, stacks27(Items.SEA_PICKLE));
        AdvancementHolder hoglinfarm = collect(consumer, "hoglinfarm", Items.COOKED_PORKCHOP, AdvancementType.GOAL, autofarm, stacks27(Items.COOKED_PORKCHOP));
        // 烤鸡机：9 组烤鸡 或 9 组羽毛 或 9 组鸡蛋（鸡蛋 16 个一组）
        collect(consumer, "lavachicken", Items.FEATHER, AdvancementType.TASK, hoglinfarm, stacks(9, Items.COOKED_CHICKEN, Items.FEATHER, Items.EGG));
        collect(consumer, "cowfarm", Items.COOKED_BEEF, AdvancementType.TASK, hoglinfarm, stacks27(Items.COOKED_BEEF));
        collect(consumer, "rabbitfarm", Items.RABBIT_HIDE, AdvancementType.TASK, hoglinfarm, stacks(9, Items.RABBIT_HIDE)); // 保持原条件
        // 蜂蜜块图标：蜂蜜农场，9 组蜂蜜块
        AdvancementHolder beehive = collect(consumer, "beehive", Items.HONEY_BLOCK, AdvancementType.TASK, autofarm, stacks(9, Items.HONEY_BLOCK));
        collect(consumer, "chorusfarm", Items.CHORUS_FLOWER, AdvancementType.TASK, autofarm, stacks27(Items.CHORUS_FRUIT));
        collect(consumer, "coralfarm", Items.BRAIN_CORAL_FAN, AdvancementType.TASK, seapicklefarm, stacks(9,
                Items.BRAIN_CORAL_FAN, Items.BUBBLE_CORAL_FAN, Items.FIRE_CORAL_FAN, Items.HORN_CORAL_FAN, Items.TUBE_CORAL_FAN));
        collect(consumer, "flowerfarm", Items.POPPY, AdvancementType.TASK, beehive, stacks(9,
                Items.DANDELION, Items.POPPY, Items.BLUE_ORCHID, Items.ALLIUM, Items.AZURE_BLUET, Items.CORNFLOWER,
                Items.LILY_OF_THE_VALLEY, Items.ORANGE_TULIP, Items.PINK_TULIP, Items.RED_TULIP, Items.WHITE_TULIP, Items.WITHER_ROSE));
        collect(consumer, "ripeningmachine", Items.DISPENSER, AdvancementType.TASK, autofarm, stacks27(
                Items.COCOA_BEANS, Items.GLOW_BERRIES, Items.VINE, Items.GLOW_LICHEN, Items.BIG_DRIPLEAF, Items.HANGING_ROOTS,
                Items.PINK_PETALS, Items.ROSE_BUSH, Items.LILAC, Items.PEONY, Items.SUNFLOWER));
    }

    // ---------------------------------------------------------------- 生电革命
    public void curcuitrevolutionRoot(Consumer<AdvancementHolder> consumer) {
        curcuitrevolution = Advancement.Builder.advancement()
                .display(Items.BEACON,
                        Component.translatable("advancements.curcuitrevolution.title"),
                        Component.translatable("advancements.curcuitrevolution.descr"),
                        Identifier.fromNamespaceAndPath("automationera", "gui/advancements/backgrounds/br"),
                        AdvancementType.TASK, true, true, false)
                .addCriterion("a", InventoryChangeTrigger.TriggerInstance.hasItems(Items.SHULKER_BOX)) // 普通（未染色）潜影盒
                .save(consumer, MOD_ID + ":" + "curcuitrevolution");
        advancementMap.put("curcuitrevolution", curcuitrevolution);

        AdvancementHolder tntquarry = impossible(consumer, "tntquarry", Items.TNT, AdvancementType.TASK, curcuitrevolution, "tntquarry");
        AdvancementHolder worldeater = impossible(consumer, "worldeater", Items.TNT_MINECART, AdvancementType.GOAL, tntquarry, "worldeater");
        AdvancementHolder perimeter = impossible(consumer, "perimeter", Items.BEDROCK, AdvancementType.GOAL, worldeater, "perimeter");
        AdvancementHolder realperimeter = impossible(consumer, "realperimeter", Items.STRUCTURE_VOID, AdvancementType.CHALLENGE, perimeter, "realperimeter");
        impossible(consumer, "netherperimeter", Items.LAVA_BUCKET, AdvancementType.CHALLENGE, realperimeter, "netherperimeter");
        AdvancementHolder fakepeaceful = impossible(consumer, "fakepeaceful", Items.BARRIER, AdvancementType.CHALLENGE, curcuitrevolution, "fakepeaceful");
        impossible(consumer, "lightsuppression", Items.LIGHT, AdvancementType.CHALLENGE, fakepeaceful, "lightsuppression");

        // 原 1 盒 → 9 盒
        collect(consumer, "doublepigman", Items.GOLD_BLOCK, AdvancementType.TASK, curcuitrevolution, boxes9(Items.GOLD_INGOT));
        collect(consumer, "bigfurnace", Items.BLAST_FURNACE, AdvancementType.TASK, curcuitrevolution, boxes9(Items.SMOOTH_STONE, Items.GLASS));
        collect(consumer, "alltree", Items.OAK_WOOD, AdvancementType.TASK, curcuitrevolution, boxes9(LOGS));
        collect(consumer, "perimobfarm", Items.ROTTEN_FLESH, AdvancementType.CHALLENGE, perimeter, boxes9(Items.BONE, Items.ROTTEN_FLESH));
        collect(consumer, "pericreeperfarm", Items.GUNPOWDER, AdvancementType.CHALLENGE, perimeter, boxes9(Items.GUNPOWDER));
        collect(consumer, "perislimefarm", Items.SLIME_BLOCK, AdvancementType.CHALLENGE, perimeter, boxes9(Items.SLIME_BALL));
        collect(consumer, "periwitherskel", Items.WITHER_SKELETON_SKULL, AdvancementType.CHALLENGE, perimeter, boxes9(Items.WITHER_SKELETON_SKULL));
        collect(consumer, "periguardianfarm", Items.SEA_LANTERN, AdvancementType.CHALLENGE, perimeter, boxes9(Items.SEA_LANTERN));

        // 经验农场：100 → 500 → 1000 级（链式）
        AdvancementHolder xp100 = impossible(consumer, "smallxpfarm", Items.EXPERIENCE_BOTTLE, AdvancementType.TASK, curcuitrevolution, "level");
        AdvancementHolder xp500 = impossible(consumer, "endlessxp", Items.ENCHANTED_BOOK, AdvancementType.GOAL, xp100, "level");
        impossible(consumer, "impossiblexp", Items.ENCHANTING_TABLE, AdvancementType.CHALLENGE, xp500, "level");

        // 生电模组（客户端上报已安装的模组）
        AdvancementHolder notvanilla = impossible(consumer, "notvanilla", Items.KNOWLEDGE_BOOK, AdvancementType.TASK, curcuitrevolution, "mod");
        impossible(consumer, "masamods", Items.WRITABLE_BOOK, AdvancementType.TASK, notvanilla, "mod");
        impossible(consumer, "carpetmod", Items.CARPET.white(), AdvancementType.TASK, notvanilla, "mod");
        impossible(consumer, "optimizemods", Items.CLOCK, AdvancementType.TASK, notvanilla, "mod");

        // 顶级成就：满级全下界合金信标（奖励由代码发放，受开发者选项控制）
        impossible(consumer, "netheritebeacon", Items.NETHERITE_BLOCK, AdvancementType.CHALLENGE, curcuitrevolution, "beacon");
    }

    public void Achivements(Consumer<AdvancementHolder> consumer) {
        AdvancementHolder stuckserver = impossible(consumer, "stuckserver", Items.COMMAND_BLOCK, AdvancementType.TASK, curcuitrevolution, "stuck_server");
        AdvancementHolder largeserver = impossible(consumer, "largeserver", Items.CHAIN_COMMAND_BLOCK, AdvancementType.GOAL, stuckserver, "large_server");
        impossible(consumer, "giantserver", Items.REPEATING_COMMAND_BLOCK, AdvancementType.CHALLENGE, largeserver, "giant_server");
        AdvancementHolder fastesttravel = impossible(consumer, "fastesttravel", Items.ENDER_PEARL, AdvancementType.CHALLENGE, netherhighway, "fastest_travel");
        impossible(consumer, "lighttravel", Items.BARRIER, AdvancementType.CHALLENGE, fastesttravel, "light_travel");
        impossible(consumer, "killdragon10time", Items.DRAGON_HEAD, AdvancementType.CHALLENGE, attribute, "kill_10time");
        AdvancementHolder afk = impossible(consumer, "afk", Items.STRUCTURE_VOID, AdvancementType.TASK, automaticFactory, "afk");
        impossible(consumer, "afkdie", Items.BARRIER, AdvancementType.TASK, afk, "afkdie");
        impossible(consumer, "openfurnace", Items.BLAST_FURNACE, AdvancementType.TASK, furnacegroup, "openfurnace");
        impossible(consumer, "placeobserver", Items.OBSERVER, AdvancementType.GOAL, automaticFactory, "placeobserver");
    }

    public void accept(Consumer<AdvancementHolder> consumer, HolderLookup.Provider registryLookup) {
        LOGGER.info("Registering advancements...");
        blockTypeRegistry = registryLookup.lookupOrThrow(Registries.BLOCK);
        itemTypeRegistry = registryLookup.lookupOrThrow(Registries.ITEM);
        structureRegistry = registryLookup.lookupOrThrow(Registries.STRUCTURE);
        autoRoot(consumer);
        attributeRoot(consumer);
        curcuitrevolutionRoot(consumer);
        Achivements(consumer);
        LOGGER.info("成功注册{}成就", createAdvancement);
    }

    // ---------------------------------------------------------------- 工具
    private AdvancementHolder collect(Consumer<AdvancementHolder> consumer, String id, Item displayItem, AdvancementType frame,
                                     AdvancementHolder parent, Criterion<?> criterion) {
        AdvancementHolder advancement = Advancement.Builder.advancement()
                .parent(parent)
                .display(displayItem,
                        Component.translatable("advancements." + id + ".title"),
                        Component.translatable("advancements." + id + ".descr"),
                        null, frame, true, true, false)
                .addCriterion("a1", criterion)
                .save(consumer, MOD_ID + ":" + id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }

    /** 由服务端代码 grantCriterion(adv, criterionName) 发放的进度 */
    private AdvancementHolder impossible(Consumer<AdvancementHolder> consumer, String id, Item displayItem, AdvancementType frame,
                                        AdvancementHolder parent, String criterionName) {
        AdvancementHolder advancement = Advancement.Builder.advancement()
                .parent(parent)
                .display(displayItem,
                        Component.translatable("advancements." + id + ".title"),
                        Component.translatable("advancements." + id + ".descr"),
                        null, frame, true, true, false)
                .addCriterion(criterionName, CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, MOD_ID + ":" + id);
        advancementMap.put(id, advancement);
        createAdvancement++;
        return advancement;
    }
}
