package com.akroZora.highendtechnology.block;

import com.akroZora.highendtechnology.registry.HETItemDeferredRegister;
import mekanism.api.MekanismAPI;
import mekanism.api.text.EnumColor;
import mekanism.client.key.MekKeyHandler;
import mekanism.client.key.MekanismKeyHandler;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeHasBounding;
import mekanism.common.block.attribute.AttributeUpgradeSupport;
import mekanism.common.block.attribute.Attributes;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.item.block.ItemBlockMekanism;
import mekanism.common.item.interfaces.IItemSustainedInventory;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.WorldUtils;
import mekanism.common.util.text.BooleanStateDisplay;
import mekanism.common.util.text.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.List;

public class HETItemBlockTooltip<BLOCK extends Block & IHasDescription> extends ItemBlockMekanism<BLOCK> {
    private final boolean hasDetails;

    public HETItemBlockTooltip(BLOCK block, Item.Properties properties) {
        this(block, false, properties);
    }

    public HETItemBlockTooltip(BLOCK block) {
        this(block, false, HETItemDeferredRegister.getHighEndTechnologyBaseProperties().stacksTo(64));
    }

    protected HETItemBlockTooltip(BLOCK block, boolean hasDetails, Item.Properties properties) {
        super(block, properties);
        this.hasDetails = hasDetails;
    }

    public void onDestroyed(@Nonnull ItemEntity item, @Nonnull DamageSource damageSource) {
        InventoryUtils.dropItemContents(item, damageSource);
    }

    public boolean placeBlock(@Nonnull BlockPlaceContext context, @Nonnull BlockState state) {
        AttributeHasBounding hasBounding = (AttributeHasBounding) Attribute.get(state, AttributeHasBounding.class);
        return (hasBounding == null || WorldUtils.areBlocksValidAndReplaceable(context.getLevel(), hasBounding.getPositions(context.getClickedPos(), state))) && super.placeBlock(context, state);
    }

    public void appendHoverText(@Nonnull ItemStack stack, Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        if (MekKeyHandler.isKeyPressed(MekanismKeyHandler.descriptionKey)) {
            tooltip.add(((IHasDescription)this.getBlock()).getDescription().translate(new Object[0]));
        } else if (this.hasDetails && MekKeyHandler.isKeyPressed(MekanismKeyHandler.detailsKey)) {
            this.addDetails(stack, world, tooltip, flag);
        } else {
            this.addStats(stack, world, tooltip, flag);
            if (this.hasDetails) {
                tooltip.add(MekanismLang.HOLD_FOR_DETAILS.translateColored(EnumColor.GRAY, new Object[]{EnumColor.INDIGO, MekanismKeyHandler.detailsKey.getTranslatedKeyMessage()}));
            }

            tooltip.add(MekanismLang.HOLD_FOR_DESCRIPTION.translateColored(EnumColor.GRAY, new Object[]{EnumColor.AQUA, MekanismKeyHandler.descriptionKey.getTranslatedKeyMessage()}));
        }

    }

    protected void addStats(@Nonnull ItemStack stack, Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
    }

    protected void addDetails(@Nonnull ItemStack stack, Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        MekanismAPI.getSecurityUtils().addSecurityTooltip(stack, tooltip);
        this.addTypeDetails(stack, world, tooltip, flag);
        FluidStack fluidStack = StorageUtils.getStoredFluidFromNBT(stack);
        if (!fluidStack.isEmpty()) {
            tooltip.add(MekanismLang.GENERIC_STORED_MB.translateColored(EnumColor.PINK, new Object[]{fluidStack, EnumColor.GRAY, TextUtils.format((long)fluidStack.getAmount())}));
        }

        if (Attribute.has(this.getBlock(), Attributes.AttributeInventory.class)) {
            Item var7 = stack.getItem();
            if (var7 instanceof IItemSustainedInventory) {
                IItemSustainedInventory inventory = (IItemSustainedInventory)var7;
                tooltip.add(MekanismLang.HAS_INVENTORY.translateColored(EnumColor.AQUA, new Object[]{EnumColor.GRAY, BooleanStateDisplay.YesNo.of(inventory.hasInventory(new Object[]{stack}))}));
            }
        }

        if (Attribute.has(this.getBlock(), AttributeUpgradeSupport.class)) {
            MekanismUtils.addUpgradesToTooltip(stack, tooltip);
        }

    }

    protected void addTypeDetails(@Nonnull ItemStack stack, Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        if (this.exposesEnergyCap(stack)) {
            StorageUtils.addStoredEnergy(stack, tooltip, false);
        }

    }
}

