package com.akroZora.highendtechnology.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import mekanism.common.config.IMekanismConfig;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.function.Function;

public class HETModConfig extends ModConfig {
    private static final HETModConfig.HETConfigFileTypeHandler MEK_TOML = new HETModConfig.HETConfigFileTypeHandler();
    private final IMekanismConfig highendtechnologyConfig;

    public HETModConfig(ModContainer container, IMekanismConfig config) {
        super(config.getConfigType(), config.getConfigSpec(), container, "High End Technology/" + config.getFileName() + ".toml");
        this.highendtechnologyConfig = config;
    }

    public ConfigFileTypeHandler getHandler() {
        return MEK_TOML;
    }

    public void clearCache() {
        this.highendtechnologyConfig.clearCache();
    }

    private static class HETConfigFileTypeHandler extends ConfigFileTypeHandler {
        private HETConfigFileTypeHandler() {
        }

        private static Path getPath(Path configBasePath) {
            return configBasePath.endsWith("serverconfig") ? FMLPaths.CONFIGDIR.get() : configBasePath;
        }

        public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
            return super.reader(getPath(configBasePath));
        }

        public void unload(Path configBasePath, ModConfig config) {
            super.unload(getPath(configBasePath), config);
        }
    }
}
