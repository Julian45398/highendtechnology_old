package com.akroZora.highendtechnology.registration;

import com.akroZora.highendtechnology.config.CommonConfig;
import com.akroZora.highendtechnology.config.GearConfig;
import com.akroZora.highendtechnology.config.HighEndTechnologyConfigHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;

public class HighEndTechnologyConfig {
    public static final CommonConfig commonConfig = new CommonConfig();
    public static final GearConfig gearConfig = new GearConfig();

    public static void registerConfigs(ModLoadingContext modLoadingContext) {
        ModContainer modContainer = modLoadingContext.getActiveContainer();
        HighEndTechnologyConfigHelper.registerConfig(modContainer,commonConfig);
        HighEndTechnologyConfigHelper.registerConfig(modContainer,gearConfig);
    }
}
