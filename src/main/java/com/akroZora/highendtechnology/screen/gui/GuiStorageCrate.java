package com.akroZora.highendtechnology.screen.gui;

import com.akroZora.highendtechnology.tile.custom.TileEntityStorageCrate;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class GuiStorageCrate extends GuiMekanismTile<TileEntityStorageCrate, MekanismTileContainer<TileEntityStorageCrate>> {
    public GuiStorageCrate(MekanismTileContainer<TileEntityStorageCrate> container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageHeight += 20;
        this.inventoryLabelY = this.imageHeight - 94;
        this.dynamicSlots = true;
    }

    @Override
    protected void drawForegroundText(@Nonnull PoseStack matrix, int mouseX, int mouseY) {
        this.renderTitleText(matrix);
        this.drawString(matrix, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, this.titleTextColor());
        super.drawForegroundText(matrix, mouseX, mouseY);
    }
}