package com.akroZora.highendtechnology.world.feature;

import com.akroZora.highendtechnology.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

import java.util.List;

public class ModConfiguredFeatures {

    public static final List<OreConfiguration.TargetBlockState> ENDERIUM_ORES = List.of(
            OreConfiguration.target(new BlockMatchTest(Blocks.END_STONE), ModBlocks.ENDERIUM_ORE.get().defaultBlockState()));

    public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ENDERIUM_ORE = FeatureUtils.register("enderium_ore",
            Feature.ORE, new OreConfiguration(ENDERIUM_ORES,30));

}
