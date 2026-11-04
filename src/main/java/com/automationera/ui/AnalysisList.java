package com.automationera.ui;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AnalysisList {
    private static List<@NotNull Item> NonInclude = List.of(
            Items.AIR,
            Items.BARRIER,
            Items.CONCRETE.yellow(),
            Items.CONCRETE.black(),
            Items.STAINED_GLASS.blue(),
            Items.STAINED_GLASS.lime()
    );
    public static class Entry {
        public final ItemStack stack;
        public final Component displayName;
        public final int count;
        public Entry(ItemStack stack, Component displayName, int count) {
            this.stack = stack;
            this.displayName = displayName;
            this.count = count;
        }
        public String countBoxGroup() {
            int groups = count / 64;
            int groupRemain = count % 64;
            // 格式如：128/0S0
            return count + "/" + groups + "S" + groupRemain;
        }
    }
    private final List<Entry> result;

    public List<Entry> getResult() { return result; }

    // 对比底层原文
    public AnalysisList(CompoundTag currentNbt, CompoundTag baseNbt, boolean consist) {
        Map<Item, Integer> now = statItem(currentNbt, true);
        if (consist && baseNbt != null) {
            Map<Item, Integer> prev = statItem(baseNbt, true);
            var it = now.entrySet().iterator();
            while (it.hasNext()){
                var e=it.next();
                Item item = e.getKey();
                int num = e.getValue()- prev.getOrDefault(item,0);
                if (num>0){
                    e.setValue(num);
                }else{
                    it.remove();
                }
            }
        }
        this.result = toEntryList(now);
    }

    public AnalysisList(CompoundTag nbt) {
        this.result = toEntryList(statItem(nbt, true));
    }

    /** 多个步骤文件的材料合计（累计渲染时用：第 1 步到当前步） */
    public AnalysisList(List<CompoundTag> nbts) {
        Map<Item, Integer> sum = new HashMap<>();
        for (CompoundTag nbt : nbts) {
            statItem(nbt, true).forEach((item, n) -> sum.merge(item, n, Integer::sum));
        }
        this.result = toEntryList(sum);
    }

    private Map<Item, Integer> statItem(CompoundTag nbt, boolean include) {
        Map<Item, Integer> map = new HashMap<>();
        if (nbt == null || !nbt.contains("palette") || !nbt.contains("blocks")) return map;
        Optional<ListTag> palette = nbt.getList("palette");
        if(palette.isEmpty()) return map;
        Optional<ListTag> blocks = nbt.getList("blocks");
        if (blocks.isPresent()){
            for (int i = 0; i < blocks.get().size(); i++) {
                CompoundTag blk = blocks.get().getCompoundOrEmpty(i);
                int id = blk.getInt("state").orElse(0);
                String blockId = palette.get().getCompoundOrEmpty(id).getString("Name").orElse("");
                var block = BuiltInRegistries.BLOCK.getValue(Identifier.parse(blockId));
                var item = block.asItem();
                if (!(item == null || item.equals(Items.AIR) || (NonInclude.contains(item)) && include)) {
                    map.put(item, map.getOrDefault(item, 0) + 1);
                }
            }
        }
        return map;
    }

    private List<Entry> toEntryList(Map<Item, Integer> map) {
        List<Entry> list = new ArrayList<>();
        for (var e : map.entrySet()) {
            list.add(new Entry(new ItemStack(e.getKey()), e.getKey().getName(), e.getValue()));
        }
        list.sort((a, b) -> Integer.compare(b.count, a.count));
        return list;
    }

    public int size() { return result.size(); }
    public Entry get(int i) { return result.get(i); }
}

