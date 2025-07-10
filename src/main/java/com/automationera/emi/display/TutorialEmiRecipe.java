package com.automationera.emi.display;

import com.automationera.OutputRecipe;
import com.automationera.emi.emiPlugin;
import com.automationera.emi.recipe.MachineRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TutorialEmiRecipe implements EmiRecipe {
    private final MachineRecipe recipe;
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
    public TutorialEmiRecipe(MachineRecipe recipe, String key, List<Item> allOutputs) {
        this.recipe = recipe;
        this.output = EmiStack.of(recipe.output);
        this.allOutputs = allOutputs;
        this.key = key;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return emiPlugin.TUTORIAL_CATEGORY;
    }

    @Override
    public Identifier getId() {
        // 使用机器标识来生成唯一配方 ID
        return Identifier.of("automationera", "/machine_recipe_" + key);
    }

    @Override
    public List<EmiIngredient> getInputs() {
        if (allOutputs != null) {
            // 返回机器所有产物对应的 EmiStack 列表
            return allOutputs.stream()
                    .map(EmiStack::of)
                    .map(stack -> (EmiIngredient) stack)
                    .toList();
        }
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
        LOGGER.info("[EMI DEBUG] key: {}", key);

        String code = MinecraftClient.getInstance().getLanguageManager().getLanguage();
        int textlen = ("zh_cn".equalsIgnoreCase(code)) ? 17 : 40;
        if (Objects.equals(key, "circuit")){
            widgets.addText(Text.literal(I18n.translate("emi.automationera.circuit.title")), 5, 5, 0xE71B1B, false);
            String descr = I18n.translate("emi.automationera.circuit.descr");
            for (int i = 0; i<=descr.length(); i+=textlen){
                widgets.addText(Text.literal(descr.substring(i, Math.min(descr.length(), i + textlen))), 5, 20 + (i/textlen*8), 0x404040, false);
            }
            widgets.addTexture(Identifier.of("automationera", "icon.png"),
                    48, 60,  // 坐标
                    64, 64, // 显示宽高
                    0, 0, // 纹理起始坐标（纹理内偏移）
                    1000, 1000, 1000, 1000); // 纹理大小（用于裁剪/缩放）
            widgets.addButton(2, 120, 32, 12,0,0, Identifier.of("automationera", "textures/gui/wikibutton.png"), () -> true, (mouseX, mouseY, button) -> {
                Util.getOperatingSystem().open(I18n.translate("emi.automationera.circuit.wiki"));
            });
            return;
        }
        widgets.addText(Text.literal(I18n.translate("emi.automationera." + key + ".title")), 5, 5, 0x404040, false);
        String descr = I18n.translate("emi.automationera." + key + ".descr");
        widgets.addButton(128, 0, 32, 12,0,0, Identifier.of("automationera", "textures/gui/wikibutton.png"), () -> true, (mouseX, mouseY, button) -> {
            Util.getOperatingSystem().open(I18n.translate("emi.automationera." + key + ".wiki"));
        });
        for (int i = 0; i<=descr.length(); i+=textlen){
            widgets.addText(Text.literal(descr.substring(i, Math.min(descr.length(), i + textlen))), 5, 20 + (i/textlen*8), 0x404040, false);
        }
        widgets.addTexture(Identifier.of("automationera", "textures/tutorial/" + key + ".png"),
                16, 50,  // 坐标
                80, 80, // 显示宽高
                0, 0, // 纹理起始坐标（纹理内偏移）
                500, 500, 500, 500); // 纹理大小（用于裁剪/缩放）

        for (int i = 0; i < allOutputs.size(); i++) {
            int sx = i * 18;
            widgets.addSlot(EmiStack.of(allOutputs.get(i)), sx, 114).recipeContext(this);
        }
    }
}
