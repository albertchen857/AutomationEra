package com.automationera;

import com.automationera.advance.AdvancementsEnabledCondition;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class AutomationeraDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		AdvancementsEnabledCondition.register();
		fabricDataGenerator.createPack().addProvider(AdvancementsProvider::new);
	}
}
