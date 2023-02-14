package com.akroZora.highendtechnology.config;

import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedIntValue;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class GearConfig extends BaseMekanismConfig {
    private final ForgeConfigSpec configSpec;
    public final CachedIntValue enderiumSwordDamage;

    public GearConfig(){
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("High End Technology Gear Config. This config is not sync'd between server and client.").push("gear");
        enderiumSwordDamage = CachedIntValue.wrap(this, builder.define("Enderium Sword Damage:",9));
        builder.pop();

        this.configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "gear";
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return this.configSpec;
    }

    @Override
    public ModConfig.Type getConfigType() {
        return ModConfig.Type.SERVER;
    }

    @Override
    public boolean addToContainer() {
        return false;
    }

    public int getEnderiumSwordDamage() {
        return enderiumSwordDamage.get();
    }
}
