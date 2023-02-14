package com.akroZora.highendtechnology.screen.gui;

import com.akroZora.highendtechnology.screen.gui.elements.GuiSlotHET;
import com.akroZora.highendtechnology.screen.gui.texture.DecorationElement;
import com.akroZora.highendtechnology.screen.gui.texture.SlotHET;
import com.akroZora.highendtechnology.tile.custom.TileEntityMachineWorkbench;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiTextureOnlyElement;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class GuiMachineWorkbench extends GuiMekanismTile<TileEntityMachineWorkbench, MekanismTileContainer<TileEntityMachineWorkbench>> {
    public GuiMachineWorkbench(MekanismTileContainer<TileEntityMachineWorkbench> container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageHeight += 48;
        this.inventoryLabelY = this.imageHeight - 94;
        this.dynamicSlots = true;
    }


    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        this.addRenderableWidget(new GuiSlotHET(SlotHET.OUTPUT_SLOT_CRAFTING, this, 135, 30));
        this.addRenderableWidget(new GuiTextureOnlyElement(DecorationElement.CRAFTING_ARROW.getTexture(), this, 104, 37,22,15));
    }

    @Override
    protected void drawForegroundText(@Nonnull PoseStack matrix, int mouseX, int mouseY) {
        this.renderTitleText(matrix);
        this.drawString(matrix, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, this.titleTextColor());
        super.drawForegroundText(matrix, mouseX, mouseY);
    }
}
