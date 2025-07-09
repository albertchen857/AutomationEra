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
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MachineEmiRecipe implements EmiRecipe {
    private final MachineRecipe recipe;
    private final EmiStack input;
    private final EmiStack output;
    private final List<Item> allOutputs;
    private final List<Item> allInputs;
    private final String key; // 机器标识（用于定位纹理和本地化字符串）
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/emi/display/MachineEmiRecipe");
    private final Map<String,List<List<String>>> Ing = new OutputRecipe().OutputIng();
    private final Map<String, List<Item>> Template = new OutputRecipe().OutputTemplate();

    /**
     * 构造一个机器配方，用于 EMI 显示。
     *
     * @param recipe   包含虚拟输入（例如 AIR）和代表性输出的 MachineRecipe 对象
     * @param key      机器的标识符（用于找到对应的纹理和本地化文本）
     * @param allOutputs 该机器能产出的所有 Item 列表
     */
    public MachineEmiRecipe(MachineRecipe recipe, String key, List<List<Item>> allOutputs) {
        this.recipe = recipe;
        this.input = EmiStack.of(recipe.input);
        this.output = EmiStack.of(recipe.output);
        this.allOutputs = allOutputs.get(0);
        this.allInputs = allOutputs.get(1);
        this.key = key;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return emiPlugin.MACHINE_CATEGORY;
    }

    @Override
    public Identifier getId() {
        // 使用机器标识来生成唯一配方 ID
        return Identifier.of("automationera", "machine_recipe_" + key);
    }

    @Override
    public List<EmiIngredient> getInputs() {
        if (allInputs != null) {
            // 返回机器所有产物对应的 EmiStack 列表
            return allInputs.stream()
                    .map(EmiStack::of)
                    .map(stack -> (EmiIngredient) stack)
                    .toList();
        }
        return List.of(input);
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
        if (allOutputs == null || allInputs == null) {
            LOGGER.error("[EMI ERROR] allOutputs or allInputs is null! key={}", key);
            return;
        }
        if (Template == null) {
            LOGGER.error("[EMI ERROR] Template is null!");
            return;
        }
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
        int ingOlen = 0;
        int ingIlen = 0;
        if (Ing.containsKey(key)){
            if(Ing.get(key).size()==2){
                ingOlen = Ing.get(key).get(0).size();
                ingIlen = Ing.get(key).get(1).size();
            }
        }
        LOGGER.info("OLEN:{}, ILEN:{}", ingOlen, ingIlen);
        //int y_inc = 0;
        for (int i = 0; i<=descr.length(); i+=textlen){
            widgets.addText(Text.literal(descr.substring(i, Math.min(descr.length(), i + textlen))), 5, 20 + (i/textlen*8), 0x404040, false);
            //y_inc = i/textlen*8;
        }
        widgets.addTexture(Identifier.of("automationera", "textures/" + key + ".png"),
                0, 40,  // 坐标
                96, 96, // 显示宽高
                0, 0, // 纹理起始坐标（纹理内偏移）
                500, 500, 500, 500); // 纹理大小（用于裁剪/缩放）
        if (allOutputs.size() + ingOlen > 0){
            for (int i = 0; i < allOutputs.size() + ingOlen; i++) {
                int sx = 86 + (i % 4) * 18;
                int sy = 56 + (i / 4) * 18;
                if (ingOlen==0 || i < allOutputs.size()){
                    widgets.addSlot(EmiStack.of(allOutputs.get(i)), sx, sy).recipeContext(this);
                }else{
                    EmiIngredient ingredient = EmiIngredient.of(Template.get(Ing.get(key).get(0).get(i-allOutputs.size())).stream().map(EmiStack::of).toList());
                    widgets.addSlot(ingredient, sx, sy).recipeContext(this);
                }
            }
        }
        if (allInputs.size() + ingIlen > 0){
            for (int i = 0; i < allInputs.size() + ingIlen; i++) {
                int sx = i * 18;
                if (ingIlen==0 || i < allInputs.size()){
                    widgets.addSlot(EmiStack.of(allInputs.get(i)), sx, 114).recipeContext(this);
                }else{
                    EmiIngredient ingredient = EmiIngredient.of(Template.get(Ing.get(key).get(1).get(i-allInputs.size())).stream().map(EmiStack::of).toList());
                    widgets.addSlot(ingredient, sx, 114).recipeContext(this);
                }
            }
        }
    }
}
