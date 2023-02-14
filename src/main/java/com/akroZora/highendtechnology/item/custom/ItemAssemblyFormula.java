package com.akroZora.highendtechnology.item.custom;

import com.mojang.realmsclient.util.JsonUtils;
import mekanism.api.text.EnumColor;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.MekanismLang;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.ItemDataUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemAssemblyFormula extends Item {
    public ItemAssemblyFormula (Item.Properties properties) {
        super(properties);
    }

    public void appendHoverText(@Nonnull ItemStack itemStack, Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        NonNullList<ItemStack> inv = this.getInventory(itemStack);
        if (inv != null) {
            List<ItemStack> stacks = new ArrayList();
            Iterator nonNulllistIterator = inv.iterator();

            endlessLoop:
            while(true) {
                ItemStack stack;
                do {
                    if (!nonNulllistIterator.hasNext()) {
                        tooltip.add(MekanismLang.INGREDIENTS.translateColored(EnumColor.GRAY, new Object[0]));
                        nonNulllistIterator = stacks.iterator();

                        while(nonNulllistIterator.hasNext()) {
                            stack = (ItemStack)nonNulllistIterator.next();
                            tooltip.add(MekanismLang.GENERIC_TRANSFER.translateColored(EnumColor.GRAY, new Object[]{stack, stack.getCount()}));
                        }
                        break endlessLoop;
                    }

                    stack = (ItemStack)nonNulllistIterator.next();
                } while(stack.isEmpty());

                boolean found = false;
                Iterator listIterator = stacks.iterator();

                while(listIterator.hasNext()) {
                    ItemStack iterStack = (ItemStack)listIterator.next();
                    if (InventoryUtils.areItemsStackable(stack, iterStack)) {
                        iterStack.grow(stack.getCount());
                        found = true;
                    }
                }

                if (!found) {
                    stacks.add(stack);
                }
            }
        }

    }

    @Nonnull
    public InteractionResultHolder<ItemStack> use(@Nonnull Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!world.isClientSide) {
                this.setInventory(stack, (NonNullList)null);
                this.setInvalid(stack, false);
            }

            return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
        } else {
            return InteractionResultHolder.pass(stack);
        }
    }

    public int getItemStackLimit(ItemStack stack) {
        return this.getInventory(stack) == null ? 64 : 1;
    }

    @Nonnull
    public Component getName(@Nonnull ItemStack stack) {
        if (this.getInventory(stack) == null) {
            return super.getName(stack);
        } else {
            return this.isInvalid(stack) ? TextComponentUtil.build(new Object[]{super.getName(stack), " ", EnumColor.DARK_RED, MekanismLang.INVALID}) : TextComponentUtil.build(new Object[]{super.getName(stack), " ", EnumColor.DARK_GREEN, MekanismLang.ENCODED});
        }
    }

    public boolean isInvalid(ItemStack stack) {
        return ItemDataUtils.getBoolean(stack, "invalid");
    }

    public void setInvalid(ItemStack stack, boolean invalid) {
        if(invalid){
            System.out.println("set invalid");
        }else {
            System.out.println("set valid");
        }
        ItemDataUtils.setBoolean(stack, "invalid", invalid);
    }

    public NonNullList<ItemStack> getInventory(ItemStack stack) {
        if (!ItemDataUtils.hasData(stack, "Items", 9)) {
            return null;
        } else {
            ListTag tagList = ItemDataUtils.getList(stack, "Items");
            NonNullList<ItemStack> inventory = NonNullList.withSize(12, ItemStack.EMPTY);

            for(int tagCount = 0; tagCount < tagList.size(); ++tagCount) {
                CompoundTag tagCompound = tagList.getCompound(tagCount);
                byte slotID = tagCompound.getByte("Slot");
                if (slotID >= 0 && slotID < 12) {
                    inventory.set(slotID, ItemStack.of(tagCompound));
                }
            }

            return inventory;
        }
    }

    public void setInventory(ItemStack stack, NonNullList<ItemStack> inv) {
        if (inv == null) {
            ItemDataUtils.removeData(stack, "Items");
        } else {
            ListTag tagList = new ListTag();

            for(int slotCount = 0; slotCount < 12; ++slotCount) {
                ItemStack slotStack = (ItemStack)inv.get(slotCount);
                if (!slotStack.isEmpty()) {
                    CompoundTag tagCompound = new CompoundTag();
                    tagCompound.putByte("Slot", (byte)slotCount);
                    slotStack.save(tagCompound);
                    tagList.add(tagCompound);
                }
            }

            ItemDataUtils.setListOrRemove(stack, "Items", tagList);
        }
    }
}
