package com.akroZora.highendtechnology.screen.menu;

import com.akroZora.highendtechnology.registration.HighEndTechnologyContainerTypes;
import com.akroZora.highendtechnology.tile.custom.TileEntityStorageCrate;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class StorageCrateContainer extends MekanismTileContainer<TileEntityStorageCrate> {
    public StorageCrateContainer(ContainerTypeRegistryObject<?> type, int id, Inventory inv, @NotNull TileEntityStorageCrate tileEntityStorageCrate) {
        super(HighEndTechnologyContainerTypes.STORAGE_CRATE, id, inv, tileEntityStorageCrate);
    }

    @Override
    protected int getInventoryYOffset() {
        return 48;
    }
}
