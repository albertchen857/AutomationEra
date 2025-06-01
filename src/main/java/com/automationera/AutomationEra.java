package com.automationera;

import com.automationera.FullStackCriterion;
import com.automationera.advance.StackTriggerTemplate;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
			if (server.getTicks() % 20 == 0) {
				for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
					// 检查铁锭
					checkPlayerStacks(player, Items.IRON_INGOT, 4);
					// 检查圆石
					checkPlayerStacks(player, Items.COBBLESTONE, 9);
					// 检查金锭
					checkPlayerStacks(player, Items.GOLD_INGOT, 6);
				}
			}
		});
	}

	private void checkPlayerStacks(ServerPlayerEntity player, net.minecraft.item.Item item, int requiredStacks) {
		int fullStackCount = 0;
		int stackSize = item.getMaxCount();

		// 扫描玩家背包
		for (var stack : player.getInventory().main) {
			if (!stack.isEmpty() &&
					stack.getItem() == item &&
					stack.getCount() == stackSize) {
				fullStackCount++;
			}
		}

		// 如果满足条件则触发进度
		if (fullStackCount >= requiredStacks) {
			FULL_STACK_CRITERION.trigger(player, item, requiredStacks);
		}
	}
}