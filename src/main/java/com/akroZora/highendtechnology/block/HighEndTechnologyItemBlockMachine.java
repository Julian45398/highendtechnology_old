package com.akroZora.highendtechnology.block;

import com.akroZora.highendtechnology.registry.HETItemDeferredRegister;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.item.interfaces.IItemSustainedInventory;

public class HighEndTechnologyItemBlockMachine extends HETItemBlockTooltip<BlockTile<?, ?>> implements IItemSustainedInventory {

    public HighEndTechnologyItemBlockMachine(BlockTile<?, ?> block) {
        super(block, true, HETItemDeferredRegister.getHighEndTechnologyBaseProperties().stacksTo(1));
    }

}

