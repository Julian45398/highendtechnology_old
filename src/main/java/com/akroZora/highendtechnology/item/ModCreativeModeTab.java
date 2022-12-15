package com.akroZora.highendtechnology.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTab {
    public static final CreativeModeTab HIGH_END_TECHNOLOGY = new CreativeModeTab("highendtechnology") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.ENDERIUM_INGOT.get());
        }
    };
}
