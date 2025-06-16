package com.automationera.emi.display;

import com.automationera.emi.category.MachineRecipeCategory;
import com.automationera.emi.emiPlugin;
import com.automationera.emi.recipe.MachineRecipe;
import com.automationera.emi.recipe.OutputRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MachineEmiRecipe implements EmiRecipe {
    private final MachineRecipe recipe;
    private final EmiStack input;
    private final EmiStack output;
    private final Map<String, List<Item> > ore = new OutputRecipe().OutputdRecipy();


    public MachineEmiRecipe(MachineRecipe recipe) {
        this.recipe = recipe;
        this.input = EmiStack.of(recipe.input);
        this.output = EmiStack.of(recipe.output);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return emiPlugin.MACHINE_CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        return new Identifier("automationera", "machine_recipe_" + recipe.hashCode());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return 130;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (Map.Entry<String, List<Item>> entry : ore.entrySet()) {
            String key = entry.getKey();
            List<Item> items = entry.getValue();
            if (items.contains(recipe.input) || items.contains(recipe.output)) {
                String id = "iron";
                switch (key) {
                    case "刷铁机" -> id = "iron";
                    case "刷石机" -> id = "stone";
                    case "树场" -> id = "wood";
                    case "刷怪塔" -> id = "mob";
                    case "自动农场" -> id = "farm";
                    case "刷沙机" -> id = "sand";
                    case "刷冰机" -> id = "ice";
                    case "铁轨机" -> id = "rail";
                    case "骨粉机" -> id = "bonemeal";
                    case "甘蔗机" -> id = "sugarcane";
                    case "地狱疣农场" -> id = "wart";
                    case "竹子农场" -> id = "bamboo";
                    case "疣猪塔" -> id = "pork";
                    case "史莱姆农场" -> id = "slime";
                    case "小黑塔" -> id = "enderman";
                    case "猪人塔" -> id = "pigman";
                    default -> id = "stone";
                }
                WidFarm(widgets, I18n.translate("emi.automationera."+id+".title"), id, I18n.translate("emi.automationera."+id+".descr"), items);
                break;
            }
        }
    }

    public MachineRecipe getRecipe() {
        return recipe;
    }

    public void WidFarm(WidgetHolder widgets, String name, String texture, String discription, List<Item> itemset){
        widgets.addText(Text.literal(name), 5, 5, 0x404040, false);
        widgets.addTexture(new Identifier("automationera", "textures/"+texture+".png"), 0, 30 , 96, 96, 50, 50,900,900,900,900);
        //widgets.addFillingArrow(100, 40, 10000);
        //widgets.addSlot(input, 80, 40);
        for (int i=0;i<itemset.size();i++){
            int sx = 86 + (i%4) * 18;
            int sy = 40 + (i/4) * 18;
            widgets.addSlot(EmiStack.of(itemset.get(i)), sx, sy).recipeContext(this);
        }
        widgets.addText(Text.literal(discription), 5, 20, 0x404040, false);
    }
}

