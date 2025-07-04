package com.automationera;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputRecipe {

    public Map<String, List<Item>> OutputTemplate() {
        Map<String, List<Item>> Template = new HashMap<>();
        Template.put("WOOL", List.of(
                Items.WHITE_WOOL, Items.BROWN_WOOL, Items.BLACK_WOOL, Items.GRAY_WOOL,
                Items.LIGHT_GRAY_WOOL, Items.RED_WOOL, Items.ORANGE_WOOL, Items.YELLOW_WOOL,
                Items.LIME_WOOL, Items.GREEN_WOOL, Items.CYAN_WOOL, Items.LIGHT_BLUE_WOOL,
                Items.BLUE_WOOL, Items.PURPLE_WOOL, Items.MAGENTA_WOOL, Items.PINK_WOOL
        ));
        Template.put("DYE", List.of(
                Items.WHITE_DYE, Items.GRAY_DYE, Items.BLACK_DYE, Items.LIGHT_BLUE_DYE, Items.LIME_DYE,
                Items.YELLOW_DYE, Items.LIGHT_GRAY_DYE, Items.ORANGE_DYE, Items.RED_DYE, Items.PINK_DYE,
                Items.BROWN_DYE, Items.PURPLE_DYE, Items.BLUE_DYE, Items.GREEN_DYE, Items.MAGENTA_DYE, Items.CYAN_DYE
        ));
        Template.put("CARPET", List.of(
                Items.WHITE_CARPET, Items.LIGHT_GRAY_CARPET, Items.GRAY_CARPET, Items.BLACK_CARPET, Items.BROWN_CARPET,
                Items.RED_CARPET, Items.ORANGE_CARPET, Items.YELLOW_CARPET, Items.LIME_CARPET, Items.GREEN_CARPET,
                Items.CYAN_CARPET, Items.LIGHT_BLUE_CARPET, Items.BLUE_CARPET, Items.PURPLE_CARPET, Items.MAGENTA_CARPET, Items.PINK_CARPET
        ));
        Template.put("BED", List.of(
                Items.WHITE_BED, Items.LIGHT_GRAY_BED, Items.GRAY_BED, Items.BLACK_BED, Items.BROWN_BED, Items.RED_BED,
                Items.ORANGE_BED, Items.YELLOW_BED, Items.LIME_BED, Items.GREEN_BED,
                Items.CYAN_BED, Items.LIGHT_BLUE_BED, Items.BLUE_BED, Items.PURPLE_BED, Items.MAGENTA_BED, Items.PINK_BED
        ));
        Template.put("BANNER", List.of(
                Items.WHITE_BANNER, Items.LIGHT_GRAY_BANNER, Items.GRAY_BANNER, Items.BLACK_BANNER, Items.BROWN_BANNER,
                Items.RED_BANNER, Items.ORANGE_BANNER, Items.YELLOW_BANNER, Items.LIME_BANNER, Items.GREEN_BANNER,
                Items.CYAN_BANNER, Items.LIGHT_BLUE_BANNER, Items.BLUE_BANNER, Items.PURPLE_BANNER, Items.MAGENTA_BANNER, Items.PINK_BANNER
        ));
        Template.put("DIAMOND_ARMOR", List.of(
                Items.DIAMOND_HELMET,
                Items.DIAMOND_CHESTPLATE,
                Items.DIAMOND_LEGGINGS,
                Items.DIAMOND_BOOTS));
        Template.put("TERRACOTTA", List.of(
                Items.WHITE_TERRACOTTA, Items.LIGHT_GRAY_TERRACOTTA, Items.GRAY_TERRACOTTA, Items.BLACK_TERRACOTTA,
                Items.BROWN_TERRACOTTA, Items.RED_TERRACOTTA, Items.ORANGE_TERRACOTTA, Items.YELLOW_TERRACOTTA, Items.LIME_TERRACOTTA, Items.GREEN_TERRACOTTA, Items.CYAN_TERRACOTTA
        ));
        Template.put("GLAZED_TERRACOTTA", List.of(
                Items.WHITE_GLAZED_TERRACOTTA, Items.LIGHT_BLUE_TERRACOTTA, Items.BLUE_TERRACOTTA, Items.PURPLE_TERRACOTTA, Items.MAGENTA_TERRACOTTA, Items.PINK_TERRACOTTA,
                Items.LIGHT_GRAY_GLAZED_TERRACOTTA, Items.GRAY_GLAZED_TERRACOTTA, Items.BLACK_GLAZED_TERRACOTTA,
                Items.BROWN_GLAZED_TERRACOTTA, Items.RED_GLAZED_TERRACOTTA, Items.ORANGE_GLAZED_TERRACOTTA, Items.YELLOW_GLAZED_TERRACOTTA, Items.LIME_GLAZED_TERRACOTTA, Items.GREEN_GLAZED_TERRACOTTA,
                Items.CYAN_GLAZED_TERRACOTTA, Items.LIGHT_BLUE_GLAZED_TERRACOTTA, Items.BLUE_GLAZED_TERRACOTTA, Items.PURPLE_GLAZED_TERRACOTTA, Items.MAGENTA_GLAZED_TERRACOTTA, Items.PINK_GLAZED_TERRACOTTA
        ));
        Template.put("BOAT", List.of(
                Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT, Items.MANGROVE_BOAT, Items.CHERRY_BOAT, Items.BAMBOO_RAFT
        ));
        Template.put("LEATHER_ARMOR", List.of(
                Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS
        ));
        Template.put("CORAL_BLOCK", List.of(
                Items.TUBE_CORAL_BLOCK, Items.BRAIN_CORAL_BLOCK, Items.BUBBLE_CORAL_BLOCK, Items.FIRE_CORAL_BLOCK, Items.HORN_CORAL_BLOCK
        ));
        Template.put("CORAL_FAN", List.of(
                Items.TUBE_CORAL_FAN, Items.BRAIN_CORAL_FAN, Items.BUBBLE_CORAL_FAN, Items.FIRE_CORAL_FAN, Items.HORN_CORAL_FAN
        ));
        Template.put("FLOWERS", List.of(
                Items.BLUE_ORCHID, Items.ALLIUM, Items.AZURE_BLUET, Items.RED_TULIP, Items.ORANGE_TULIP,
                Items.WHITE_TULIP, Items.PINK_TULIP, Items.OXEYE_DAISY, Items.CORNFLOWER, Items.LILY_OF_THE_VALLEY,
                Items.SUNFLOWER, Items.LILAC, Items.ROSE_BUSH, Items.PEONY, Items.DANDELION, Items.POPPY
        ));
        Template.put("CONCRETE_POWDER", List.of(
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
        Template.put("DEAD_CORAL_FAN", List.of(
                Items.DEAD_TUBE_CORAL_FAN,
                Items.DEAD_BRAIN_CORAL_FAN,
                Items.DEAD_BUBBLE_CORAL_FAN,
                Items.DEAD_FIRE_CORAL_FAN,
                Items.DEAD_HORN_CORAL_FAN
        ));
        Template.put("FISH", List.of(
                Items.COD,
                Items.SALMON,
                Items.PUFFERFISH,
                Items.TROPICAL_FISH
        ));
        Template.put("DIAMOND_TOOL", List.of(
                Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_PICKAXE, Items.DIAMOND_HOE
        ));
        Template.put("IRON_TOOL", List.of(
                Items.IRON_AXE, Items.IRON_SHOVEL, Items.IRON_PICKAXE, Items.IRON_HOE
        ));
        Template.put("STONE_TOOL", List.of(
                Items.STONE_AXE, Items.STONE_SHOVEL, Items.STONE_PICKAXE, Items.STONE_HOE
        ));
        Template.put("SEEDS", List.of(
                Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS
        ));
        Template.put("MOSS", List.of(
                Items.MOSS_BLOCK, Items.MOSS_CARPET, Items.SMALL_DRIPLEAF, Items.BIG_DRIPLEAF,
                Items.AZALEA, Items.FLOWERING_AZALEA,Items.SPORE_BLOSSOM
        ));
        Template.put("FISH_BUCKET", List.of(Items.PUFFERFISH_BUCKET, Items.TROPICAL_FISH_BUCKET));
        Template.put("W_CAVE", List.of(Items.SMALL_AMETHYST_BUD,Items.POINTED_DRIPSTONE,Items.DRIPSTONE_BLOCK));
        Template.put("W_FALLING", List.of(Items.SAND, Items.RED_SAND, Items.GRAVEL));
        Template.put("W_SAPLING", List.of(Items.CHERRY_SAPLING,Items.MANGROVE_PROPAGULE));
        Template.put("W_ICE", List.of(Items.PACKED_ICE, Items.BLUE_ICE));
        Template.put("SAPLING", List.of(
                Items.OAK_SAPLING,
                Items.SPRUCE_SAPLING,
                Items.BIRCH_SAPLING,
                Items.JUNGLE_SAPLING,
                Items.ACACIA_SAPLING,
                Items.DARK_OAK_SAPLING,
                Items.CHERRY_SAPLING      // 樱花树苗
        ));


        return Template;
    }
    public Map<String, List<List<Item>>> TradeRecipy() {
        Map<String, List<List<Item>>> trade = new HashMap<>();
        trade.put("armorer", List.of(
                List.of(
                        Items.COAL, Items.IRON_INGOT, Items.LAVA_BUCKET, Items.DIAMOND
                ),
                List.of(
                        Items.BELL,
                        Items.SHIELD
                )
        ));
        trade.put("butcher", List.of(
                List.of(
                        Items.CHICKEN,
                        Items.RABBIT,
                        Items.PORKCHOP,
                        Items.COAL,
                        Items.BEEF,
                        Items.MUTTON,
                        Items.DRIED_KELP_BLOCK,
                        Items.SWEET_BERRIES
                ),
                List.of(
                        Items.RABBIT_STEW, Items.COOKED_CHICKEN, Items.COOKED_PORKCHOP
                )
        ));
        trade.put("farmer", List.of(
                List.of(
                        Items.WHEAT, Items.POTATO, Items.CARROT, Items.BEETROOT,
                        Items.PUMPKIN, Items.MELON, Items.SWEET_BERRIES
                ),
                List.of(
                        Items.BREAD, Items.PUMPKIN_PIE, Items.APPLE, Items.COOKIE, Items.CAKE,
                        Items.SUSPICIOUS_STEW, Items.GOLDEN_CARROT, Items.GLISTERING_MELON_SLICE
                )
        ));
        trade.put("fisherman", List.of(
                List.of(
                        Items.STRING, Items.COAL, Items.COD, Items.SALMON, Items.TROPICAL_FISH,
                        Items.PUFFERFISH
                ),
                List.of(
                        Items.COD_BUCKET, Items.COOKED_COD, Items.CAMPFIRE, Items.COOKED_SALMON
                )// BOAT
        ));
        trade.put("fletcher", List.of(
                List.of(
                        Items.STICK, Items.FLINT, Items.STRING, Items.FEATHER, Items.GRAVEL, Items.TRIPWIRE_HOOK
                ),
                List.of(
                        Items.ARROW, Items.FLINT, Items.BOW, Items.CROSSBOW, Items.TIPPED_ARROW
                )
        ));

        trade.put("leatherworker", List.of(
                List.of(
                        Items.LEATHER, Items.RABBIT_HIDE, Items.FLINT, Items.SCUTE
                ),
                List.of(
                        Items.LEATHER_HORSE_ARMOR, Items.SADDLE
                ) // leather armor
        ));

        trade.put("librarian", List.of(
                List.of(
                        Items.PAPER, Items.BOOK, Items.INK_SAC, Items.WRITTEN_BOOK
                ),
                List.of(
                        Items.BOOKSHELF, Items.LANTERN, Items.GLASS, Items.ENCHANTED_BOOK, Items.COMPASS, Items.CLOCK, Items.NAME_TAG
                )
        ));

        trade.put("cleric", List.of(
                List.of(
                        Items.ROTTEN_FLESH, Items.GOLD_INGOT, Items.RABBIT_FOOT, Items.SCUTE, Items.GLASS_BOTTLE, Items.NETHER_WART
                ),
                List.of(
                        Items.REDSTONE, Items.LAPIS_LAZULI, Items.GLOWSTONE, Items.ENDER_PEARL, Items.EXPERIENCE_BOTTLE
                )
        ));

        trade.put("cartographer", List.of(
                List.of(
                        Items.PAPER, Items.GLASS_PANE, Items.COMPASS
                ),
                List.of(
                        Items.MAP, Items.ITEM_FRAME, Items.GLOBE_BANNER_PATTERN
                )// banner
        ));

        trade.put("shepherd", List.of(
                //in:WOOL DYE
                List.of(),
                List.of(//out carpet bed banner
                        Items.SHEARS, Items.PAINTING
                )
        ));

        trade.put("mason", List.of(
                List.of(
                        Items.CLAY_BALL, Items.STONE, Items.GRANITE, Items.ANDESITE, Items.DIORITE, Items.QUARTZ
                ),
                List.of(
                        Items.BRICK, Items.CHISELED_STONE_BRICKS, Items.DRIPSTONE_BLOCK, Items.POLISHED_ANDESITE, Items.POLISHED_DIORITE, Items.POLISHED_GRANITE, Items.QUARTZ_BLOCK, Items.QUARTZ_PILLAR
                )// out terracotta glazed_tara
        ));

        trade.put("toolsmith", List.of(
                List.of(
                        Items.COAL, Items.IRON_INGOT, Items.FLINT, Items.DIAMOND
                ), List.of(Items.BELL)
        ));

        trade.put("weaponsmith", List.of(
                List.of(
                        Items.COAL, Items.IRON_INGOT, Items.FLINT, Items.DIAMOND
                ),
                List.of(
                        Items.BELL, Items.DIAMOND_AXE, Items.DIAMOND_SWORD, Items.IRON_AXE, Items.IRON_SWORD
                )
        ));
        trade.put("wanderingtrader", List.of(
                // 收购项
                List.of(
                        Items.VINE, Items.MOSS_CARPET, Items.GLOW_BERRIES, Items.SMALL_DRIPLEAF,
                        Items.POINTED_DRIPSTONE, Items.ROOTED_DIRT, Items.SNIFFER_EGG
                ),
                List.of(

                        Items.COCOA_BEANS, Items.SUGAR_CANE, Items.BAMBOO, Items.CACTUS, Items.SEA_PICKLE,
                        Items.KELP, Items.VINE, Items.FERN,   Items.TORCHFLOWER, Items.PITCHER_PLANT, Items.PODZOL,
                        Items.ROOTED_DIRT, Items.GLOWSTONE, Items.SLIME_BLOCK, Items.SEA_LANTERN,
                        Items.CLAY, Items.MUD, Items.MUDDY_MANGROVE_ROOTS, Items.MOSSY_COBBLESTONE,
                         Items.POWDER_SNOW_BUCKET, Items.LEAD, Items.NAUTILUS_SHELL, Items.SNIFFER_EGG
                )//coarl block /coral flower dye
        ));
        return trade;
    }
    public Map<String, List<List<Item>>> OutputdRecipy() {
        Map<String, List<List<Item>>> ore = new HashMap<>();
        ore.put("stone", List.of(List.of(Items.COBBLESTONE),List.of()));
        ore.put("wood", List.of(List.of(
                Items.OAK_LOG,
                Items.SPRUCE_LOG,
                Items.BIRCH_LOG,
                Items.JUNGLE_LOG,
                Items.ACACIA_LOG,
                Items.DARK_OAK_LOG,
                Items.MANGROVE_LOG,
                Items.CHERRY_LOG,
                Items.STICK,
                Items.APPLE), List.of(Items.BONE_MEAL)));
        ore.put("iron",List.of(List.of(
                Items.IRON_INGOT,
                Items.POPPY
        ), List.of(Items.IRON_GOLEM_SPAWN_EGG,Items.VILLAGER_SPAWN_EGG)));
        ore.put("mob",List.of(List.of(
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
        ), List.of(Items.ZOMBIE_SPAWN_EGG,Items.CREEPER_SPAWN_EGG,Items.SKELETON_SPAWN_EGG,Items.SPIDER_SPAWN_EGG)));
        ore.put("farm",List.of(List.of(
                Items.WHEAT,
                Items.WHEAT_SEEDS,
                Items.CARROT,
                Items.POTATO,
                Items.BEETROOT,
                Items.BEETROOT_SEEDS
        ), List.of(Items.VILLAGER_SPAWN_EGG)));
        ore.put("sand",List.of(List.of(
                Items.SAND,
                Items.RED_SAND,
                Items.GRAVEL,
                Items.DRAGON_EGG,
                Items.ANVIL
        ), List.of(Items.END_PORTAL_FRAME)));
        ore.put("ice",List.of(List.of(Items.ICE), List.of()));
        ore.put("rail",List.of(List.of(
                Items.RAIL,
                Items.ACTIVATOR_RAIL,
                Items.DETECTOR_RAIL,
                Items.POWERED_RAIL
        ), List.of()));
        ore.put("bonemeal",List.of(List.of(
                Items.BONE_MEAL,
                Items.MOSS_BLOCK,
                Items.MOSS_CARPET,
                Items.AZALEA,
                Items.FLOWERING_AZALEA
        ), List.of(Items.BONE_MEAL)));
        ore.put("sugarcane",List.of(List.of(Items.SUGAR_CANE), List.of()));
        ore.put("wart",List.of(List.of(Items.NETHER_WART), List.of()));
        ore.put("bamboo",List.of(List.of(Items.BAMBOO), List.of()));
        ore.put("pork",List.of(List.of(
                Items.COOKED_PORKCHOP,
                Items.LEATHER), List.of(Items.HOGLIN_SPAWN_EGG)));
        ore.put("slime",List.of(List.of(Items.SLIME_BALL), List.of(Items.SLIME_SPAWN_EGG)));
        ore.put("enderman",List.of(List.of(Items.ENDER_PEARL), List.of(Items.ENDERMAN_SPAWN_EGG,Items.ENDERMITE_SPAWN_EGG)));
        ore.put("pigman",List.of(List.of(
                Items.GOLD_INGOT,
                Items.GOLD_NUGGET,
                Items.ROTTEN_FLESH
        ), List.of(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG)));
        ore.put("cactus",List.of(List.of(Items.CACTUS), List.of()));
        ore.put("melon",List.of(List.of(
                Items.PUMPKIN,
                Items.MELON,
                Items.MELON_SLICE
        ), List.of()));
        ore.put("berry",List.of(List.of(
                Items.SWEET_BERRIES
        ), List.of(Items.FOX_SPAWN_EGG)));
        ore.put("blade",List.of(List.of(
                Items.BLAZE_ROD
        ), List.of(Items.BLAZE_SPAWN_EGG)));
        ore.put("chicken",List.of(List.of(
                Items.COOKED_CHICKEN,
                Items.EGG,
                Items.FEATHER
        ), List.of(Items.CHICKEN_SPAWN_EGG)));
        ore.put("chorus",List.of(List.of(
                Items.CHORUS_FRUIT,
                Items.CHORUS_FLOWER
        ), List.of()));
        ore.put("clay",List.of(List.of(
                Items.DIRT,
                Items.CLAY,
                Items.MUD,
                Items.OAK_LOG,
                Items.BONE_MEAL
        ), List.of(Items.BONE_MEAL)));
        ore.put("copper",List.of(List.of(
                Items.EXPOSED_COPPER,
                Items.WEATHERED_COPPER,
                Items.OXIDIZED_COPPER
        ), List.of(Items.COPPER_BLOCK)));
        ore.put("creeper",List.of(List.of(
                Items.GUNPOWDER
        ), List.of(Items.CREEPER_SPAWN_EGG)));
        ore.put("cristal",List.of(List.of(
                Items.AMETHYST_SHARD
        ), List.of()));
        ore.put("drone",List.of(List.of(
                Items.ROTTEN_FLESH,
                Items.COPPER_INGOT,
                Items.TRIDENT
        ), List.of(Items.DROWNED_SPAWN_EGG)));
        ore.put("flower",List.of(List.of(
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
        ), List.of(Items.BONE_MEAL)));
        ore.put("froglight",List.of(List.of(
                Items.OCHRE_FROGLIGHT,
                Items.VERDANT_FROGLIGHT,
                Items.PEARLESCENT_FROGLIGHT
        ), List.of(Items.MAGMA_CUBE_SPAWN_EGG,Items.FROG_SPAWN_EGG)));
        ore.put("ghast",List.of(List.of(
                Items.GHAST_TEAR,
                Items.GUNPOWDER
        ), List.of(Items.GHAST_SPAWN_EGG)));
        ore.put("honey",List.of(List.of(
                Items.HONEY_BOTTLE,
                Items.HONEYCOMB
        ), List.of(Items.BEE_SPAWN_EGG)));
        ore.put("hook",List.of(List.of(
                Items.TRIPWIRE_HOOK
        ), List.of()));
        ore.put("kelp",List.of(List.of(
                Items.KELP
        ), List.of()));
        ore.put("magmacube",List.of(List.of(
                Items.MAGMA_CREAM
        ), List.of(Items.MAGMA_CUBE_SPAWN_EGG)));
        ore.put("marine",List.of(List.of(
                Items.PRISMARINE_SHARD,
                Items.PRISMARINE_CRYSTALS,
                Items.COD,
                Items.SALMON,
                Items.COOKED_COD,
                Items.COOKED_SALMON,
                Items.PUFFERFISH,
                Items.TROPICAL_FISH
        ), List.of(Items.GUARDIAN_SPAWN_EGG,Items.ELDER_GUARDIAN_SPAWN_EGG)));
        ore.put("mushroom",List.of(List.of(
                Items.BROWN_MUSHROOM,
                Items.RED_MUSHROOM
        ), List.of()));
        ore.put("mushroom2",List.of(List.of(
                Items.BROWN_MUSHROOM,
                Items.RED_MUSHROOM,
                Items.RED_MUSHROOM_BLOCK,
                Items.BROWN_MUSHROOM_BLOCK,
                Items.MUSHROOM_STEM
        ), List.of()));
        ore.put("pickle",List.of(List.of(
                Items.SEA_PICKLE
        ), List.of(Items.BONE_MEAL)));
        ore.put("piglin",List.of(List.of(
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
        ), List.of(Items.PIGLIN_SPAWN_EGG)));
        ore.put("rabbit",List.of(List.of(
                Items.COOKED_RABBIT,
                Items.RABBIT_HIDE,
                Items.RABBIT_FOOT
        ), List.of(Items.RABBIT_SPAWN_EGG)));
        ore.put("raid",List.of(List.of(
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
        ), List.of(Items.PILLAGER_SPAWN_EGG,Items.VINDICATOR_SPAWN_EGG,Items.WITCH_SPAWN_EGG,Items.EVOKER_SPAWN_EGG)));
        ore.put("ripend",List.of(List.of(
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
        ), List.of(Items.BONE_MEAL)));
        ore.put("shulker",List.of(List.of(
                Items.SHULKER_SHELL
        ), List.of(Items.SHULKER_SPAWN_EGG)));
        ore.put("sniffer",List.of(List.of(
                Items.TORCHFLOWER_SEEDS,
                Items.PITCHER_POD,
                Items.SNIFFER_EGG
        ), List.of(Items.SNIFFER_SPAWN_EGG)));
        ore.put("snow",List.of(List.of(
                Items.SNOWBALL
        ), List.of(Items.SNOW_GOLEM_SPAWN_EGG)));
        ore.put("string",List.of(List.of(
                Items.STRING
        ), List.of(Items.SHEARS)));
        ore.put("wheat",List.of(List.of(
                Items.WHEAT,
                Items.WHEAT_SEEDS,
                Items.BEETROOT,
                Items.BEETROOT_SEEDS
        ), List.of(Items.VILLAGER_SPAWN_EGG)));
        ore.put("leaf",List.of(List.of(
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
        ), List.of(Items.SHEARS,Items.BONE_MEAL)));
        ore.put("turtle",List.of(List.of(
                Items.TURTLE_EGG,
                Items.SCUTE,
                Items.SEAGRASS
        ), List.of(Items.TURTLE_SPAWN_EGG,Items.SEAGRASS)));
        ore.put("basalt",List.of(List.of(
                Items.BASALT
        ), List.of()));
        ore.put("cow",List.of(List.of(
                Items.COOKED_BEEF,
                Items.LEATHER
        ), List.of(Items.COW_SPAWN_EGG)));
        ore.put("wither",List.of(List.of(
                Items.NETHER_STAR
        ), List.of(Items.SOUL_SAND)));
        ore.put("witherskel",List.of(List.of(
                Items.COAL,
                Items.BONE,
                Items.WITHER_SKELETON_SKULL
        ), List.of(Items.WITHER_SKELETON_SPAWN_EGG)));
        ore.put("witherrose",List.of(List.of(
                Items.WITHER_ROSE,
                Items.ENDER_PEARL
        ), List.of()));
        ore.put("sheep",List.of(List.of(
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
        ), List.of(Items.SHEEP_SPAWN_EGG)));
        ore.put("witch",List.of(List.of(
                Items.GLASS_BOTTLE,
                Items.GLOWSTONE_DUST,
                Items.GUNPOWDER,
                Items.REDSTONE,
                Items.SPIDER_EYE,
                Items.SUGAR,
                Items.STICK
        ), List.of(Items.WITCH_SPAWN_EGG)));
        ore.put("obsidian",List.of(List.of(
                Items.OBSIDIAN
        ), List.of()));
        ore.put("squid",List.of(List.of(
                Items.INK_SAC,
                Items.GLOW_INK_SAC
        ), List.of(Items.SQUID_SPAWN_EGG,Items.GLOW_SQUID_SPAWN_EGG)));
        ore.put("dripstone",List.of(List.of(
                Items.POINTED_DRIPSTONE
        ), List.of()));
        ore.put("concrete",List.of(List.of(
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
        ), List.of(Items.WATER_BUCKET)));
        ore.put("allay", List.of(List.of(), List.of(Items.ALLAY_SPAWN_EGG)));
        ore.put("bat", List.of(List.of(
                Items.BAT_SPAWN_EGG
        ), List.of()));
        ore.put("bedrock", List.of(List.of(
                Items.BEDROCK
        ), List.of(Items.TNT)));
        ore.put("brew", List.of(List.of(
                Items.POTION,
                Items.LINGERING_POTION,
                Items.SPLASH_POTION
        ), List.of(Items.POTION,Items.BLAZE_POWDER)));
        ore.put("carpet", List.of(List.of(
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
        ), List.of()));

        ore.put("cat", List.of(List.of(
                Items.CAT_SPAWN_EGG
        ), List.of(Items.VILLAGER_SPAWN_EGG,Items.RABBIT_SPAWN_EGG)));
        ore.put("classify", List.of(List.of(), List.of(Items.REDSTONE,
                Items.HOPPER)));
        ore.put("coral", List.of(List.of(
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
        ), List.of(Items.BONE_MEAL)));

        ore.put("disk", List.of(List.of(
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
        ), List.of(Items.SKELETON_SPAWN_EGG)));

        ore.put("drainer", List.of(List.of(
                Items.WATER_BUCKET,
                Items.LAVA_BUCKET
        ), List.of()));
        ore.put("endstone", List.of(List.of(
                Items.END_STONE
        ), List.of(Items.END_CRYSTAL)));
        ore.put("fish", List.of(List.of(
                Items.COD,
                Items.SALMON,
                Items.TROPICAL_FISH
        ), List.of()));
        ore.put("fishing", List.of(List.of(
                Items.BOW,
                Items.ENCHANTED_BOOK,
                Items.FISHING_ROD,
                Items.NAME_TAG,
                Items.NAUTILUS_SHELL,
                Items.SADDLE,
                Items.BOWL,
                Items.LEATHER,
                Items.ROTTEN_FLESH,
                Items.STICK,
                Items.STRING,
                Items.GLASS_BOTTLE,
                Items.BONE,
                Items.INK_SAC,
                Items.TRIPWIRE_HOOK
        ), List.of(Items.FISHING_ROD)));

        ore.put("goat", List.of(List.of(
                Items.GOAT_HORN
        ), List.of(Items.GOAT_SPAWN_EGG)));
        ore.put("head", List.of(List.of(
                Items.CREEPER_HEAD,
                Items.ZOMBIE_HEAD,
                Items.SKELETON_SKULL,
                Items.WITHER_SKELETON_SKULL,
                Items.PIGLIN_HEAD
        ), List.of(Items.CREEPER_SPAWN_EGG)));
        ore.put("lantern", List.of(List.of(
                Items.JACK_O_LANTERN
        ), List.of(Items.SHEARS)));
        ore.put("lava", List.of(List.of(
                Items.LAVA_BUCKET
        ), List.of(Items.POINTED_DRIPSTONE)));
        ore.put("lichen", List.of(List.of(
                Items.GLOW_LICHEN
        ), List.of(Items.BONE_MEAL)));
        ore.put("load", List.of(List.of(), List.of(Items.MINECART)));
        ore.put("mooshroom", List.of(List.of(
                Items.BEEF,
                Items.COOKED_BEEF,
                Items.LEATHER
        ), List.of(Items.BONE_MEAL)));

        ore.put("nylium", List.of(List.of(
                Items.CRIMSON_NYLIUM,
                Items.WARPED_NYLIUM
        ), List.of(Items.BONE_MEAL)));
        ore.put("obsidian2", List.of(List.of(
                Items.OBSIDIAN
        ), List.of()));
        ore.put("pack", List.of(List.of(
                Items.SHULKER_BOX
        ), List.of()));
        ore.put("phantom", List.of(List.of(
                Items.PHANTOM_MEMBRANE
        ), List.of(Items.PHANTOM_SPAWN_EGG)));
        ore.put("pig", List.of(List.of(
                Items.PORKCHOP,
                Items.COOKED_PORKCHOP
        ), List.of(Items.PIG_SPAWN_EGG)));
        ore.put("smelt", List.of(List.of(), List.of(Items.LAVA_BUCKET,
                Items.SHULKER_BOX)));
        ore.put("spider", List.of(List.of(
                Items.STRING,
                Items.SPIDER_EYE
        ), List.of(Items.SPIDER_SPAWN_EGG)));
        ore.put("stone2", List.of(List.of(
                Items.STONE
        ), List.of()));
        ore.put("stray", List.of(List.of(
                Items.TIPPED_ARROW,
                Items.BONE,
                Items.ARROW
        ), List.of(Items.POWDER_SNOW_BUCKET)));
        ore.put("strip", List.of(List.of(
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
        ), List.of(Items.TNT)));

        ore.put("tbm", List.of(List.of(
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
        ), List.of(Items.TNT)));
        ore.put("tnt", List.of(List.of(
                Items.TNT
        ), List.of()));
        ore.put("tnt3d", List.of(List.of(
                Items.TNT
        ), List.of()));
        ore.put("unpack", List.of(List.of(), List.of(Items.SHULKER_BOX)));
        ore.put("vine", List.of(List.of(
                Items.VINE
        ), List.of(Items.SHEARS)));
        ore.put("warden", List.of(List.of(
                Items.SCULK_CATALYST
        ), List.of(Items.MINECART)));
        ore.put("worldeater", List.of(
                List.of(Items.TNT),
                List.of(Items.SLIME_BLOCK, Items.ANVIL, Items.BEDROCK)));
        ore.put("bedrock2", List.of(
                List.of(Items.BEDROCK),
                List.of(Items.PISTON)));
        ore.put("brew2", List.of(
                List.of(Items.POTION,Items.LINGERING_POTION,Items.SPLASH_POTION),
                List.of(Items.GLASS_BOTTLE,Items.BLAZE_POWDER)));
        ore.put("dirt2", List.of(
                List.of(Items.PODZOL,Items.DIRT,Items.SPRUCE_LOG),
                List.of(Items.BONE_MEAL)));
        ore.put("dirt3", List.of(
                List.of(Items.DIRT),
                List.of(Items.COARSE_DIRT)));
        ore.put("flower2", List.of(
                List.of(Items.SUNFLOWER,Items.ROSE_BUSH,Items.LILAC,Items.PEONY),
                List.of(Items.BONE_MEAL)));
        ore.put("furnace", List.of(
                List.of(Items.FURNACE),
                List.of(Items.COAL,Items.LAVA_BUCKET)));
        ore.put("furnace2", List.of(
                List.of(Items.FURNACE),
                List.of()));
        ore.put("melon2", List.of(
                List.of(Items.MELON,Items.PUMPKIN),
                List.of(Items.ENDERMAN_SPAWN_EGG,Items.ENDERMITE_SPAWN_EGG)));
        ore.put("mob2", List.of(List.of(
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
                Items.SUGAR
        ), List.of(Items.ZOMBIE_SPAWN_EGG,Items.CREEPER_SPAWN_EGG,Items.SKELETON_SPAWN_EGG,Items.SPIDER_SPAWN_EGG)));
        ore.put("mob3", List.of(List.of(
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
                Items.SUGAR
        ), List.of(Items.ZOMBIE_SPAWN_EGG,Items.CREEPER_SPAWN_EGG,Items.SKELETON_SPAWN_EGG,Items.SPIDER_SPAWN_EGG)));
        ore.put("mob4", List.of(List.of(
                Items.GUNPOWDER,
                Items.ARROW,
                Items.BONE,
                Items.ROTTEN_FLESH,
                Items.REDSTONE,
                Items.GLOWSTONE_DUST,
                Items.GLASS_BOTTLE,
                Items.SUGAR
        ), List.of(Items.ZOMBIE_SPAWN_EGG,Items.CREEPER_SPAWN_EGG,Items.SKELETON_SPAWN_EGG,Items.WITCH_SPAWN_EGG)));
        ore.put("mobswitch", List.of(
                List.of(Items.WARDEN_SPAWN_EGG),
                List.of(Items.SCULK_SHRIEKER)));
        ore.put("pearlcannon", List.of(
                List.of(Items.ENDER_PEARL),
                List.of(Items.TNT)));
        ore.put("snow2", List.of(
                List.of(Items.SNOWBALL),
                List.of(Items.SNOW_GOLEM_SPAWN_EGG,Items.TNT)));
        ore.put("sugarcane2", List.of(
                List.of(Items.SUGAR_CANE,Items.CACTUS,Items.BAMBOO,Items.POINTED_DRIPSTONE),
                List.of(Items.PISTON,Items.SLIME_BLOCK)));
        ore.put("suppression", List.of(
                List.of(Items.OBSIDIAN,Items.LIGHT),
                List.of(Items.POWERED_RAIL,Items.REDSTONE_LAMP,Items.REDSTONE)));
        ore.put("suppression2", List.of(
                List.of(Items.OBSIDIAN),
                List.of(Items.POWERED_RAIL,Items.REDSTONE)));
        ore.put("tntcannon", List.of(
                List.of(Items.TNT),
                List.of(Items.TNT,Items.REDSTONE)));
        ore.put("wood2", List.of(
                List.of(Items.OAK_LOG,
                        Items.SPRUCE_LOG,
                        Items.BIRCH_LOG,
                        Items.JUNGLE_LOG,
                        Items.ACACIA_LOG,
                        Items.DARK_OAK_LOG,
                        Items.MANGROVE_LOG,
                        Items.CHERRY_LOG,
                        Items.STICK,
                        Items.APPLE),
                List.of(Items.BONE_MEAL)));
        return ore;
    }
    public Map<String, List<Item>> TutorialRecipy() {
        Map<String, List<Item>> tut = new HashMap<>();
        tut.put("tut_logic", List.of(Items.REDSTONE));
        tut.put("tut_0tcircuit", List.of(Items.PISTON,Items.REDSTONE));
        tut.put("tut_bedrock", List.of(Items.BEDROCK,Items.ENDER_PEARL,Items.KNOWLEDGE_BOOK));
        tut.put("tut_bud", List.of(Items.SLIME_BLOCK,Items.PISTON,Items.REDSTONE));
        tut.put("tut_clock", List.of(Items.REDSTONE,Items.HOPPER));
        tut.put("tut_cud", List.of(Items.COMPARATOR,Items.REDSTONE));
        tut.put("tut_elevator", List.of(Items.WATER_BUCKET,Items.SOUL_SAND,Items.MAGMA_BLOCK));
        tut.put("tut_hfcircuit", List.of(Items.REDSTONE,Items.OBSERVER));
        tut.put("tut_performance", List.of(Items.REDSTONE,Items.KNOWLEDGE_BOOK));
        tut.put("tut_qc", List.of(Items.PISTON,Items.REDSTONE));
        tut.put("tut_railtransportation", List.of(Items.RAIL,Items.MINECART,Items.REDSTONE,Items.KNOWLEDGE_BOOK));
        tut.put("tut_itemtransportation", List.of(Items.KNOWLEDGE_BOOK,Items.REDSTONE,Items.HOPPER,Items.WATER_BUCKET,Items.DROPPER));
        tut.put("tut_random", List.of(Items.REDSTONE,Items.DISPENSER));
        tut.put("tut_tick", List.of(Items.KNOWLEDGE_BOOK,Items.REDSTONE));
        tut.put("tut_update", List.of(Items.KNOWLEDGE_BOOK));
        tut.put("tut_perimeter", List.of(Items.ANVIL,Items.BEDROCK,Items.TNT));
        tut.put("tut_railway", List.of(Items.RAIL,Items.MINECART,Items.KNOWLEDGE_BOOK));
        tut.put("tut_iceway", List.of(Items.KNOWLEDGE_BOOK,Items.OAK_BOAT,Items.BLUE_ICE));
        tut.put("tut_flying", List.of(Items.REDSTONE,Items.PISTON,Items.SLIME_BLOCK));
        tut.put("tut_unit", List.of(Items.KNOWLEDGE_BOOK));
        tut.put("tut_term", List.of(Items.KNOWLEDGE_BOOK));
        return tut;
    }
    public Map<String, List<List<String>>> OutputIng() {
        Map<String, List<List<String>>> Ing = new HashMap<>();
        //trade
        Ing.put("fisherman", List.of(
                List.of(),
                List.of("BOAT")// BOAT
        ));
        Ing.put("leatherworker", List.of(
                List.of(),
                List.of("LEATHER_ARMOR") // leather armor
        ));
        Ing.put("cartographer", List.of(
                List.of(),
                List.of("BANNER")// banner
        ));

        Ing.put("shepherd", List.of(
                List.of("WOOL","DYE"),
                List.of("CARPET","BED","BANNER")
        ));
        Ing.put("mason", List.of(
                List.of(),
                List.of("TERRACOTTA","GLAZED_TERRACOTTA")// out terracotta glazed_tara
        ));
        Ing.put("toolsmith", List.of(
                List.of(),
                List.of("DIAMOND_TOOL","IRON_TOOL","STONE_TOOL")
        ));
        Ing.put("wandering_trader", List.of(
                List.of("SEEDS","W_SAPLING"),
                List.of("CORAL_BLOCK","CORAL_FAN","FLOWER","DYE","SEEDS","FISH_BUCKET","W_CAVE","W_FALLING","W_SAPLING","W_ICE")
        ));
        //normal
        Ing.put("sand", List.of(
                List.of("CONCRETE_POWDER"),
                List.of()
        ));
        Ing.put("tnt", List.of(
                List.of("DEAD_CORAL_FAN"),
                List.of()
        ));
        Ing.put("tnt3d", List.of(
                List.of("DEAD_CORAL_FAN"),
                List.of()
        ));
        Ing.put("rail", List.of(
                List.of(),
                List.of("DEAD_CORAL_FAN")
        ));
        Ing.put("obsidian", List.of(
                List.of(),
                List.of("CARPET")
        ));
        Ing.put("fishing", List.of(
                List.of("FISH"),
                List.of()
        ));
        Ing.put("furnace2", List.of(
                List.of(),
                List.of("CARPET")));
        Ing.put("wood", List.of(
                List.of("SAPLING"),
                List.of()));
        Ing.put("wood2", List.of(
                List.of("SAPLING"),
                List.of()));
        return Ing;
    }
}
