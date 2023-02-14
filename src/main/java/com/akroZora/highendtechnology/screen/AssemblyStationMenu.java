package com.akroZora.highendtechnology.screen;

import com.akroZora.highendtechnology.registration.HighendtechnologyBlocks;
import com.akroZora.highendtechnology.recipe.AssemblyContainer;
import com.akroZora.highendtechnology.recipe.AssemblyStationRecipe;
import com.akroZora.highendtechnology.screen.slot.AssemblyStationResultSlot;
import com.akroZora.highendtechnology.tile.custom.AssemblyStationBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class AssemblyStationMenu extends AbstractContainerMenu {
    private final AssemblyStationBlockEntity blockEntity;
    private final Level level;
    private final Player player;
    private final AssemblyContainer craftingContainer;
    private final ResultContainer resultContainer = new ResultContainer();
    private final SimpleContainer storageContainer;
    private boolean doUpdate = true;


    public AssemblyStationMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public AssemblyStationMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.ASSEMBLY_STATION_MENU.get(), pContainerId);
        checkContainerSize(inv, AssemblyStationBlockEntity.totalSlots);

        this.blockEntity = ((AssemblyStationBlockEntity) entity);
        this.level = inv.player.level;
        this.player = inv.player;
        this.storageContainer = new SimpleContainer(9);
        this.craftingContainer = new AssemblyContainer(this,3);
        //Output Slot
        this.addSlot(new AssemblyStationResultSlot(player,this,0, 135, 35));
        //Grid Slots
        for (int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                int index = 3*i+j;
                this.addSlot(new Slot(this.craftingContainer, index, 17+18*j, 18*i+17));
                this.craftingContainer.setItem(index,this.blockEntity.assemblyItemHandler.getStackInSlot(index));
            }
        }
        //Assembly Slots
        for (int i = 0; i<3; i++){
            int index = i+9;
            this.addSlot(new Slot(this.craftingContainer, index, 77, 17+18*i));
            this.craftingContainer.setItem(index,this.blockEntity.assemblyItemHandler.getStackInSlot(index));
        }
        //Storage Slots
        for (int i = 0; i<AssemblyStationBlockEntity.storageSlots; i++){
            this.addSlot(new Slot(this.storageContainer, i, 18*i+8, 84));
            this.storageContainer.setItem(i,this.blockEntity.storageItemHandler.getStackInSlot(i));
        }
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    protected static void slotChangedCraftingGrid(AbstractContainerMenu pMenu, Level pLevel, Player pPlayer, CraftingContainer pContainer, ResultContainer pResult) {
        if (!pLevel.isClientSide) {
            ServerPlayer serverplayer = (ServerPlayer)pPlayer;
            ItemStack itemStack;
            if(hasAssemblyItems(pContainer)){
                itemStack = checkAssemblyRecipe(pLevel,pContainer,pResult,serverplayer);
            }else{
                itemStack = checkCraftingRecipe(pLevel,pContainer,pResult,serverplayer);
            }
            pResult.setItem(0, itemStack);
            pMenu.setRemoteSlot(0, itemStack);
            serverplayer.connection.send(new ClientboundContainerSetSlotPacket(pMenu.containerId, pMenu.incrementStateId(), 0, itemStack));
        }
    }
    protected static boolean hasAssemblyItems(CraftingContainer pContainer){
        for (int i = 9; i < 12; i++) {
            if(pContainer.getItem(i)!=ItemStack.EMPTY){
                return true;
            }
        }
        return false;
    }
    protected static ItemStack checkAssemblyRecipe(Level pLevel,CraftingContainer pContainer,ResultContainer pResult,ServerPlayer serverplayer){
        Optional<AssemblyStationRecipe> optional = pLevel.getServer().getRecipeManager().getRecipeFor(AssemblyStationRecipe.Type.INSTANCE, pContainer, pLevel);
        if (optional.isPresent()) {
            AssemblyStationRecipe assemblyRecipe = optional.get();
            if (pResult.setRecipeUsed(pLevel, serverplayer, assemblyRecipe)) {
                return assemblyRecipe.assemble(pContainer);
            }
        }
        return ItemStack.EMPTY;
    }
    protected static ItemStack checkCraftingRecipe(Level pLevel,CraftingContainer pContainer,ResultContainer pResult,ServerPlayer serverplayer){
        Optional<CraftingRecipe> optional = pLevel.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, pContainer, pLevel);
        if (optional.isPresent()) {
            CraftingRecipe craftingrecipe = optional.get();
            if (pResult.setRecipeUsed(pLevel, serverplayer, craftingrecipe)) {
                return craftingrecipe.assemble(pContainer);
            }
        }
        return ItemStack.EMPTY;
    }

    public void setDoUpdate(boolean doUpdate) {
        this.doUpdate = doUpdate;
    }
    public boolean getDoUpdate() {
        return this.doUpdate;
    }


    public void slotsChanged(Container pInventory) {
        if(pInventory==this.storageContainer){
            return;
        }
        if(this.doUpdate){
            slotChangedCraftingGrid(this, this.level, this.player, this.craftingContainer, this.resultContainer);
        }
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        for (int i = 0; i < 12; i++) {
            this.blockEntity.assemblyItemHandler.setStackInSlot(i,this.craftingContainer.getItem(i));
        }
        for (int i = 0; i < 9; i++) {
            this.blockEntity.storageItemHandler.setStackInSlot(i,this.storageContainer.getItem(i));
        }
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return pSlot.container != this.resultContainer && super.canTakeItemForPickAll(pStack, pSlot);
    }
    /**
     * Returns the Crafting Container
     */
    public CraftingContainer getCraftingContainer() {
        return this.craftingContainer;
    }
    /**
     * Returns the Result Container
     */
    public ResultContainer getResultContainer() {
        return this.resultContainer;
    }
    /**
     * Returns the Storage Container
     */
    public SimpleContainer getStorageContainer() {
        return this.storageContainer;
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
    private static final int VANILLA_FIRST_SLOT_INDEX = 22;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 13;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 22;  // must be the number of slots you have!

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
                pPlayer, HighendtechnologyBlocks.ASSEMBLY_STATION.getBlock());
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
