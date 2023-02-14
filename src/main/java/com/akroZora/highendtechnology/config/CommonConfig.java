package com.akroZora.highendtechnology.config;

import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedIntValue;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class CommonConfig extends BaseMekanismConfig {
    private final ForgeConfigSpec configSpec;
    public final CachedIntValue enderiumSwordDamage;

    public CommonConfig(){
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("High End Technology Common Config. This config is not sync'd between server and client.").push("common");
        this.enderiumSwordDamage = CachedIntValue.wrap(this, builder.defineInRange("Enderium Sword Damage:",9,0,1000));
        builder.pop();


        this.configSpec = builder.build();
    }


    @Override
    public String getFileName() {
        return "common";
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return this.configSpec;
    }

    @Override
    public ModConfig.Type getConfigType() {
        return ModConfig.Type.COMMON;
    }
}
