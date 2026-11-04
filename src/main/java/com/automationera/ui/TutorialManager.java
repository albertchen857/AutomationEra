package com.automationera.ui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 教程结构文件（assets/automationera/tutorial/&lt;id&gt;/&lt;id&gt;_stepN.nbt）的读取与缓存。
 * 0.1.9.1 起每个 stepN 只含这一步新增的方块，界面上显示的是第 1 步到当前步的累计；
 * &lt;id&gt;_full.nbt 是完整机器（导出投影用），steps.json 是每步的元信息。
 * 1.21.6+ 的 GUI 改为延迟渲染，真正的 3D 绘制在 {@link TutorialStructureRenderer} 里完成。
 */
public class TutorialManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEraTutorial");

    // 每帧都会用到，避免每帧重新解压 NBT
    private static final Map<String, Optional<NbtCompound>> NBT_CACHE = new HashMap<>();
    private static final Map<String, Optional<Structure>> STRUCTURE_CACHE = new HashMap<>();

    public record PlacedBlock(int x, int y, int z, BlockState state) {
    }

    public record Structure(List<PlacedBlock> blocks, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        public int maxDimension() {
            return Math.max(maxX - minX + 1, Math.max(maxY - minY + 1, maxZ - minZ + 1));
        }
    }

    /**
     * steps.json 里每一步的信息（由 litematic_steps.py 拆分时生成）
     *
     * y0/y1：这一步覆盖的 Y 范围（结构坐标）；role：skeleton/fill/opening/detail/furnish/attach/redstone/entity；
     * old：这一步的方块主要来自旧教程第几步，用来沿用旧的步骤说明
     */
    public record StepMeta(int step, int y0, int y1, String role, int old) {
    }

    private static final Map<String, Structure> CUMULATIVE_CACHE = new HashMap<>();
    private static final Map<String, List<StepMeta>> META_CACHE = new HashMap<>();

    /** 资源包重载后结构可能变化，打开教程主界面时清一次缓存 */
    public static void clearCache() {
        NBT_CACHE.clear();
        STRUCTURE_CACHE.clear();
        CUMULATIVE_CACHE.clear();
        META_CACHE.clear();
    }

    /**
     * 从第 1 步累计到第 upto 步的全部方块（0.1.9.1 起每个步骤文件只存本步新增的方块）。
     * 包围盒固定用整台机器的，切换步骤时镜头不会跳。
     */
    @Nullable
    public static Structure loadCumulative(String id, int upto) {
        Structure all = union(id, countSteps(id));
        if (all == null) return null;
        Structure part = union(id, upto);
        if (part == null) return null;
        return new Structure(part.blocks(), all.minX(), all.minY(), all.minZ(), all.maxX(), all.maxY(), all.maxZ());
    }

    @Nullable
    private static Structure union(String id, int upto) {
        String k = id + "#cum" + upto;
        Structure cached = CUMULATIVE_CACHE.get(k);
        if (cached != null) return cached;
        List<PlacedBlock> blocks = new ArrayList<>();
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (int s = 1; s <= upto; s++) {
            Structure st = loadStructure(id, s);
            if (st == null) continue;
            blocks.addAll(st.blocks());
            if (st.blocks().isEmpty()) continue;
            minX = Math.min(minX, st.minX()); minY = Math.min(minY, st.minY()); minZ = Math.min(minZ, st.minZ());
            maxX = Math.max(maxX, st.maxX()); maxY = Math.max(maxY, st.maxY()); maxZ = Math.max(maxZ, st.maxZ());
        }
        if (minX == Integer.MAX_VALUE) return null;
        Structure s = new Structure(List.copyOf(blocks), minX, minY, minZ, maxX, maxY, maxZ);
        CUMULATIVE_CACHE.put(k, s);
        return s;
    }

    /** 每一步的元信息；没有 steps.json（旧数据）时返回空列表 */
    public static List<StepMeta> loadStepMeta(String id) {
        return META_CACHE.computeIfAbsent(id, TutorialManager::readStepMeta);
    }

    private static List<StepMeta> readStepMeta(String id) {
        Identifier rid = Identifier.of("automationera", "tutorial/" + id + "/steps.json");
        Resource res = MinecraftClient.getInstance().getResourceManager().getResource(rid).orElse(null);
        if (res == null) return List.of();
        try (var reader = res.getReader()) {
            List<StepMeta> list = new ArrayList<>();
            for (JsonElement e : JsonParser.parseReader(reader).getAsJsonObject().getAsJsonArray("steps")) {
                JsonObject o = e.getAsJsonObject();
                int y0 = o.get("y0").isJsonNull() ? 0 : o.get("y0").getAsInt();
                int y1 = o.get("y1").isJsonNull() ? y0 : o.get("y1").getAsInt();
                list.add(new StepMeta(o.get("step").getAsInt(), y0, y1, o.get("role").getAsString(), o.get("old").getAsInt()));
            }
            return List.copyOf(list);
        } catch (Exception ex) {
            LOGGER.error("steps.json 读取失败 {}", rid, ex);
            return List.of();
        }
    }

    @Nullable
    public static StepMeta stepMeta(String id, int step) {
        List<StepMeta> list = loadStepMeta(id);
        return step >= 1 && step <= list.size() ? list.get(step - 1) : null;
    }

    private static String key(String id, int step) {
        return id + "#" + step;
    }

    @Nullable
    public static NbtCompound loadNbtFromResource(String id, int step) {
        return NBT_CACHE.computeIfAbsent(key(id, step), k -> Optional.ofNullable(readNbt(id, step))).orElse(null);
    }

    @Nullable
    private static NbtCompound readNbt(String id, int step) {
        if (step < 1) return null;
        ResourceManager rm = MinecraftClient.getInstance().getResourceManager();
        Identifier rid = Identifier.of("automationera", "tutorial/" + id + "/" + id + "_step" + step + ".nbt");
        Resource res = rm.getResource(rid).orElse(null);
        if (res == null) return null;
        try (InputStream is = res.getInputStream()) {
            return NbtIo.readCompressed(is, NbtSizeTracker.ofUnlimitedBytes());
        } catch (Exception e) {
            LOGGER.error("NBT加载失败 {}", rid, e);
            return null;
        }
    }

    public static boolean hasStep(String id, int step) {
        return loadNbtFromResource(id, step) != null;
    }

    /** 该教程一共有多少步（step1 开始连续存在的 nbt 数量） */
    public static int countSteps(String id) {
        int n = 0;
        while (hasStep(id, n + 1)) n++;
        return n;
    }

    @Nullable
    public static Structure loadStructure(String id, int step) {
        return STRUCTURE_CACHE.computeIfAbsent(key(id, step), k -> Optional.ofNullable(parseStructure(loadNbtFromResource(id, step)))).orElse(null);
    }

    @Nullable
    public static Structure parseStructure(@Nullable NbtCompound nbt) {
        if (nbt == null) return null;
        Optional<NbtList> paletteNbt = nbt.getList("palette");
        Optional<NbtList> blocksNbt = nbt.getList("blocks");
        if (paletteNbt.isEmpty() || blocksNbt.isEmpty()) {
            LOGGER.warn("Tutorial NBT invalid: missing palette/blocks");
            return null;
        }
        List<BlockState> palette = new ArrayList<>();
        for (int i = 0; i < paletteNbt.get().size(); i++) {
            palette.add(readBlockStateFromNbt(paletteNbt.get().getCompoundOrEmpty(i)));
        }

        List<PlacedBlock> blocks = new ArrayList<>();
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        NbtList list = blocksNbt.get();
        for (int i = 0; i < list.size(); i++) {
            NbtCompound blk = list.getCompoundOrEmpty(i);
            NbtList pos = blk.getList("pos").orElse(new NbtList());
            int x = pos.getInt(0).orElse(0), y = pos.getInt(1).orElse(0), z = pos.getInt(2).orElse(0);
            minX = Math.min(minX, x); maxX = Math.max(maxX, x);
            minY = Math.min(minY, y); maxY = Math.max(maxY, y);
            minZ = Math.min(minZ, z); maxZ = Math.max(maxZ, z);
            int idx = blk.getInt("state").orElse(-1);
            if (idx < 0 || idx >= palette.size()) continue;
            BlockState bs = palette.get(idx);
            if (bs.isAir()) continue;
            blocks.add(new PlacedBlock(x, y, z, bs));
        }
        if (minX == Integer.MAX_VALUE) {
            minX = minY = minZ = maxX = maxY = maxZ = 0;
        }
        return new Structure(List.copyOf(blocks), minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static BlockState readBlockStateFromNbt(NbtCompound compound) {
        String name = compound.getString("Name").orElse("");
        Identifier id = Identifier.tryParse(name);
        if (id == null) {
            return Blocks.AIR.getDefaultState();
        }
        Block block = Registries.BLOCK.get(id);
        BlockState state = block.getDefaultState();
        Optional<NbtCompound> props = compound.getCompound("Properties");
        if (props.isPresent()) {
            for (String propName : props.get().getKeys()) {
                Property<?> prop = block.getStateManager().getProperty(propName);
                if (prop == null) continue;
                String value = props.get().getString(propName).orElse(null);
                if (value == null) continue;
                state = withProperty(state, prop, value);
            }
        }
        return state;
    }

    private static <T extends Comparable<T>> BlockState withProperty(BlockState state, Property<T> prop, String value) {
        return prop.parse(value).map(v -> state.with(prop, v)).orElse(state);
    }
}
