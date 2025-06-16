package com.automationera.emi.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputRecipe {
    public Map<String, List<Item>> OutputdRecipy() {
        Map<String, List<Item>> ore = new HashMap<>();
        ore.put("刷石机", List.of(Items.COBBLESTONE));
        ore.put("树场", List.of(
                Items.OAK_LOG,
                Items.SPRUCE_LOG,
                Items.BIRCH_LOG,
                Items.JUNGLE_LOG,
                Items.ACACIA_LOG,
                Items.DARK_OAK_LOG,
                Items.MANGROVE_LOG,
                Items.CHERRY_LOG));
        ore.put("刷铁机",List.of(
                Items.IRON_INGOT,
                Items.POPPY
        ));
        ore.put("刷怪塔",List.of(
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
        ore.put("自动农场",List.of(
                Items.WHEAT,
                Items.WHEAT_SEEDS,
                Items.CARROT,
                Items.POTATO,
                Items.BEETROOT,
                Items.BEETROOT_SEEDS
        ));
        ore.put("刷沙机",List.of(
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
        ore.put("刷冰机",List.of(Items.ICE));
        ore.put("铁轨机",List.of(
                Items.RAIL,
                Items.ACTIVATOR_RAIL,
                Items.DETECTOR_RAIL,
                Items.POWERED_RAIL
        ));
        ore.put("骨粉机",List.of(
                Items.BONE_MEAL,
                Items.MOSS_BLOCK,
                Items.MOSS_CARPET,
                Items.AZALEA,
                Items.FLOWERING_AZALEA
        ));
        ore.put("甘蔗机",List.of(Items.SUGAR_CANE));
        ore.put("地狱疣农场",List.of(Items.NETHER_WART));
        ore.put("竹子农场",List.of(Items.NETHER_WART));
        ore.put("疣猪塔",List.of(
                Items.COOKED_PORKCHOP,
                Items.LEATHER));
        ore.put("史莱姆农场",List.of(Items.SLIME_BALL));
        ore.put("小黑塔",List.of(Items.ENDER_PEARL));
        ore.put("猪人塔",List.of(
                Items.GOLD_INGOT,
                Items.GOLD_NUGGET,
                Items.ROTTEN_FLESH
        ));
        return ore;
    }
}
