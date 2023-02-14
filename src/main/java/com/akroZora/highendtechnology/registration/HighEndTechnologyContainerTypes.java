package com.akroZora.highendtechnology.registration;

import com.akroZora.highendtechnology.HighEndTechnology;
import com.akroZora.highendtechnology.registration.HighendtechnologyBlocks;
import com.akroZora.highendtechnology.screen.menu.MachineAssemblicatorContainer;
import com.akroZora.highendtechnology.screen.menu.MachineWorkbenchContainer;
import com.akroZora.highendtechnology.tile.custom.MachineAssemblicatorBlockEntity;
import com.akroZora.highendtechnology.tile.custom.TileEntityMachineWorkbench;
import com.akroZora.highendtechnology.tile.custom.TileEntityStorageCrate;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;

public class HighEndTechnologyContainerTypes {
    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(HighEndTechnology.MOD_ID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityStorageCrate>> STORAGE_CRATE;
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityMachineWorkbench>> MACHINE_WORKBENCH;
    public static final ContainerTypeRegistryObject<MekanismTileContainer<MachineAssemblicatorBlockEntity>> MACHINE_ASSEMBLICATOR;


    private HighEndTechnologyContainerTypes() {
    }


    static {
        STORAGE_CRATE = CONTAINER_TYPES.custom("storage_crate_container", TileEntityStorageCrate.class).offset(0, 20).build();
        MACHINE_WORKBENCH = CONTAINER_TYPES.register(HighendtechnologyBlocks.MACHINE_WORKBENCH, TileEntityMachineWorkbench.class, MachineWorkbenchContainer::new);
        MACHINE_ASSEMBLICATOR = CONTAINER_TYPES.register(HighendtechnologyBlocks.MACHINE_ASSEMBLICATOR, MachineAssemblicatorBlockEntity.class, MachineAssemblicatorContainer::new);
    }
}
