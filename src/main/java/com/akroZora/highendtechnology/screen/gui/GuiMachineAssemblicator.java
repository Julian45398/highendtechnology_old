package com.akroZora.highendtechnology.screen.gui;

import com.akroZora.highendtechnology.HighEndTechnology;
import com.akroZora.highendtechnology.item.custom.ItemAssemblyFormula;
import com.akroZora.highendtechnology.network.ModPacketGuiInteract;
import com.akroZora.highendtechnology.tile.custom.MachineAssemblicatorBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.button.MekanismButton;
import mekanism.client.gui.element.button.MekanismImageButton;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.network.to_server.PacketGuiInteract;
import mekanism.common.util.text.BooleanStateDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class GuiMachineAssemblicator extends GuiConfigurableTile<MachineAssemblicatorBlockEntity, MekanismTileContainer<MachineAssemblicatorBlockEntity>> {
    private MekanismButton encodeFormulaButton;
    private MekanismButton stockControlButton;
    private MekanismButton fillEmptyButton;
    private MekanismButton craftSingleButton;
    private MekanismButton craftAvailableButton;
    private MekanismButton autoModeButton;

    public GuiMachineAssemblicator(MekanismTileContainer<MachineAssemblicatorBlockEntity> container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageHeight += 64;
        this.inventoryLabelY = this.imageHeight - 94;
        this.dynamicSlots = true;
    }

    protected void addGuiElements() {
        super.addGuiElements();
        this.addRenderableWidget(new GuiVerticalPowerBar(this, ((MachineAssemblicatorBlockEntity)this.tile).getEnergyContainer(), 163, 15));
        this.addRenderableWidget(new GuiSlot(SlotType.OUTPUT_LARGE, this, 123, 16));
        this.addRenderableWidget((GuiProgress)(new GuiProgress(() -> {
            return (double)((MachineAssemblicatorBlockEntity)this.tile).getOperatingTicks() / (double)((MachineAssemblicatorBlockEntity)this.tile).getTicksRequired();
        }, ProgressType.TALL_RIGHT, this, 102, 43)).jeiCrafting());
        this.addRenderableWidget(new GuiEnergyTab(this, ((MachineAssemblicatorBlockEntity)this.tile).getEnergyContainer()));
        this.encodeFormulaButton = (MekanismButton)this.addRenderableWidget(new MekanismImageButton(this, 7, 45, 14, this.getButtonLocation("encode_formula"), () -> {
            HighEndTechnology.packetHandler().sendToServer(new ModPacketGuiInteract(ModPacketGuiInteract.ModGuiInteraction.ENCODE_FORMULA, this.tile));
        }, this.getOnHover(MekanismLang.ENCODE_FORMULA)));
        this.stockControlButton = (MekanismButton)this.addRenderableWidget(new MekanismImageButton(this, 26, 75, 16, this.getButtonLocation("stock_control"), () -> {
            HighEndTechnology.packetHandler().sendToServer(new ModPacketGuiInteract(ModPacketGuiInteract.ModGuiInteraction.STOCK_CONTROL_BUTTON, this.tile));
        }, this.getOnHover(() -> {
            return MekanismLang.STOCK_CONTROL.translate(new Object[]{BooleanStateDisplay.OnOff.of(((MachineAssemblicatorBlockEntity)this.tile).getStockControl())});
        })));
        this.fillEmptyButton = (MekanismButton)this.addRenderableWidget(new MekanismImageButton(this, 44, 75, 16, this.getButtonLocation("fill_empty"), () -> {
            HighEndTechnology.packetHandler().sendToServer(new ModPacketGuiInteract(ModPacketGuiInteract.ModGuiInteraction.MOVE_ITEMS, this.tile));
        }, this.getOnHover(MekanismLang.FILL_EMPTY)));
        this.craftSingleButton = (MekanismButton)this.addRenderableWidget(new MekanismImageButton(this, 81, 75, 16, this.getButtonLocation("craft_single"), () -> {
            HighEndTechnology.packetHandler().sendToServer(new ModPacketGuiInteract(ModPacketGuiInteract.ModGuiInteraction.CRAFT_SINGLE, this.tile));
        }));
        this.craftAvailableButton = (MekanismButton)this.addRenderableWidget(new MekanismImageButton(this, 99, 75, 16, this.getButtonLocation("craft_available"), () -> {
            HighEndTechnology.packetHandler().sendToServer(new ModPacketGuiInteract(ModPacketGuiInteract.ModGuiInteraction.CRAFT_ALL, this.tile));
        }, this.getOnHover(MekanismLang.CRAFT_AVAILABLE)));
        this.autoModeButton = (MekanismButton)this.addRenderableWidget(new MekanismImageButton(this, 117, 75, 16, this.getButtonLocation("auto_toggle"), () -> {
            Mekanism.packetHandler().sendToServer(new PacketGuiInteract(PacketGuiInteract.GuiInteraction.NEXT_MODE, this.tile));
        }, this.getOnHover(() -> {
            return MekanismLang.AUTO_MODE.translate(new Object[]{BooleanStateDisplay.OnOff.of(((MachineAssemblicatorBlockEntity)this.tile).getAutoMode())});
        })));
        this.updateEnabledButtons();
    }

    public void containerTick() {
        super.containerTick();
        this.updateEnabledButtons();
    }

    private void updateEnabledButtons() {
        this.encodeFormulaButton.active = !((MachineAssemblicatorBlockEntity)this.tile).getAutoMode() && ((MachineAssemblicatorBlockEntity)this.tile).hasRecipe() && this.canEncode();
        this.stockControlButton.active = ((MachineAssemblicatorBlockEntity)this.tile).recipeFormula != null && ((MachineAssemblicatorBlockEntity)this.tile).recipeFormula.isValidFormula();
        this.fillEmptyButton.active = !((MachineAssemblicatorBlockEntity)this.tile).getAutoMode();
        this.craftSingleButton.active = !((MachineAssemblicatorBlockEntity)this.tile).getAutoMode() && ((MachineAssemblicatorBlockEntity)this.tile).hasRecipe();
        this.craftAvailableButton.active = !((MachineAssemblicatorBlockEntity)this.tile).getAutoMode() && ((MachineAssemblicatorBlockEntity)this.tile).hasRecipe();
        this.autoModeButton.active = ((MachineAssemblicatorBlockEntity)this.tile).recipeFormula != null && ((MachineAssemblicatorBlockEntity)this.tile).recipeFormula.isValidFormula();
    }

    protected void drawForegroundText(@Nonnull PoseStack matrix, int mouseX, int mouseY) {
        this.renderTitleText(matrix);
        this.drawString(matrix, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, this.titleTextColor());
        super.drawForegroundText(matrix, mouseX, mouseY);
    }

    protected ItemStack checkValidity(int slotIndex) {
        int i = slotIndex - 21;
        if (i >= 0 && ((MachineAssemblicatorBlockEntity)this.tile).recipeFormula != null && ((MachineAssemblicatorBlockEntity)this.tile).recipeFormula.isValidFormula()) {
            ItemStack stack = (ItemStack)((MachineAssemblicatorBlockEntity)this.tile).recipeFormula.input.get(i);
            if (!stack.isEmpty()) {
                Slot slot = (Slot)((MekanismTileContainer)this.menu).slots.get(slotIndex);
                if (slot.getItem().isEmpty() || !((MachineAssemblicatorBlockEntity)this.tile).recipeFormula.isIngredientInPos(((MachineAssemblicatorBlockEntity)this.tile).getLevel(), slot.getItem(), i)) {
                    return stack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    protected void renderBg(@Nonnull PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        super.renderBg(matrix, partialTick, mouseX, mouseY);
        SlotOverlay overlay = ((MachineAssemblicatorBlockEntity)this.tile).hasRecipe() ? SlotOverlay.CHECK : SlotOverlay.X;
        RenderSystem.setShaderTexture(0, overlay.getTexture());
        blit(matrix, this.leftPos + 102, this.topPos + 22, 0.0F, 0.0F, overlay.getWidth(), overlay.getHeight(), overlay.getWidth(), overlay.getHeight());
    }

    private boolean canEncode() {
        if ((((MachineAssemblicatorBlockEntity)this.tile).recipeFormula == null || !((MachineAssemblicatorBlockEntity)this.tile).recipeFormula.isValidFormula()) && !((MachineAssemblicatorBlockEntity)this.tile).getFormulaSlot().isEmpty()) {
            ItemStack formulaStack = ((MachineAssemblicatorBlockEntity)this.tile).getFormulaSlot().getStack();
            Item var3 = formulaStack.getItem();
            boolean var10000;
            if (var3 instanceof ItemAssemblyFormula) {
                ItemAssemblyFormula formula = (ItemAssemblyFormula)var3;
                if (formula.getInventory(formulaStack) == null) {
                    var10000 = true;
                    return var10000;
                }
            }

            var10000 = false;
            return var10000;
        } else {
            return false;
        }
    }
}
