package com.akroZora.highendtechnology.tile.custom;

import com.akroZora.highendtechnology.screen.AssemblyStationMenu;
import com.akroZora.highendtechnology.tile.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AssemblyStationBlockEntity extends BlockEntity implements MenuProvider, RecipeHolder {

    public static final int assemblyItemSlots = 12;
    public static final int storageSlots = 9;
    public static final int totalSlots = storageSlots+assemblyItemSlots;

    public final ItemStackHandler assemblyItemHandler = new ItemStackHandler(assemblyItemSlots) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public final ItemStackHandler storageItemHandler = new ItemStackHandler(storageSlots) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };



    public LazyOptional<IItemHandlerModifiable> assemblyHandler = LazyOptional.empty();
    public LazyOptional<IItemHandlerModifiable> storageHandler = LazyOptional.empty();

    @Override
    public void onLoad() {
        super.onLoad();
        assemblyHandler = LazyOptional.of(() -> assemblyItemHandler);
        storageHandler = LazyOptional.of(() -> storageItemHandler);
    }

    public AssemblyStationBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.ASSEMBLY_STATION_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Assembly Station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        assert this.level != null;
        return new AssemblyStationMenu(pContainerId, pInventory, this);
    }

    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("AssemblyInventory", assemblyItemHandler.serializeNBT());
        tag.put("StorageInventory", storageItemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        assemblyItemHandler.deserializeNBT(nbt.getCompound("AssemblyInventory"));
        storageItemHandler.deserializeNBT(nbt.getCompound("StorageInventory"));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(totalSlots);
        for (int i = 0; i < assemblyItemHandler.getSlots(); i++) {
            inventory.setItem(i, assemblyItemHandler.getStackInSlot(i));
        }
        for (int i = 0; i < storageItemHandler.getSlots(); i++) {
            inventory.setItem(i+ assemblyItemHandler.getSlots(), storageItemHandler.getStackInSlot(i));
        }
        if(this.hasLevel()) {
            Containers.dropContents(this.level, this.worldPosition, inventory);
        }

    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> pRecipe) {

    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }
}
