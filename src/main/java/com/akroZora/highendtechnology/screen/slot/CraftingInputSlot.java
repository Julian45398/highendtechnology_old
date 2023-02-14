package com.akroZora.highendtechnology.screen.slot;

import com.akroZora.highendtechnology.tile.CraftingTile;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NonNull;
import mekanism.common.inventory.slot.BasicInventorySlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class CraftingInputSlot extends BasicInventorySlot {
    private final IContentsListener iContentsListener;

    public static CraftingInputSlot at(IContentsListener listener, int x, int y){
        return new CraftingInputSlot(alwaysTrue,alwaysTrue,alwaysTrue,listener,x,y);
    }

    protected CraftingInputSlot(Predicate<@NonNull ItemStack> canExtract, Predicate<@NonNull ItemStack> canInsert, Predicate<@NonNull ItemStack> validator, @Nullable IContentsListener listener, int x, int y) {
        super(canExtract, canInsert, validator, listener, x, y);
        this.iContentsListener = listener;
    }

    @Override
    public void onContentsChanged() {
        super.onContentsChanged();
        System.out.println("CraftingSlotChanged");
        IContentsListener listener = this.iContentsListener;
        if(listener==null){
            System.out.println("listener is null");
        } else if (listener instanceof CraftingTile) {
            System.out.println("Instaceof CraftingTile");
            ((CraftingTile) listener).onGridSlotsChanged();
        }
    }


}
