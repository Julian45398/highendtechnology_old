package com.akroZora.highendtechnology.recipe;

import com.akroZora.highendtechnology.screen.AssemblyStationMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class AssemblyContainer extends CraftingContainer {
    private final NonNullList<ItemStack> items;

    private final AbstractContainerMenu menu;
    public AssemblyContainer(AbstractContainerMenu menu, int size) {
        super(menu, size, size);
        this.items = NonNullList.withSize(12,ItemStack.EMPTY);
        this.menu = menu;
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack s = getItem(index);
        if(s.isEmpty()) return ItemStack.EMPTY;
        setItem(index, ItemStack.EMPTY);
        return s;
    }

    public int getContainerSize() {
        return this.items.size();
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the stack in the given slot.
     */
    public ItemStack getItem(int pIndex) {
        return pIndex >= this.getContainerSize() ? ItemStack.EMPTY : this.items.get(pIndex);
    }



    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    public ItemStack removeItem(int pIndex, int pCount) {
        ItemStack itemstack = ContainerHelper.removeItem(this.items, pIndex, pCount);
        if (!itemstack.isEmpty()) {
            this.menu.slotsChanged(this);
        }

        return itemstack;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setItem(int pIndex, ItemStack pStack) {
        this.items.set(pIndex, pStack);
        this.menu.slotsChanged(this);
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void setChanged() {
    }

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    public void clearContent() {
        this.items.clear();
    }

    public void fillStackedContents(StackedContents pHelper) {
        for(ItemStack itemstack : this.items) {
            pHelper.accountSimpleStack(itemstack);
        }
    }

    public static AssemblyContainer createDummy(int size){
        AbstractContainerMenu tempContainer = new AbstractContainerMenu(MenuType.CRAFTING, 1) {
            public boolean stillValid(@Nonnull Player player) {
                return false;
            }
        };
        return new AssemblyContainer(tempContainer, size);
    }

}
