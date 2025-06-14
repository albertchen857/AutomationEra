package com.automationera.emi.display;

import com.automationera.emi.emiPlugin;
import com.automationera.emi.recipe.MachineRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MachineEmiRecipe implements EmiRecipe {
    private final MachineRecipe recipe;
    private final EmiStack input;
    private final EmiStack output;
    private final Identifier CACTUS_TEXTURE = new Identifier("automationera", "textures/gui/cactusfarm_structure.png");

    public MachineEmiRecipe(MachineRecipe recipe) {
        this.recipe = recipe;
        this.input = EmiStack.of(recipe.input);
        this.output = EmiStack.of(recipe.output);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return new EmiRecipeCategory(emiPlugin.MACHINE_CATEGORY_ID, EmiStack.of(Items.DISPENSER));
    }

    @Override
    public @Nullable Identifier getId() {
        return null;
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
        return 80;
    }

    @Override
    public void addWidgets(dev.emi.emi.api.widget.WidgetHolder widgets) {
        // 左上角：机器名字
        widgets.addText(Text.literal("仙人掌农场"), 5, 5, 0x404040, false);

        // 左中：结构图（使用图片）
        widgets.addTexture(CACTUS_TEXTURE, 5, 20, 60, 40, 0, 0); // 自定义结构图尺寸

        // 中间箭头（用内置箭头或图标）
        widgets.addTexture(new Identifier("textures/gui/arrow.png"), 90, 32, 24, 17, 0, 0);

        // 右上角：输入
        widgets.addSlot(input, 130, 20);

        // 右下角：输出
        widgets.addSlot(output, 130, 50).recipeContext(this);
    }

    public MachineRecipe getRecipe() {
        return recipe;
    }
}

