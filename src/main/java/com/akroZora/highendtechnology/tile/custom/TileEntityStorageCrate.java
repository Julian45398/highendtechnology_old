package com.akroZora.highendtechnology.tile.custom;

import com.akroZora.highendtechnology.registration.HighendtechnologyBlocks;
import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TileEntityStorageCrate extends TileEntityMekanism {
    private List<IInventorySlot> storageSlots;
    public TileEntityStorageCrate(BlockPos pos, BlockState state) {
        super(HighendtechnologyBlocks.STORAGE_CRATE, pos, state);
    }

    @Nullable
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        this.storageSlots = new ArrayList();
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);

        for(int slotY = 0; slotY < 4; ++slotY) {
            for(int slotX = 0; slotX < 9; ++slotX) {
                IInventorySlot storageSlot = BasicInventorySlot.at(listener, 8 + slotX * 18, 18 + slotY * 18);
                builder.addSlot(storageSlot);
                this.storageSlots.add(storageSlot);
            }
        }

        return builder.build();
    }

    protected ResourceLocation getStat() {
        return Stats.OPEN_CHEST;
    }
    public InteractionResult openGui(Player player) {
        InteractionResult result = super.openGui(player);

        if (result.consumesAction() && !this.isRemote()) {
            player.awardStat(Stats.CUSTOM.get(this.getStat()));
            PiglinAi.angerNearbyPiglins(player, true);
        }

        return result;
    }

    public void drops(){
        int storageSize = 4*9;
        SimpleContainer inventory = new SimpleContainer(storageSize);
        for (int i = 0; i < storageSize; i++) {
            inventory.setItem(i, storageSlots.get(i).getStack());
        }
        if(this.hasLevel()) {
            Containers.dropContents(this.level, this.worldPosition, inventory);
        }
    }
}
