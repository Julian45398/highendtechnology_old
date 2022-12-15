package com.akroZora.highendtechnology.tile.custom;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;


public class HydraulicPressBlockEntity extends TileEntityElectricMachine {

    public HydraulicPressBlockEntity(BlockPos pos, BlockState state) {
        super(MekanismBlocks.ENRICHMENT_CHAMBER, pos, state, 200);
    }

    @NotNull
    @Override
    public IMekanismRecipeTypeProvider<ItemStackToItemStackRecipe, InputRecipeCache.SingleItem<ItemStackToItemStackRecipe>> getRecipeType() {
        return null;
    }


}
