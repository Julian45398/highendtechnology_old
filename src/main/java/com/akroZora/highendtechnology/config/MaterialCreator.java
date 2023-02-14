package com.akroZora.highendtechnology.config;

import mekanism.common.config.IMekanismConfig;
import mekanism.common.config.value.CachedFloatValue;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MaterialCreator extends BaseMaterial{


    //public final CachedFloatValue swordDamage;
    public MaterialCreator(IMekanismConfig config, ForgeConfigSpec.Builder builder, BaseMaterial materialDefaults) {
        builder.comment("MaterialCreation").push("HELLO");
    }

    @Nullable
    @Override
    public TagKey<Block> getTag() {
        return null;
    }

    @Override
    public int getShieldDurability() {
        return 0;
    }

    @Override
    public float getAxeDamage() {
        return 0;
    }

    @Override
    public float getAxeAtkSpeed() {
        return 0;
    }

    @NotNull
    @Override
    public String getRegistryPrefix() {
        return null;
    }

    @Override
    public int getCommonEnchantability() {
        return 0;
    }

    @NotNull
    @Override
    public Ingredient getCommonRepairMaterial() {
        return null;
    }
}
