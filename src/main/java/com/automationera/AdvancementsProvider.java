package com.automationera;

import com.automationera.advance.Advancements;
import com.automationera.advance.AdvancementsEnabledCondition;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementsProvider extends FabricAdvancementProvider {
    public AdvancementsProvider(FabricDataOutput dataGenerator, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataGenerator,registryLookup);
    }

    /** 所有进度都带加载条件：开发者选项关闭“生电成就”时整组不加载 */
    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup provider, Consumer<AdvancementEntry> consumer) {
        new Advancements().accept(withConditions(consumer, AdvancementsEnabledCondition.INSTANCE), provider);
    }
}
