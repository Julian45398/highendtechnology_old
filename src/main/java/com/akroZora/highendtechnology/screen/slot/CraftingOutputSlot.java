package com.akroZora.highendtechnology.screen.slot;

import com.akroZora.highendtechnology.tile.CraftingTile;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NonNull;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.InventoryContainerSlot;
import mekanism.common.inventory.slot.BasicInventorySlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class CraftingOutputSlot extends BasicInventorySlot {

    private final IContentsListener iContentsListener;
    private int x;
    private int y;


    public static CraftingOutputSlot at(IContentsListener listener, int x, int y){
        return new CraftingOutputSlot(alwaysTrue,alwaysFalse,alwaysTrue,listener,x,y);
    }

    protected CraftingOutputSlot(Predicate<@NonNull ItemStack> canExtract, Predicate<@NonNull ItemStack> canInsert, Predicate<@NonNull ItemStack> validator, @Nullable IContentsListener listener, int x, int y) {
        super(canExtract, canInsert, validator, listener, x, y);
        this.iContentsListener = listener;
        this.x = x;
        this.y = y;
        this.setSlotType(ContainerSlotType.OUTPUT);
    }


    @Override
    public boolean isItemValidForInsertion(ItemStack stack, AutomationType automationType) {
        return false;
    }

    @Override
    public void onContentsChanged() {
        super.onContentsChanged();
        System.out.println("OutputSlot Changed");
        IContentsListener listener = this.iContentsListener;
        if(listener==null){
            return;
        } else if (listener instanceof CraftingTile) {

            ((CraftingTile) listener).onOutputChanged();
            System.out.println("Instanceof CraftingTile");
        }
    }

    @Nullable
    @Override
    public InventoryContainerSlot createContainerSlot() {
        return new InventoryContainerSlot(this, this.x, this.y, this.getSlotType(), this.getSlotOverlay(), null, this::setStackUnchecked){
            @Override
            public boolean canMergeWith(@NotNull ItemStack stack) {
                return false;
            }
        };
    }
}
