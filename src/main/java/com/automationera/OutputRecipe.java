package com.automationera;

import com.ibm.icu.impl.ICURegionDataTables;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputRecipe {
    public Map<String, List<Item>> OutputdRecipy() {
        Map<String, List<Item>> ore = new HashMap<>();
        ore.put("stone", List.of(Items.COBBLESTONE));
        ore.put("wood", List.of(
                Items.OAK_LOG,
                Items.SPRUCE_LOG,
                Items.BIRCH_LOG,
                Items.JUNGLE_LOG,
                Items.ACACIA_LOG,
                Items.DARK_OAK_LOG,
                Items.MANGROVE_LOG,
                Items.CHERRY_LOG));
        ore.put("iron",List.of(
                Items.IRON_INGOT,
                Items.POPPY
        ));
        ore.put("mob",List.of(
                Items.GUNPOWDER,
                Items.ARROW,
                Items.TIPPED_ARROW,
                Items.BONE,
                Items.STRING,
                Items.ROTTEN_FLESH,
                Items.REDSTONE,
                Items.GLOWSTONE_DUST,
                Items.GLASS_BOTTLE,
                Items.SPIDER_EYE,
                Items.SUGAR,
                Items.SLIME_BALL
        ));
        ore.put("farm",List.of(
                Items.WHEAT,
                Items.WHEAT_SEEDS,
                Items.CARROT,
                Items.POTATO,
                Items.BEETROOT,
                Items.BEETROOT_SEEDS
        ));
        ore.put("sand",List.of(
                Items.SAND,
                Items.RED_SAND,
                Items.GRAVEL,
                Items.DRAGON_EGG,
                Items.ANVIL,
                Items.BLACK_CONCRETE_POWDER,
                Items.WHITE_CONCRETE_POWDER,
                Items.GRAY_CONCRETE_POWDER,
                Items.LIGHT_GRAY_CONCRETE_POWDER,
                Items.BROWN_CONCRETE_POWDER,
                Items.RED_CONCRETE_POWDER,
                Items.ORANGE_CONCRETE_POWDER,
                Items.YELLOW_CONCRETE_POWDER,
                Items.GREEN_CONCRETE_POWDER,
                Items.LIME_CONCRETE_POWDER,
                Items.CYAN_CONCRETE_POWDER,
                Items.LIGHT_BLUE_CONCRETE_POWDER,
                Items.BLUE_CONCRETE_POWDER,
                Items.PURPLE_CONCRETE_POWDER,
                Items.MAGENTA_CONCRETE_POWDER,
                Items.PINK_CONCRETE_POWDER
        ));
        ore.put("ice",List.of(Items.ICE));
        ore.put("rail",List.of(
                Items.RAIL,
                Items.ACTIVATOR_RAIL,
                Items.DETECTOR_RAIL,
                Items.POWERED_RAIL
        ));
        ore.put("bonemeal",List.of(
                Items.BONE_MEAL,
                Items.MOSS_BLOCK,
                Items.MOSS_CARPET,
                Items.AZALEA,
                Items.FLOWERING_AZALEA
        ));
        ore.put("sugarcane",List.of(Items.SUGAR_CANE));
        ore.put("wart",List.of(Items.NETHER_WART));
        ore.put("bamboo",List.of(Items.BAMBOO));
        ore.put("pork",List.of(
                Items.COOKED_PORKCHOP,
                Items.LEATHER));
        ore.put("slime",List.of(Items.SLIME_BALL));
        ore.put("enderman",List.of(Items.ENDER_PEARL));
        ore.put("pigman",List.of(
                Items.GOLD_INGOT,
                Items.GOLD_NUGGET,
                Items.ROTTEN_FLESH
        ));
        ore.put("cactus",List.of(Items.CACTUS));
        ore.put("melon",List.of(
                Items.PUMPKIN,
                Items.MELON,
                Items.MELON_SLICE
        ));
        ore.put("berry",List.of(
                Items.SWEET_BERRIES
        ));
        ore.put("blade",List.of(
                Items.BLAZE_ROD
        ));
        ore.put("chicken",List.of(
                Items.COOKED_CHICKEN,
                Items.EGG,
                Items.FEATHER
        ));
        ore.put("chorus",List.of(
                Items.CHORUS_FRUIT,
                Items.CHORUS_FLOWER
        ));
        ore.put("clay",List.of(
                Items.DIRT,
                Items.CLAY,
                Items.MUD,
                Items.OAK_LOG,
                Items.BONE_MEAL
        ));
        ore.put("copper",List.of(
                Items.EXPOSED_COPPER,
                Items.WEATHERED_COPPER,
                Items.OXIDIZED_COPPER
        ));
        ore.put("creeper",List.of(
                Items.GUNPOWDER
        ));
        ore.put("cristal",List.of(
                Items.AMETHYST_SHARD
        ));
        ore.put("drone",List.of(
                Items.ROTTEN_FLESH,
                Items.COPPER_INGOT,
                Items.TRIDENT
        ));
        ore.put("flower",List.of(
                Items.ALLIUM,               // 绒球葱
                Items.AZURE_BLUET,          // 雾灰色美氏花
                Items.BLUE_ORCHID,          // 蓝色风信子
                Items.CORNFLOWER,           // 矢车菊
                Items.DANDELION,            // 蒲公英
                Items.LILY_OF_THE_VALLEY,   // 铃兰
                Items.OXEYE_DAISY,          // 滨菊
                Items.POPPY,                // 罂粟
                Items.ORANGE_TULIP,         // 橙色郁金香
                Items.PINK_TULIP,           // 粉红色郁金香
                Items.RED_TULIP,            // 红色郁金香
                Items.WHITE_TULIP           // 白色郁金香
        ));
        ore.put("froglight",List.of(
                Items.OCHRE_FROGLIGHT,
                Items.VERDANT_FROGLIGHT,
                Items.PEARLESCENT_FROGLIGHT
        ));
        ore.put("ghast",List.of(
                Items.GHAST_TEAR,
                Items.GUNPOWDER
        ));
        ore.put("honey",List.of(
                Items.HONEY_BOTTLE,
                Items.HONEYCOMB
        ));
        ore.put("hook",List.of(
                Items.TRIPWIRE_HOOK
        ));
        ore.put("kelp",List.of(
                Items.KELP
        ));
        ore.put("magmacube",List.of(
                Items.MAGMA_CREAM
        ));
        ore.put("marine",List.of(
                Items.PRISMARINE_SHARD,
                Items.PRISMARINE_CRYSTALS,
                Items.COD,
                Items.SALMON,
                Items.COOKED_COD,
                Items.COOKED_SALMON,
                Items.PUFFERFISH,
                Items.TROPICAL_FISH
        ));
        ore.put("mushroom",List.of(
                Items.BROWN_MUSHROOM,
                Items.RED_MUSHROOM
        ));
        ore.put("mushroom2",List.of(
                Items.BROWN_MUSHROOM,
                Items.RED_MUSHROOM,
                Items.RED_MUSHROOM_BLOCK,
                Items.BROWN_MUSHROOM_BLOCK,
                Items.MUSHROOM_STEM
        ));
        ore.put("pickle",List.of(
                Items.SEA_PICKLE
        ));
        ore.put("piglin",List.of(
                Items.BLACKSTONE,
                Items.GRAVEL,
                Items.SPECTRAL_ARROW,
                Items.NETHER_BRICK,
                Items.SOUL_SAND,
                Items.LEATHER,
                Items.CRYING_OBSIDIAN,
                Items.OBSIDIAN,
                Items.QUARTZ,
                Items.STRING,
                Items.ENDER_PEARL,
                Items.IRON_NUGGET,
                Items.ENCHANTED_BOOK,
                Items.POTION
        ));
        ore.put("rabbit",List.of(
                Items.COOKED_RABBIT,
                Items.RABBIT_HIDE,
                Items.RABBIT_FOOT
        ));
        ore.put("raid",List.of(
                Items.EMERALD,
                Items.TOTEM_OF_UNDYING,
                Items.CROSSBOW,
                Items.ENCHANTED_BOOK,
                Items.WHITE_BANNER,
                Items.SADDLE,
                Items.GLASS_BOTTLE,
                Items.GLOWSTONE_DUST,
                Items.GUNPOWDER,
                Items.REDSTONE,
                Items.SPIDER_EYE,
                Items.SUGAR,
                Items.STICK
        ));
        ore.put("ripend",List.of(
                Items.GLOW_BERRIES,
                Items.BIG_DRIPLEAF,
                Items.WHEAT,
                Items.WHEAT_SEEDS,
                Items.CARROT,
                Items.POTATO,
                Items.BEETROOT,
                Items.BEETROOT_SEEDS,
                Items.COCOA_BEANS,
                Items.HANGING_ROOTS,
                Items.BAMBOO,
                Items.TORCHFLOWER,
                Items.TORCHFLOWER_SEEDS,
                Items.PITCHER_PLANT,
                Items.PITCHER_POD
        ));
        ore.put("shulker",List.of(
                Items.SHULKER_SHELL
        ));
        ore.put("sniffer",List.of(
                Items.TORCHFLOWER_SEEDS,
                Items.PITCHER_POD
        ));
        ore.put("snow",List.of(
                Items.SNOWBALL
        ));
        ore.put("string",List.of(
                Items.STRING
        ));
        ore.put("wheat",List.of(
                Items.WHEAT,
                Items.WHEAT_SEEDS,
                Items.BEETROOT,
                Items.BEETROOT_SEEDS
        ));
        ore.put("leaf",List.of(
                Items.OAK_LEAVES,         // 橡树树叶
                Items.SPRUCE_LEAVES,      // 云杉树叶
                Items.BIRCH_LEAVES,       // 白桦树叶
                Items.JUNGLE_LEAVES,      // 丛林树叶
                Items.ACACIA_LEAVES,      // 金合欢树叶
                Items.DARK_OAK_LEAVES,    // 深色橡树树叶
                Items.MANGROVE_LEAVES,    // 红树树叶
                Items.CHERRY_LEAVES,      // 樱花树叶
                Items.AZALEA_LEAVES,      // 杜鹃树叶
                Items.FLOWERING_AZALEA_LEAVES
        ));
        ore.put("turtle",List.of(
                Items.TURTLE_EGG,
                Items.SCUTE,
                Items.SEAGRASS
        ));
        ore.put("basalt",List.of(
                Items.BASALT
        ));
        ore.put("cow",List.of(
                Items.COOKED_BEEF,
                Items.LEATHER
        ));
        ore.put("wither",List.of(
                Items.NETHER_STAR
        ));
        ore.put("witherskel",List.of(
                Items.COAL,
                Items.BONE,
                Items.WITHER_SKELETON_SKULL
        ));
        ore.put("witherrose",List.of(
                Items.WITHER_ROSE,
                Items.ENDER_PEARL
        ));
        ore.put("sheep",List.of(
                Items.WHITE_WOOL,      // 白色羊毛
                Items.LIGHT_GRAY_WOOL, // 浅灰色羊毛
                Items.GRAY_WOOL,       // 灰色羊毛
                Items.BLACK_WOOL,      // 黑色羊毛
                Items.BROWN_WOOL,      // 棕色羊毛
                Items.RED_WOOL,        // 红色羊毛
                Items.ORANGE_WOOL,     // 橙色羊毛
                Items.YELLOW_WOOL,     // 黄色羊毛
                Items.LIME_WOOL,       // 黄绿色羊毛
                Items.GREEN_WOOL,      // 绿色羊毛
                Items.CYAN_WOOL,       // 青色羊毛
                Items.LIGHT_BLUE_WOOL, // 淡蓝色羊毛
                Items.BLUE_WOOL,       // 蓝色羊毛
                Items.PURPLE_WOOL,     // 紫色羊毛
                Items.MAGENTA_WOOL,    // 品红色羊毛
                Items.PINK_WOOL        // 粉红色羊毛
        ));
        ore.put("witch",List.of(
                Items.GLASS_BOTTLE,
                Items.GLOWSTONE_DUST,
                Items.GUNPOWDER,
                Items.REDSTONE,
                Items.SPIDER_EYE,
                Items.SUGAR,
                Items.STICK
        ));
        ore.put("obsidian",List.of(
                Items.OBSIDIAN
        ));
        ore.put("squid",List.of(
                Items.INK_SAC,
                Items.GLOW_INK_SAC
        ));
        ore.put("dripstone",List.of(
                Items.POINTED_DRIPSTONE
        ));
        ore.put("concrete",List.of(
                Items.WHITE_CONCRETE,      // 白色混凝土
                Items.LIGHT_GRAY_CONCRETE, // 浅灰色混凝土
                Items.GRAY_CONCRETE,       // 灰色混凝土
                Items.BLACK_CONCRETE,      // 黑色混凝土
                Items.BROWN_CONCRETE,      // 棕色混凝土
                Items.RED_CONCRETE,        // 红色混凝土
                Items.ORANGE_CONCRETE,     // 橙色混凝土
                Items.YELLOW_CONCRETE,     // 黄色混凝土
                Items.LIME_CONCRETE,       // 黄绿色混凝土
                Items.GREEN_CONCRETE,      // 绿色混凝土
                Items.CYAN_CONCRETE,       // 青色混凝土
                Items.LIGHT_BLUE_CONCRETE, // 淡蓝色混凝土
                Items.BLUE_CONCRETE,       // 蓝色混凝土
                Items.PURPLE_CONCRETE,     // 紫色混凝土
                Items.MAGENTA_CONCRETE,    // 品红色混凝土
                Items.PINK_CONCRETE        // 粉红色混凝土
        ));
        ore.put("allay", List.of(
                Items.ALLAY_SPAWN_EGG
        ));
        ore.put("bat", List.of(
                Items.BAT_SPAWN_EGG
        ));
        ore.put("bedrock", List.of(
                Items.BEDROCK,
                Items.TNT
        ));
        ore.put("brew", List.of(
                Items.POTION,
                Items.LINGERING_POTION,
                Items.SPLASH_POTION
        ));
        ore.put("carpet", List.of(
                Items.WHITE_CARPET,
                Items.ORANGE_CARPET,
                Items.MAGENTA_CARPET,
                Items.LIGHT_BLUE_CARPET,
                Items.YELLOW_CARPET,
                Items.LIME_CARPET,
                Items.PINK_CARPET,
                Items.GRAY_CARPET,
                Items.LIGHT_GRAY_CARPET,
                Items.CYAN_CARPET,
                Items.PURPLE_CARPET,
                Items.BLUE_CARPET,
                Items.BROWN_CARPET,
                Items.GREEN_CARPET,
                Items.RED_CARPET,
                Items.BLACK_CARPET
        ));

        ore.put("cat", List.of(
                Items.CAT_SPAWN_EGG
        ));
        ore.put("classify", List.of(
                Items.REDSTONE,
                Items.HOPPER
        ));
        ore.put("coral", List.of(
                Items.TUBE_CORAL_FAN,
                Items.BRAIN_CORAL_FAN,
                Items.BUBBLE_CORAL_FAN,
                Items.FIRE_CORAL_FAN,
                Items.HORN_CORAL_FAN,
                Items.TUBE_CORAL,
                Items.BRAIN_CORAL,
                Items.BUBBLE_CORAL,
                Items.FIRE_CORAL,
                Items.HORN_CORAL,
                Items.SEAGRASS
        ));

        ore.put("disk", List.of(
                Items.MUSIC_DISC_13,
                Items.MUSIC_DISC_CAT,
                Items.MUSIC_DISC_BLOCKS,
                Items.MUSIC_DISC_CHIRP,
                Items.MUSIC_DISC_FAR,
                Items.MUSIC_DISC_MALL,
                Items.MUSIC_DISC_MELLOHI,
                Items.MUSIC_DISC_STAL,
                Items.MUSIC_DISC_STRAD,
                Items.MUSIC_DISC_WARD,
                Items.MUSIC_DISC_WAIT,
                Items.MUSIC_DISC_11
        ));

        ore.put("drainer", List.of(
                Items.WATER_BUCKET,
                Items.LAVA_BUCKET
        ));
        ore.put("endstone", List.of(
                Items.END_STONE
        ));
        ore.put("fish", List.of(
                Items.COD,
                Items.SALMON,
                Items.TROPICAL_FISH
        ));
        ore.put("fishing", List.of(
                Items.BOW,
                Items.ENCHANTED_BOOK,
                Items.FISHING_ROD,
                Items.NAME_TAG,
                Items.NAUTILUS_SHELL,
                Items.SADDLE,
                Items.COD,
                Items.SALMON,
                Items.PUFFERFISH,
                Items.TROPICAL_FISH,
                Items.BOWL,
                Items.LEATHER,
                Items.LEATHER_BOOTS,
                Items.ROTTEN_FLESH,
                Items.STICK,
                Items.STRING,
                Items.GLASS_BOTTLE,
                Items.BONE,
                Items.INK_SAC,
                Items.TRIPWIRE_HOOK
        ));

        ore.put("goat", List.of(
                Items.GOAT_HORN
        ));
        ore.put("head", List.of(
                Items.CREEPER_HEAD,
                Items.ZOMBIE_HEAD,
                Items.SKELETON_SKULL,
                Items.WITHER_SKELETON_SKULL,
                Items.PIGLIN_HEAD
        ));
        ore.put("lantern", List.of(
                Items.JACK_O_LANTERN
        ));
        ore.put("lava", List.of(
                Items.LAVA_BUCKET
        ));
        ore.put("lichen", List.of(
                Items.GLOW_LICHEN
        ));
        ore.put("load", List.of(
                Items.MINECART
        ));
        ore.put("mooshroom", List.of(
                Items.BEEF,
                Items.COOKED_BEEF,
                Items.LEATHER
        ));

        ore.put("nylium", List.of(
                Items.CRIMSON_NYLIUM,
                Items.WARPED_NYLIUM
        ));
        ore.put("obsidian2", List.of(
                Items.OBSIDIAN
        ));
        ore.put("pack", List.of(
                Items.SHULKER_BOX
        ));
        ore.put("phantom", List.of(
                Items.PHANTOM_MEMBRANE
        ));
        ore.put("pig", List.of(
                Items.PORKCHOP,
                Items.COOKED_PORKCHOP
        ));
        ore.put("smelt", List.of(
                Items.LAVA_BUCKET,
                Items.SHULKER_BOX
        ));
        ore.put("spider", List.of(
                Items.STRING,
                Items.SPIDER_EYE
        ));
        ore.put("stone2", List.of(
                Items.STONE
        ));
        ore.put("stray", List.of(
                Items.TIPPED_ARROW,
                Items.BONE,
                Items.ARROW
        ));
        ore.put("strip", List.of(
                Items.COBBLED_DEEPSLATE,
                Items.DIRT,
                // 所有去皮原木
                Items.STRIPPED_OAK_LOG,
                Items.STRIPPED_SPRUCE_LOG,
                Items.STRIPPED_BIRCH_LOG,
                Items.STRIPPED_JUNGLE_LOG,
                Items.STRIPPED_ACACIA_LOG,
                Items.STRIPPED_DARK_OAK_LOG,
                Items.STRIPPED_MANGROVE_LOG,
                Items.STRIPPED_CHERRY_LOG,
                Items.STRIPPED_BAMBOO_BLOCK,
                Items.STRIPPED_CRIMSON_STEM,
                Items.STRIPPED_WARPED_STEM
        ));

        ore.put("tbm", List.of(
                Items.COBBLESTONE,
                Items.DEEPSLATE,
                Items.NETHERRACK,
                Items.COAL,
                Items.RAW_COPPER,
                Items.RAW_IRON,
                Items.RAW_GOLD,
                Items.LAPIS_LAZULI,
                Items.REDSTONE,
                Items.DIAMOND,
                Items.QUARTZ
        ));
        ore.put("tnt", List.of(
                Items.TNT,
                Items.DEAD_TUBE_CORAL_FAN,
                Items.DEAD_BRAIN_CORAL_FAN,
                Items.DEAD_BUBBLE_CORAL_FAN,
                Items.DEAD_FIRE_CORAL_FAN,
                Items.DEAD_HORN_CORAL_FAN
        ));
        ore.put("tnt3d", List.of(
                Items.TNT,
                Items.DEAD_TUBE_CORAL_FAN,
                Items.DEAD_BRAIN_CORAL_FAN,
                Items.DEAD_BUBBLE_CORAL_FAN,
                Items.DEAD_FIRE_CORAL_FAN,
                Items.DEAD_HORN_CORAL_FAN
        ));
        ore.put("unpack", List.of(
                Items.SHULKER_BOX
        ));
        ore.put("vine", List.of(
                Items.VINE
        ));
        ore.put("warden", List.of(
                Items.SCULK_CATALYST
        ));
        ore.put("worldeater", List.of(
                Items.TNT,
                Items.SLIME_BLOCK,
                Items.ANVIL,
                Items.BEDROCK
        ));

        return ore;
    }
    public Map<String, Map<Item,Item>> OutputfRecipy() {
        Map<String, Map<Item, Item>> ore = new HashMap<>();
        Map<Item, Item> mp = new HashMap<>();
        mp.put(Items.COBBLESTONE,Items.STONE);
        ore.put("刷石机", mp);
        return ore;
    }
}
