package com.akroZora.highendtechnology.screen;

import com.akroZora.highendtechnology.block.ModBlocks;
import com.akroZora.highendtechnology.tile.custom.AssemblyStationBlockEntity;
import com.akroZora.highendtechnology.screen.slot.ModResultSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class AssemblyStationMenu extends AbstractContainerMenu {
    private final AssemblyStationBlockEntity blockEntity;
    private final Level level;

    public AssemblyStationMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public AssemblyStationMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.ASSEMBLY_STATION_MENU.get(), pContainerId);
        checkContainerSize(inv, AssemblyStationBlockEntity.totalSlots);
        blockEntity = ((AssemblyStationBlockEntity) entity);
        this.level = inv.player.level;
        //Matrix Slots index 0-8 absolute index 0-8
        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,1).ifPresent(handler -> {
            for (int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    this.addSlot(new SlotItemHandler(handler, 3*i+j, 17+18*j, 18*i+17));
                }
            }
        });
        //Output Slot index 0 absolute index 9
        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,2).ifPresent(handler -> {
            this.addSlot(new ModResultSlot(handler,0, 135, 35));
        });
        //Assembly Slot index 0-5 absolute index 10-15
        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,3).ifPresent(handler -> {
            //Assembly Item Slots index 0-2 absolute index 10-12
            for (int i = 0; i<AssemblyStationBlockEntity.assemblyItemSlots; i++){
                this.addSlot(new SlotItemHandler(handler, i, 77, 17+18*i));
            }
        });
        //Storage Slot indexes 0-8 absolute index 16-24
        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,4).ifPresent(handler -> {
            for (int i = 0; i<AssemblyStationBlockEntity.storageSlots; i++){
                this.addSlot(new SlotItemHandler(handler, i, 18*i+8, 84));
            }
        });
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }


    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots and the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = AssemblyStationBlockEntity.totalSlots;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = AssemblyStationBlockEntity.totalSlots-AssemblyStationBlockEntity.storageSlots;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = AssemblyStationBlockEntity.totalSlots;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index>=VANILLA_FIRST_SLOT_INDEX&& TE_INVENTORY_SLOT_COUNT + VANILLA_SLOT_COUNT>= index) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX,TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if(TE_INVENTORY_SLOT_COUNT>index){
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.ASSEMBLY_STATION.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 120 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 178));
        }
    }
}
