package com.automationera;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 隐藏的开发者选项（没有界面），文件：config/automationera.json
 * <pre>
 * {
 *   "enableAdvancements": true,        // 是否开启生电成就（关掉后本模组的进度不会加载，需要 /reload 或重进世界）
 *   "enableAdvancementRewards": true   // 是否开启生电成就奖励（如下界合金信标的 10000 经验）
 * }
 * </pre>
 */
public final class ModConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/Config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("automationera.json");

    private static Data data = new Data();

    private static final class Data {
        boolean enableAdvancements = true;
        boolean enableAdvancementRewards = true;
    }

    private ModConfig() {
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                try (Reader r = Files.newBufferedReader(FILE)) {
                    Data read = GSON.fromJson(r, Data.class);
                    if (read != null) data = read;
                }
            }
            // 写回一次，补齐新增的字段
            try (Writer w = Files.newBufferedWriter(FILE)) {
                GSON.toJson(data, w);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read {}, using defaults", FILE, e);
            data = new Data();
        }
    }

    public static boolean advancementsEnabled() {
        return data.enableAdvancements;
    }

    public static boolean advancementRewardsEnabled() {
        return data.enableAdvancementRewards;
    }
}
