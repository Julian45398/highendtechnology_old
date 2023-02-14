package com.akroZora.highendtechnology.registration;

import com.akroZora.highendtechnology.HighEndTechnology;
import com.akroZora.highendtechnology.tile.custom.MachineAssemblicatorBlockEntity;
import com.akroZora.highendtechnology.tile.custom.TileEntityMachineWorkbench;
import com.akroZora.highendtechnology.tile.custom.TileEntityStorageCrate;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;

public class HighendtechnologyTileEntityTypes {
    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(HighEndTechnology.MOD_ID);



    public static final TileEntityTypeRegistryObject<TileEntityStorageCrate> STORAGE_CRATE;

    public static final TileEntityTypeRegistryObject<TileEntityMachineWorkbench> MACHINE_WORKBENCH;
    public static final TileEntityTypeRegistryObject<MachineAssemblicatorBlockEntity> MACHINE_ASSEMBLICATOR;


    private HighendtechnologyTileEntityTypes() {
    }

    static {
        STORAGE_CRATE = TILE_ENTITY_TYPES.register(HighendtechnologyBlocks.STORAGE_CRATE, TileEntityStorageCrate::new);
        MACHINE_WORKBENCH = TILE_ENTITY_TYPES.register(HighendtechnologyBlocks.MACHINE_WORKBENCH, TileEntityMachineWorkbench::new);
        MACHINE_ASSEMBLICATOR = TILE_ENTITY_TYPES.register(HighendtechnologyBlocks.MACHINE_ASSEMBLICATOR, MachineAssemblicatorBlockEntity::new);
    }
}
