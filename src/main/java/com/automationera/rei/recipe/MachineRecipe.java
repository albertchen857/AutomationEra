package com.automationera.rei.recipe;

import net.minecraft.item.Item;

public class MachineRecipe {
    public final Item input;
    public final Item output;

    public MachineRecipe(Item input, Item output) {
        this.input = input;
        this.output = output;
    }
}
