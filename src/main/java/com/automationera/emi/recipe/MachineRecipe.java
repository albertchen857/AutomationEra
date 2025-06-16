package com.automationera.emi.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineRecipe {
    public final Item input;
    public final Item output;

    public MachineRecipe(Item input, Item output) {
        this.input = input;
        this.output = output;
    }
}
