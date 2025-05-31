package com.automationera;

import com.automationera.advance.AdvancementTriggerHandler;
import com.automationera.advance.FullStackCriterion;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutomationEra implements ModInitializer {
	public static final String MOD_ID = "automationera";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final FullStackCriterion FULL_STACK_CRITERION = new FullStackCriterion();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Criteria.register(FULL_STACK_CRITERION);
		// 添加每刻检查
		ServerTickEvents.START_SERVER_TICK.register(server -> {
			if (server.getTicks() % 100 == 0) {
				for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
					AdvancementTriggerHandler.checkFullStacks(player);
				}
			}
		});
	}
}