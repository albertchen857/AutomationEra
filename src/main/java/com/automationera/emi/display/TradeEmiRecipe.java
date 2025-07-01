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
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TradeEmiRecipe implements EmiRecipe {
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
    public TradeEmiRecipe(MachineRecipe recipe, String key, List<List<Item>> allOutputs) {
        this.recipe = recipe;
        this.input = EmiStack.of(recipe.input);
        this.output = EmiStack.of(recipe.output);
        this.allOutputs = allOutputs.get(0);
        this.allInputs = allOutputs.get(1);
        this.key = key;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return emiPlugin.TRADE_CATEGORY;
    }

    @Override
    public Identifier getId() {
        // 使用机器标识来生成唯一配方 ID
        return new Identifier("automationera", "machine_recipe_" + key);
    }

    @Override
    public List<EmiIngredient> getInputs() {
        if (key.endsWith("_in")) {
            List<EmiIngredient> inputs = new ArrayList<>();
            inputs.add(EmiStack.of(Items.EMERALD));
            // 加入其他输入
            if (allInputs != null) {
                inputs.addAll(allInputs.stream().map(EmiStack::of).map(stack -> (EmiIngredient) stack).toList());
            }
            return inputs;
        }
        if (allInputs != null) {
            // 返回机器所有产物对应的 EmiStack 列表
            return allInputs.stream()
                    .map(EmiStack::of)
                    .map(stack -> (EmiIngredient) stack)
                    .toList();
        }
        return List.of();
    }

    @Override
    public List<EmiStack> getOutputs() {
        if (key.endsWith("_out")){
            List<EmiStack> result = new ArrayList<>();
            result.add(EmiStack.of(Items.EMERALD));
            if (allOutputs != null) {
                result.addAll(allOutputs.stream().map(EmiStack::of).toList());
                return result;
            }
        }
        if (allOutputs != null) {
            return allOutputs.stream().map(EmiStack::of).toList();
        }
        return List.of();
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
        String job = key.split("_")[0];
        String code = MinecraftClient.getInstance().getLanguageManager().getLanguage();
        int textlen = ("zh_cn".equalsIgnoreCase(code)) ? 17 : 40;
        if (Objects.equals(key, "trade_main")){
            widgets.addText(Text.literal(I18n.translate("emi.automationera.circuit.title")), 5, 5, 0xE71B1B, false);
            String descr = I18n.translate("emi.automationera.circuit.descr");
            for (int i = 0; i<=descr.length(); i+=textlen){
                widgets.addText(Text.literal(descr.substring(i, Math.min(descr.length(), i + textlen))), 5, 20 + (i/textlen*8), 0x404040, false);
            }
            widgets.addTexture(new Identifier("automationera", "icon.png"),
                    48, 60,  // 坐标
                    64, 64, // 显示宽高
                    0, 0, // 纹理起始坐标（纹理内偏移）
                    1000, 1000, 1000, 1000); // 纹理大小（用于裁剪/缩放）
            widgets.addButton(2, 120, 32, 12,0,0, new Identifier("automationera", "textures/gui/wikibutton.png"), () -> true, (mouseX, mouseY, button) -> {
                Util.getOperatingSystem().open(I18n.translate("emi.automationera.circuit.wiki"));
            });
            return;
        }

        widgets.addText(Text.literal(I18n.translate("emi.automationera." + key + ".title")), 5, 5, 0x404040, false);
        String descr = I18n.translate("emi.automationera." + key + ".descr");
        widgets.addButton(128, 0, 32, 12,0,0, new Identifier("automationera", "textures/gui/wikibutton.png"), () -> true, (mouseX, mouseY, button) -> {
            Util.getOperatingSystem().open(I18n.translate("emi.automationera.trade.wiki"));
        });
        int ingOlen, ingIlen;
        int inglen = 0;
        if (Ing.containsKey(key)){
            ingOlen = Ing.get(key).get(0).size();
            ingIlen = Ing.get(key).get(1).size();
            inglen =  key.endsWith("_out") ? ingOlen : ingIlen;
        }
        for (int i = 0; i<=descr.length(); i+=textlen){
            widgets.addText(Text.literal(descr.substring(i, Math.min(descr.length(), i + textlen))), 5, 20 + (i/textlen*8), 0x404040, false);
        }

        widgets.addTexture(new Identifier("automationera", "textures/villager/" + key.split("_")[0] + ".png"),
                0, 30,  // 坐标
                48, 96, // 显示宽高
                0, 0, // 纹理起始坐标（纹理内偏移）
                410, 825, 410, 825); // 纹理大小（用于裁剪/缩放）
        List<Item> preall = key.split("_")[1].equals("out") ? allOutputs : allInputs;
        int premd = key.split("_")[1].equals("out") ? 0 : 1;
        for (int i = 0; i < preall.size() + inglen; i++) {
            int sx = 52 + (i % 6) * 18;
            int sy = 40 + (i / 6) * 18;
            if (inglen==0 || (i < preall.size())){
                widgets.addSlot(EmiStack.of(preall.get(i)), sx, sy).recipeContext(this);
            }else{
                EmiIngredient ingredient = EmiIngredient.of(Template.get(Ing.get(job).get(premd).get(i-preall.size())).stream().map(EmiStack::of).toList());
                widgets.addSlot(ingredient, sx, sy).recipeContext(this);
            }
        }
        widgets.addSlot(EmiStack.of(Items.EMERALD), 0, 114).recipeContext(this);
    }

    public MachineRecipe getRecipe() {
        return recipe;
    }
}
