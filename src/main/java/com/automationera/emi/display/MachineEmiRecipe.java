package com.automationera.emi.display;

import com.automationera.emi.emiPlugin;
import com.automationera.emi.recipe.MachineRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MachineEmiRecipe implements EmiRecipe {
    private final MachineRecipe recipe;
    // 我们不使用 EmiIngredient 输入槽位，因为机器产物展示不需要输入
    private final EmiStack output;
    private final List<Item> allOutputs;
    private final String key; // 机器标识（用于定位纹理和本地化字符串）
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/emi/display/MachineEmiRecipe");

    /**
     * 构造一个机器配方，用于 EMI 显示。
     *
     * @param recipe   包含虚拟输入（例如 AIR）和代表性输出的 MachineRecipe 对象
     * @param key      机器的标识符（用于找到对应的纹理和本地化文本）
     * @param allOutputs 该机器能产出的所有 Item 列表
     */
    public MachineEmiRecipe(MachineRecipe recipe, String key, List<Item> allOutputs) {
        this.recipe = recipe;
        this.output = EmiStack.of(recipe.output);
        this.allOutputs = allOutputs;
        this.key = key;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return emiPlugin.MACHINE_CATEGORY;
    }

    @Override
    public Identifier getId() {
        // 使用机器标识来生成唯一配方 ID
        return new Identifier("automationera", "machine_recipe_" + key);
    }

    @Override
    public List<EmiIngredient> getInputs() {
        // 没有输入成分（机器自身产生产物）
        return List.of();
    }

    @Override
    public List<EmiStack> getOutputs() {
        if (allOutputs != null) {
            // 返回机器所有产物对应的 EmiStack 列表
            return allOutputs.stream().map(EmiStack::of).toList();
        }
        // 后备方案：如果未提供产物列表，则返回单一输出
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
        LOGGER.info("out:{}, mac:{}", recipe.output, key);
        widgets.addText(Text.literal(I18n.translate("emi.automationera." + key + ".title")), 5, 5, 0x404040, false);
        String descr = I18n.translate("emi.automationera." + key + ".descr");
        int y_inc = 0;
        for (int i = 0; i<=descr.length(); i+=17){
            widgets.addText(Text.literal(descr.substring(i, Math.min(descr.length(), i + 17))), 5, 20 + (i/17*8), 0x404040, false);
            y_inc = i/17*8;
        }
        widgets.addTexture(new Identifier("automationera", "textures/" + key + ".png"),
                0, 40,  // 坐标
                96, 96, // 显示宽高
                50, 50, // 纹理起始坐标（纹理内偏移）
                900, 900, 900, 900); // 纹理大小（用于裁剪/缩放）
        for (int i = 0; i < allOutputs.size(); i++) {
            int sx = 86 + (i % 4) * 18;
            int sy = 56 + (i / 4) * 18;
            widgets.addSlot(EmiStack.of(allOutputs.get(i)), sx, sy).recipeContext(this);
        }
    }

    public MachineRecipe getRecipe() {
        return recipe;
    }
}
