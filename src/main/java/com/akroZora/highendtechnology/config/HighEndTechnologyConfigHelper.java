package com.akroZora.highendtechnology.config;

import mekanism.common.config.IMekanismConfig;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class HighEndTechnologyConfigHelper {
    public static final Path CONFIG_DIRECTORY;

    public static void registerConfig(ModContainer modContainer, IMekanismConfig config) {
        HETModConfig modConfig = new HETModConfig(modContainer, config);
        if (config.addToContainer()) {
            modContainer.addConfig(modConfig);
        }

    }


    static {
        CONFIG_DIRECTORY = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve("High End Technology"), "High End Technology");
    }
}
