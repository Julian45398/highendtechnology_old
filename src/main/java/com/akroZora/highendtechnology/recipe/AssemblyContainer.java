package com.akroZora.highendtechnology.recipe;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class AssemblyContainer extends CraftingContainer {
    private final IItemHandlerModifiable inv;
    public AssemblyContainer(ItemStackHandler stackHandler) {
        super(null, 3, 3);
            this.inv = stackHandler;
    }

    @Override
    public int getContainerSize() {
        return inv.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i < inv.getSlots(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return inv.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack s = getItem(index);
        if(s.isEmpty()) return ItemStack.EMPTY;
        setItem(index, ItemStack.EMPTY);
        return s;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = inv.getStackInSlot(index);
        return stack.isEmpty() ? ItemStack.EMPTY : stack.split(count);
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        inv.setStackInSlot(index, stack);
    }

    @Override
    public void clearContent() {
        for(int i = 0; i < inv.getSlots(); i++) {
            inv.setStackInSlot(i, ItemStack.EMPTY);
        }
    }



}
