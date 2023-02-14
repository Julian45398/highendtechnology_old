package com.akroZora.highendtechnology.screen.gui.texture;

import com.akroZora.highendtechnology.HighEndTechnology;
import net.minecraft.resources.ResourceLocation;

public enum SlotHET {
    OUTPUT_SLOT_CRAFTING("output_slot_crafting",26,26);


    private final ResourceLocation texture;
    private final int width;
    private final int height;

    private static final String path = "textures/gui/elements/";


    private ResourceLocation warningTexture;

    private SlotHET(String texture, int width, int height){
        this.texture = new ResourceLocation(HighEndTechnology.MOD_ID,path+texture+".png");
        this.width = width;
        this.height = height;
        setWarningTexture();
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    private void setWarningTexture(){
        this.warningTexture = this.texture;
    }

    public ResourceLocation getWarningTexture() {
        return warningTexture;
    }
}
