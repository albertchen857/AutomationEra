package com.automationera.ui;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AnalysisList {
    private static List<@NotNull Item> NonInclude = List.of(
            Items.AIR,
            Items.BARRIER,
            Items.YELLOW_CONCRETE,
            Items.BLACK_CONCRETE,
            Items.BLUE_STAINED_GLASS,
            Items.LIME_STAINED_GLASS
    );
    public static class Entry {
        public final ItemStack stack;
        public final Text displayName;
        public final int count;
        public Entry(ItemStack stack, Text displayName, int count) {
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
    public AnalysisList(NbtCompound currentNbt, NbtCompound baseNbt, boolean consist) {
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

    public AnalysisList(NbtCompound nbt) {
        this.result = toEntryList(statItem(nbt, true));
    }

    /** 多个步骤文件的材料合计（累计渲染时用：第 1 步到当前步） */
    public AnalysisList(List<NbtCompound> nbts) {
        Map<Item, Integer> sum = new HashMap<>();
        for (NbtCompound nbt : nbts) {
            statItem(nbt, true).forEach((item, n) -> sum.merge(item, n, Integer::sum));
        }
        this.result = toEntryList(sum);
    }

    private Map<Item, Integer> statItem(NbtCompound nbt, boolean include) {
        Map<Item, Integer> map = new HashMap<>();
        if (nbt == null || !nbt.contains("palette") || !nbt.contains("blocks")) return map;
        Optional<NbtList> palette = nbt.getList("palette");
        if(palette.isEmpty()) return map;
        Optional<NbtList> blocks = nbt.getList("blocks");
        if (blocks.isPresent()){
            for (int i = 0; i < blocks.get().size(); i++) {
                NbtCompound blk = blocks.get().getCompoundOrEmpty(i);
                int id = blk.getInt("state").orElse(0);
                String blockId = palette.get().getCompoundOrEmpty(id).getString("Name").orElse("");
                var block = Registries.BLOCK.get(Identifier.of(blockId));
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

