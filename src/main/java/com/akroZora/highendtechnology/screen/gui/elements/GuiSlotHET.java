package com.akroZora.highendtechnology.screen.gui.elements;

import com.akroZora.highendtechnology.screen.gui.texture.DecorationElement;
import com.akroZora.highendtechnology.screen.gui.texture.SlotHET;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiTexturedElement;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.jei.interfaces.IJEIGhostTarget;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.warning.ISupportsWarning;
import mekanism.common.inventory.warning.WarningTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class GuiSlotHET extends GuiTexturedElement implements IJEIGhostTarget,ISupportsWarning<GuiSlotHET> {

    private static final int INVALID_SLOT_COLOR = MekanismRenderer.getColorARGB(EnumColor.DARK_RED, 0.8F);
    public static final int DEFAULT_HOVER_COLOR = 0x80FFFFFF;
    private final SlotHET slotType;
    private Supplier<ItemStack> validityCheck;
    private Supplier<ItemStack> storedStackSupplier;
    private Supplier<SlotOverlay> overlaySupplier;
    @javax.annotation.Nullable
    private BooleanSupplier warningSupplier;
    @javax.annotation.Nullable
    private IntSupplier overlayColorSupplier;
    @javax.annotation.Nullable
    private SlotOverlay overlay;
    @javax.annotation.Nullable
    private IHoverable onHover;
    @javax.annotation.Nullable
    private IClickable onClick;
    private boolean renderHover;
    private boolean renderAboveSlots;

    @javax.annotation.Nullable
    private IGhostIngredientConsumer ghostHandler;

    public GuiSlotHET(SlotHET type, IGuiWrapper gui, int x, int y) {
        super(type.getTexture(), gui, x, y, type.getWidth(), type.getHeight());
        this.slotType = type;
        active = false;
    }

    public GuiSlotHET validity(Supplier<ItemStack> validityCheck) {
        //TODO - 1.18: Evaluate if any of these validity things should be moved to the warning system
        this.validityCheck = validityCheck;
        return this;
    }

    @Override
    public GuiSlotHET warning(@Nonnull WarningTracker.WarningType type, @Nonnull BooleanSupplier warningSupplier) {
        this.warningSupplier = ISupportsWarning.compound(this.warningSupplier, gui().trackWarning(type, warningSupplier));
        return this;
    }

    /**
     * @apiNote For use when there is no validity check and this is a "fake" slot in that the container screen doesn't render the item by default.
     */
    public GuiSlotHET stored(Supplier<ItemStack> storedStackSupplier) {
        this.storedStackSupplier = storedStackSupplier;
        return this;
    }

    public GuiSlotHET hover(IHoverable onHover) {
        this.onHover = onHover;
        return this;
    }

    public GuiSlotHET click(IClickable onClick) {
        this.onClick = onClick;
        return this;
    }

    public GuiSlotHET with(SlotOverlay overlay) {
        this.overlay = overlay;
        return this;
    }

    public GuiSlotHET overlayColor(IntSupplier colorSupplier) {
        overlayColorSupplier = colorSupplier;
        return this;
    }

    public GuiSlotHET with(Supplier<SlotOverlay> overlaySupplier) {
        this.overlaySupplier = overlaySupplier;
        return this;
    }

    public GuiSlotHET setRenderHover(boolean renderHover) {
        this.renderHover = renderHover;
        return this;
    }

    public GuiSlotHET setGhostHandler(@javax.annotation.Nullable IGhostIngredientConsumer ghostHandler) {
        this.ghostHandler = ghostHandler;
        return this;
    }

    public GuiSlotHET setRenderAboveSlots() {
        this.renderAboveSlots = true;
        return this;
    }

    @Override
    public void renderButton(@Nonnull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (!renderAboveSlots) {
            draw(matrix);
        }
    }

    @Override
    public void drawBackground(@Nonnull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (renderAboveSlots) {
            draw(matrix);
        }
    }

    private void draw(@Nonnull PoseStack matrix) {
        if (warningSupplier != null && warningSupplier.getAsBoolean()) {
            RenderSystem.setShaderTexture(0, slotType.getWarningTexture());
        } else {
            RenderSystem.setShaderTexture(0, getResource());
        }
        blit(matrix, x, y, 0, 0, width, height, width, height);
        if (overlaySupplier != null) {
            overlay = overlaySupplier.get();
        }
        if (overlay != null) {
            RenderSystem.setShaderTexture(0, overlay.getTexture());
            blit(matrix, x, y, 0, 0, overlay.getWidth(), overlay.getHeight(), overlay.getWidth(), overlay.getHeight());
        }
        drawContents(matrix);
    }

    protected void drawContents(@Nonnull PoseStack matrix) {
        if (validityCheck != null) {
            ItemStack invalid = validityCheck.get();
            if (!invalid.isEmpty()) {
                int xPos = x + 1;
                int yPos = y + 1;
                fill(matrix, xPos, yPos, xPos + 16, yPos + 16, INVALID_SLOT_COLOR);
                MekanismRenderer.resetColor();
                gui().renderItem(matrix, invalid, xPos, yPos);
            }
        } else if (storedStackSupplier != null) {
            ItemStack stored = storedStackSupplier.get();
            if (!stored.isEmpty()) {
                gui().renderItem(matrix, stored, x + 1, y + 1);
            }
        }
    }

    @Override
    public void renderForeground(PoseStack matrix, int mouseX, int mouseY) {
        if (renderHover && isHoveredOrFocused()) {
            int xPos = relativeX + 1;
            int yPos = relativeY + 1;
            fill(matrix, xPos, yPos, xPos + 16, yPos + 16, DEFAULT_HOVER_COLOR);
            MekanismRenderer.resetColor();
        }
        if (overlayColorSupplier != null) {
            matrix.pushPose();
            matrix.translate(0, 0, 10);
            int xPos = relativeX + 1;
            int yPos = relativeY + 1;
            fill(matrix, xPos, yPos, xPos + 16, yPos + 16, overlayColorSupplier.getAsInt());
            matrix.popPose();
            MekanismRenderer.resetColor();
        }
        if (isHoveredOrFocused()) {
            //TODO: Should it pass it the proper mouseX and mouseY. Probably, though buttons may have to be redone slightly then
            renderToolTip(matrix, mouseX - getGuiLeft(), mouseY - getGuiTop());
        }
    }

    @Override
    public void renderToolTip(@Nonnull PoseStack matrix, int mouseX, int mouseY) {
        super.renderToolTip(matrix, mouseX, mouseY);
        if (onHover != null) {
            onHover.onHover(this, matrix, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (onClick != null && isValidClickButton(button)) {
            if (mouseX >= x + borderSize() && mouseY >= y + borderSize() && mouseX < x + width - borderSize() && mouseY < y + height - borderSize()) {
                onClick.onClick(this, (int) mouseX, (int) mouseY);
                playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Nullable
    @Override
    public IGhostIngredientConsumer getGhostHandler() {
        return ghostHandler;
    }

    @Override
    public int borderSize() {
        return 1;
    }
}
