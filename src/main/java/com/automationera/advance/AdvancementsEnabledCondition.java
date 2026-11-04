package com.automationera.advance;

import com.automationera.ModConfig;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import org.jetbrains.annotations.Nullable;

/**
 * 进度 JSON 上的加载条件（fabric:load_conditions）：开发者选项 enableAdvancements 关闭时，本模组的进度全部不加载。
 */
public final class AdvancementsEnabledCondition implements ResourceCondition {
    public static final AdvancementsEnabledCondition INSTANCE = new AdvancementsEnabledCondition();
    public static final ResourceConditionType<AdvancementsEnabledCondition> TYPE = ResourceConditionType.create(
            Identifier.fromNamespaceAndPath("automationera", "advancements_enabled"), MapCodec.unit(INSTANCE));

    private static boolean registered = false;

    private AdvancementsEnabledCondition() {
    }

    public static void register() {
        if (!registered) {
            ResourceConditions.register(TYPE);
            registered = true;
        }
    }

    @Override
    public ResourceConditionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean test(@Nullable RegistryOps.RegistryInfoLookup registryInfo) {
        return ModConfig.advancementsEnabled();
    }
}
