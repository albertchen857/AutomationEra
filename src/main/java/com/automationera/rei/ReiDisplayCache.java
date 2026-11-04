package com.automationera.rei;

import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.impl.client.ClientHelperImpl;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * REI 配方（Display）的按服务器本地缓存。
 * <p>
 * 1.21.2 起服务器不再把配方发给客户端，只有服务端也装了 REI 时，REI 才会通过 DisplaySyncPacket 同步全部配方；
 * 原版/没装 REI 的服务器上客户端只能看到配方书里解锁的那部分。
 * <ul>
 *   <li>记录：每次真的收到服务器（单人游戏是内置服务器）同步的配方，退出时存到 config/automationera/rei_cache/&lt;服务器&gt;.nbt</li>
 *   <li>恢复：进服后服务器没有 REI 时，REI 每次重载都会调用 {@link #addCachedDisplays}：
 *       优先用这个服务器的缓存，没有的话用单人游戏的缓存（原版 + 本地模组的配方）</li>
 * </ul>
 * 物品标签（tag）原版本来就会同步给客户端，不需要缓存。
 * 用 Display.codec()（字符串 id）存储，不同服务器/整合包的数字 id 不一样，网络格式不能直接落盘。
 */
public final class ReiDisplayCache {
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEra/ReiCache");
    private static final String LOCAL = "local";
    private static final Path DIR = FabricLoader.getInstance().getConfigDir().resolve("automationera").resolve("rei_cache");

    // 本次连接
    @Nullable private static String sessionKey;
    private static final List<Display> received = new ArrayList<>();
    @Nullable private static RegistryAccess receivedRegistries;
    // 已读取的缓存
    @Nullable private static String loadedKey;
    private static List<Display> loaded = List.of();

    private ReiDisplayCache() {
    }

    public static void init() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            sessionKey = keyOf(client);
            received.clear();
            receivedRegistries = null;
            loadedKey = null;
            loaded = List.of();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (sessionKey != null && !received.isEmpty() && receivedRegistries != null) {
                save(sessionKey, List.copyOf(received), receivedRegistries);
            }
            sessionKey = null;
            received.clear();
            receivedRegistries = null;
            loaded = List.of();
            loadedKey = null;
        });
    }

    /** 由 DisplaySyncPacket.handle 的 mixin 调用：记录服务器真实同步的配方 */
    public static void onServerSync(boolean reset, Iterable<Display> displays) {
        Minecraft client = Minecraft.getInstance();
        if (client.getConnection() == null) return;
        if (reset) received.clear();
        for (Display d : displays) received.add(d);
        receivedRegistries = client.getConnection().registryAccess();
    }

    /** reiPlugin.registerDisplays 末尾调用：服务器没有 REI 时补上缓存的配方 */
    public static void addCachedDisplays(DisplayRegistry registry) {
        Minecraft client = Minecraft.getInstance();
        if (sessionKey == null || client.getConnection() == null) return;
        if (ClientHelperImpl.getInstance().canUsePackets()) return; // 服务器装了 REI，会自己同步
        if (!sessionKey.equals(loadedKey)) {
            RegistryAccess registries = client.getConnection().registryAccess();
            loaded = load(sessionKey, registries);
            if (loaded.isEmpty() && !LOCAL.equals(sessionKey)) {
                loaded = load(LOCAL, registries);
            }
            loadedKey = sessionKey;
            LOGGER.info("REI cache: server has no REI, using {} cached displays", loaded.size());
        }
        for (Display d : loaded) {
            registry.add(d);
        }
    }

    @Nullable
    private static String keyOf(Minecraft client) {
        if (client.hasSingleplayerServer()) return LOCAL;
        ServerData info = client.getCurrentServer();
        if (info == null || info.ip == null) return null;
        return "server_" + info.ip.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "_");
    }

    private static Path fileOf(String key) {
        return DIR.resolve(key + ".nbt");
    }

    private static void save(String key, List<Display> displays, RegistryAccess registries) {
        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
        ListTag list = new ListTag();
        int failed = 0;
        for (Display d : displays) {
            var r = Display.codec().encodeStart(ops, d).result();
            if (r.isPresent()) list.add(r.get());
            else failed++;
        }
        CompoundTag root = new CompoundTag();
        root.putString("mc", SharedConstants.getCurrentVersion().name());
        root.put("displays", list);
        try {
            Files.createDirectories(DIR);
            NbtIo.writeCompressed(root, fileOf(key));
            LOGGER.info("REI cache: saved {} displays for {} ({} could not be encoded)", list.size(), key, failed);
        } catch (Exception e) {
            LOGGER.error("REI cache: failed to save {}", key, e);
        }
    }

    private static List<Display> load(String key, RegistryAccess registries) {
        Path file = fileOf(key);
        if (!Files.exists(file)) return List.of();
        try {
            CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
            if (!SharedConstants.getCurrentVersion().name().equals(root.getString("mc").orElse(""))) {
                LOGGER.info("REI cache: {} was made for another game version, ignored", key);
                return List.of();
            }
            RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
            List<Display> out = new ArrayList<>();
            int failed = 0;
            for (Tag e : root.getListOrEmpty("displays")) {
                // 当前环境里不存在的物品/配方，或 REI 自身编解码不对称（如锻造台的 SmithingDisplay）的条目，跳过即可
                try {
                    var r = Display.codec().parse(ops, e).result();
                    if (r.isPresent()) out.add(r.get());
                    else failed++;
                } catch (Exception ex) {
                    failed++;
                }
            }
            LOGGER.info("REI cache: loaded {} displays from {} ({} skipped)", out.size(), key, failed);
            return out;
        } catch (Exception e) {
            LOGGER.error("REI cache: failed to load {}", key, e);
            return List.of();
        }
    }
}
