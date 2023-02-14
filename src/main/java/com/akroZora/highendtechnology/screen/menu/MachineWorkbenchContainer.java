package com.akroZora.highendtechnology.screen.menu;

import com.akroZora.highendtechnology.registration.HighEndTechnologyContainerTypes;
import com.akroZora.highendtechnology.screen.slot.CraftingOutputSlot;
import com.akroZora.highendtechnology.tile.custom.TileEntityMachineWorkbench;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.container.SelectedWindowData;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.IInsertableSlot;
import mekanism.common.inventory.container.slot.InventoryContainerSlot;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MachineWorkbenchContainer extends MekanismTileContainer<TileEntityMachineWorkbench> {
    public MachineWorkbenchContainer(int id, Inventory inv, @NotNull TileEntityMachineWorkbench tileEntityMachineWorkbench) {
        super(HighEndTechnologyContainerTypes.MACHINE_WORKBENCH, id, inv, tileEntityMachineWorkbench);
    }

    @Override
    protected int getInventoryYOffset() {
        return 132;
    }
}
