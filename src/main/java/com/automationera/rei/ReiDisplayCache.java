package com.automationera.rei;

import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.impl.client.ClientHelperImpl;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
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
    @Nullable private static DynamicRegistryManager receivedRegistries;
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
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() == null) return;
        if (reset) received.clear();
        for (Display d : displays) received.add(d);
        receivedRegistries = client.getNetworkHandler().getRegistryManager();
    }

    /** reiPlugin.registerDisplays 末尾调用：服务器没有 REI 时补上缓存的配方 */
    public static void addCachedDisplays(DisplayRegistry registry) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (sessionKey == null || client.getNetworkHandler() == null) return;
        if (ClientHelperImpl.getInstance().canUsePackets()) return; // 服务器装了 REI，会自己同步
        if (!sessionKey.equals(loadedKey)) {
            DynamicRegistryManager registries = client.getNetworkHandler().getRegistryManager();
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
    private static String keyOf(MinecraftClient client) {
        if (client.isIntegratedServerRunning()) return LOCAL;
        ServerInfo info = client.getCurrentServerEntry();
        if (info == null || info.address == null) return null;
        return "server_" + info.address.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "_");
    }

    private static Path fileOf(String key) {
        return DIR.resolve(key + ".nbt");
    }

    private static void save(String key, List<Display> displays, DynamicRegistryManager registries) {
        RegistryOps<NbtElement> ops = RegistryOps.of(NbtOps.INSTANCE, registries);
        NbtList list = new NbtList();
        int failed = 0;
        for (Display d : displays) {
            var r = Display.codec().encodeStart(ops, d).result();
            if (r.isPresent()) list.add(r.get());
            else failed++;
        }
        NbtCompound root = new NbtCompound();
        root.putString("mc", SharedConstants.getGameVersion().name());
        root.put("displays", list);
        try {
            Files.createDirectories(DIR);
            NbtIo.writeCompressed(root, fileOf(key));
            LOGGER.info("REI cache: saved {} displays for {} ({} could not be encoded)", list.size(), key, failed);
        } catch (Exception e) {
            LOGGER.error("REI cache: failed to save {}", key, e);
        }
    }

    private static List<Display> load(String key, DynamicRegistryManager registries) {
        Path file = fileOf(key);
        if (!Files.exists(file)) return List.of();
        try {
            NbtCompound root = NbtIo.readCompressed(file, NbtSizeTracker.ofUnlimitedBytes());
            if (!SharedConstants.getGameVersion().name().equals(root.getString("mc").orElse(""))) {
                LOGGER.info("REI cache: {} was made for another game version, ignored", key);
                return List.of();
            }
            RegistryOps<NbtElement> ops = RegistryOps.of(NbtOps.INSTANCE, registries);
            List<Display> out = new ArrayList<>();
            int failed = 0;
            for (NbtElement e : root.getListOrEmpty("displays")) {
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
