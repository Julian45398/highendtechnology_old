package com.akroZora.highendtechnology.world.feature;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModPlacedFeatures {
    public static final Holder<PlacedFeature> ENDERIUM_ORE_PLACED = PlacementUtils.register("enderium_ore_placed",
            ModConfiguredFeatures.ENDERIUM_ORE,ModOrePlacement.commonOrePlacement(40, HeightRangePlacement.uniform(
                    VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(384))));
}
