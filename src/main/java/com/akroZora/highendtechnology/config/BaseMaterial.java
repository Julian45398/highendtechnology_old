package com.akroZora.highendtechnology.config;

import com.akroZora.highendtechnology.HighEndTechnology;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BaseMaterial {
    @Nullable

    public abstract TagKey<Block> getTag();//Force this to be implemented

    public abstract int getShieldDurability();

    public float getSwordDamage() {
        return 3;
    }

    public float getSwordAtkSpeed() {
        return -2.4F;
    }

    public float getShovelDamage() {
        return 1.5F;
    }

    public float getShovelAtkSpeed() {
        return -3.0F;
    }

    public abstract float getAxeDamage();

    public abstract float getAxeAtkSpeed();

    public float getPickaxeDamage() {
        return 1;
    }

    public float getPickaxeAtkSpeed() {
        return -2.8F;
    }


    @Nonnull
    public abstract String getRegistryPrefix();

    //Recombine the methods that are split in such a way as to make it so the compiler can reobfuscate them properly
    public abstract int getCommonEnchantability();

    public boolean burnsInFire() {
        return true;
    }

    public int getItemEnchantability() {
        return getCommonEnchantability();
    }

    public int getArmorEnchantability() {
        return getCommonEnchantability();
    }

    @Nonnull
    public abstract Ingredient getCommonRepairMaterial();

    @Nonnull

    public Ingredient getItemRepairMaterial() {
        return getCommonRepairMaterial();
    }

    @Nonnull
    public Ingredient getArmorRepairMaterial() {
        return getCommonRepairMaterial();
    }

    @Nonnull
    public String getName() {
        return HighEndTechnology.MOD_ID + ":" + getRegistryPrefix();
    }

}
