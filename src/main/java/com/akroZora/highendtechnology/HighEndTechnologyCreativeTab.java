package com.akroZora.highendtechnology;

import com.akroZora.highendtechnology.registration.HighEndTechnologyItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class HighEndTechnologyCreativeTab extends CreativeModeTab{

    public HighEndTechnologyCreativeTab(){
        super(HighEndTechnology.MOD_ID);
    }

    @Override
    public ItemStack makeIcon() {
        return HighEndTechnologyItems.ENDERIUM_INGOT.get().getDefaultInstance();
    }
}
