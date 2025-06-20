package com.automationera.emi.display;

import com.automationera.emi.emiPlugin;
import com.automationera.emi.recipe.MachineRecipe;
import com.automationera.emi.recipe.OutputRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class MachineEmiRecipe implements EmiRecipe {
    private final MachineRecipe recipe;
    private final EmiStack input;
    private final EmiStack output;
    private final Map<String, List<Item> > ore = new OutputRecipe().OutputdRecipy();
    private final List<Item> allOutputs;

    public MachineEmiRecipe(MachineRecipe recipe, List<Item> allOutputs) {
        this.recipe = recipe;
        this.input = EmiStack.of(recipe.input);
        this.output = EmiStack.of(recipe.output);
        this.allOutputs = allOutputs;
    }

    public MachineEmiRecipe(MachineRecipe recipe) {
        this(recipe, null);
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
        if (allOutputs != null) {
            return allOutputs.stream().map(EmiStack::of).toList();
        }
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
            String id = entry.getKey();
            List<Item> items = entry.getValue();
            if (items.contains(recipe.input) || items.contains(recipe.output)) {
                switch (id) {
                    case "trade" -> {
                        return;
                    }
                }
                WidFarm(widgets, I18n.translate("emi.automationera."+ id +".title"), id, I18n.translate("emi.automationera."+id+".descr"), items);
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

