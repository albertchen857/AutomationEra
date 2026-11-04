package com.automationera.basic;

import com.mojang.blaze3d.Blaze3D;
import java.net.URI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Util;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ExportFile {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger("AutomationEraTutorial/FileManager");

    public static Path ensureAutomationEraDir() throws IOException {
        Path dir = FabricLoader.getInstance().getGameDir().resolve("schematics").resolve("AutomationEra");
        Files.createDirectories(dir);
        return dir;
    }

    /**
     * 把整台机器的结构文件（tutorial/&lt;id&gt;/&lt;id&gt;_full.nbt）导出到 schematics/AutomationEra/&lt;id&gt;.nbt。
     * 0.1.9.1 起重新拆分的机器各步骤文件只含本步新增方块，所以优先导出 _full；
     * 没有 _full 的（保持旧格式的机器）导出最后一步 lastStep。
     *
     * @return 导出的文件路径；资源不存在或写入失败时返回 null
     */
    public static Path exportTutorialFolder(String machineId, int lastStep) {
        String modid = "automationera";
        Identifier id = Identifier.fromNamespaceAndPath(modid, "tutorial/" + machineId + "/" + machineId + "_full.nbt");
        try {
            Path dest = ensureAutomationEraDir();
            ResourceManager rm = Minecraft.getInstance().getResourceManager();
            Resource res = rm.getResource(id).orElse(null);
            if (res == null) {
                id = Identifier.fromNamespaceAndPath(modid, "tutorial/" + machineId + "/" + machineId + "_step" + lastStep + ".nbt");
                res = rm.getResource(id).orElse(null);
            }
            if (res == null) {
                LOGGER.warn("Resource not found: {}", id);
                return null;
            }
            Path out = dest.resolve(machineId + ".nbt");
            try (InputStream in = res.open()) {
                Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
            }
            return out;
        } catch (IOException e) {
            LOGGER.error("Export failed: {}", id, e);
            return null;
        }
    }

    // 用原版的 Util 打开文件夹/链接：游戏里 java.awt 通常是 headless，Desktop 不可用
    public static void openAutomationEraDir() {
        try {
            Blaze3D.openPath(ensureAutomationEraDir());
        } catch (IOException e) {
            LOGGER.error("Cannot open schematics folder", e);
        }
    }

    public static void buymeacoffee() {
        Blaze3D.openUri(URI.create("https://buymeacoffee.com/albertchen857"));
    }
}
