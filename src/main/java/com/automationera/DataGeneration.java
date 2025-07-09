package com.automationera;

import com.automationera.advance.FullShulkerBoxCriterion;
import com.automationera.advance.FullStackCriterion;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class DataGeneration implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        // 只用 Criteria.register（不需要 Registry.register）
        net.minecraft.advancement.criterion.Criteria.register(
                com.automationera.advance.FullStackCriterion.ID.toString(),
                com.automationera.advance.FullStackCriterion.INSTANCE
        );
        net.minecraft.advancement.criterion.Criteria.register(
                com.automationera.advance.FullShulkerBoxCriterion.ID.toString(),
                com.automationera.advance.FullShulkerBoxCriterion.INSTANCE
        );

        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(AdvancementsProvider::new);
    }
}
