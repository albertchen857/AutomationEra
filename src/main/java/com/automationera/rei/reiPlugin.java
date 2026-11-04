package com.automationera.rei;

import com.automationera.OutputRecipe;
import com.automationera.rei.category.MachineRecipeCategory;
import com.automationera.rei.category.TradeRecipeCategory;
import com.automationera.rei.category.TutorialRecipeCategory;
import com.automationera.rei.display.MachineReiRecipe;
import com.automationera.rei.display.TradeReiRecipe;
import com.automationera.rei.display.TutorialReiRecipe;
import com.automationera.rei.recipe.MachineRecipe;
import me.shedaniel.rei.api.client.ClientHelper;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class reiPlugin implements REIClientPlugin {
    public static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/REITest");
    // New Category Identifiers for REI (replace EmiRecipeCategory)
    public static final CategoryIdentifier<MachineReiRecipe> MACHINE_CATEGORY_ID =
            CategoryIdentifier.of("automationera", "machine");
    public static final CategoryIdentifier<TradeReiRecipe> TRADE_CATEGORY_ID =
            CategoryIdentifier.of("automationera", "trade");
    public static final CategoryIdentifier<TutorialReiRecipe> TUTORIAL_CATEGORY_ID =
            CategoryIdentifier.of("automationera", "tutorial");

    // Data maps from OutputRecipe (unchanged)
    public static final Map<String, List<List<Item>>> ore = new OutputRecipe().OutputdRecipy();
    public static final Map<String, List<List<Item>>> trade = new OutputRecipe().TradeRecipy();
    public static final Map<String, List<Item>> tut = new OutputRecipe().TutorialRecipy();

    public static final Identifier ICON_TEXTURE = Identifier.fromNamespaceAndPath("automationera", "icon.png");
    public static final Map<String, EntryStack<?>> ENTRY_BY_KEY = new HashMap<>();

    @Override
    public void registerCategories(CategoryRegistry registry) {
        // Register custom recipe categories
        registry.add(new MachineRecipeCategory());
        registry.add(new TradeRecipeCategory());
        registry.add(new TutorialRecipeCategory());
        // (Optional) link workstations or other category settings if needed
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        ReiLayout.clearCache();
        registry.add(new MachineReiRecipe(
                new MachineRecipe(Items.REDSTONE, Items.REDSTONE),
                "circuit",
                List.of(List.of(Items.REDSTONE), List.of(Items.REDSTONE_BLOCK))
        ));
        ENTRY_BY_KEY.put("circuit", EntryStacks.of(Items.REDSTONE));
        // Register Machine
        for (Map.Entry<String, List<List<Item>>> entry : ore.entrySet()) {
            String key = entry.getKey();
            List<Item> outputs = entry.getValue().get(0);
            List<Item> inputs = entry.getValue().get(1);
            if (!outputs.isEmpty()) {
                // Use a dummy MachineRecipe (AIR -> first output) to represent machine outputs
                registry.add(new MachineReiRecipe(new MachineRecipe(outputs.getFirst(), outputs.getFirst()), key, entry.getValue()));
                ENTRY_BY_KEY.put(key, EntryStacks.of(outputs.getFirst()));
            }
            // Note: The complementary input-focused machine recipe (MachineEmiRecipe in EMI) is omitted in REI for simplicity
        }
        // Register villager trade recipes
        for (Map.Entry<String, List<List<Item>>> entry : trade.entrySet()) {
            String key = entry.getKey();
            List<Item> offers = entry.getValue().get(0);
            List<Item> requirements = entry.getValue().get(1);
            if (!offers.isEmpty()) {
                // Villager offers (output) for emerald
                registry.add(new TradeReiRecipe(new MachineRecipe(Items.EMERALD, offers.getFirst()), key + "_in", entry.getValue()));
            }
            if (!requirements.isEmpty()) {
                // Villager accepts item for emerald (emerald as output)
                registry.add(new TradeReiRecipe(new MachineRecipe(requirements.getFirst(), Items.EMERALD), key + "_out", entry.getValue()));
            }
        }
        // Register tutorial entries
        for (Map.Entry<String, List<Item>> entry : tut.entrySet()) {
            String key = entry.getKey();
            List<Item> outputItems = entry.getValue();
            if (!outputItems.isEmpty()) {
                // Use first item as representative (input/output are the same dummy for tutorial)
                Item repr = outputItems.getFirst();
                registry.add(new TutorialReiRecipe(new MachineRecipe(repr, repr), key, outputItems));
            }
        }
        // 服务器没装 REI 时，补上本地缓存的配方
        ReiDisplayCache.addCachedDisplays(registry);
    }

    // Utility methods for texture Identifiers (for categories to use)
    public static Identifier getMachineTexture(String machineKey) {
        return Identifier.fromNamespaceAndPath("automationera", "textures/rei/machine/" + machineKey + ".png");
    }
    public static Identifier getVillagerTexture(String job) {
        return Identifier.fromNamespaceAndPath("automationera", "textures/rei/villager/" + job + ".png");
    }
    /** 教程图片：非中文语言时优先用带英文标注的 <key>_en.png（存在的话） */
    public static Identifier getTutorialTexture(String tutKey) {
        Identifier base = Identifier.fromNamespaceAndPath("automationera", "textures/rei/tutorial/" + tutKey + ".png");
        Minecraft client = Minecraft.getInstance();
        if (!client.getLanguageManager().getSelected().startsWith("zh")) {
            Identifier en = Identifier.fromNamespaceAndPath("automationera", "textures/rei/tutorial/" + tutKey + "_en.png");
            if (client.getResourceManager().getResource(en).isPresent()) return en;
        }
        return base;
    }
    /** 打开某个机器的 REI 页面（教程界面的 REI 按钮用） */
    public static void openMachinePage(String key) {
        EntryStack<?> entryStack = ENTRY_BY_KEY.get(key);
        if (entryStack == null) {
            LOGGER.warn("No EntryStack found for machine page: {}", key);
            return;
        }
        ClientHelper.getInstance().openView(
                ViewSearchBuilder.builder()
                        .addRecipesFor(entryStack)
                        .setPreferredOpenedCategory(MACHINE_CATEGORY_ID));
    }

    // Utility to open wiki URL (called from category widgets on button click)
    public static void openWiki(String url) {
        net.minecraft.util.Util.getPlatform().openUri(url);
    }
}
