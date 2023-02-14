package com.akroZora.highendtechnology.screen.menu;

import com.akroZora.highendtechnology.registration.HighEndTechnologyContainerTypes;
import com.akroZora.highendtechnology.tile.custom.MachineAssemblicatorBlockEntity;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import net.minecraft.world.entity.player.Inventory;

public class MachineAssemblicatorContainer extends MekanismTileContainer<MachineAssemblicatorBlockEntity> {
    public MachineAssemblicatorContainer(int id, Inventory inv, MachineAssemblicatorBlockEntity tile) {
        super(HighEndTechnologyContainerTypes.MACHINE_ASSEMBLICATOR, id, inv, tile);
    }

    @Override
    protected int getInventoryYOffset() {
        return 148;
    }
}
