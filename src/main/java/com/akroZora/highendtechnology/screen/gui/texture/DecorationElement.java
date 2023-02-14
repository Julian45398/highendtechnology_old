package com.akroZora.highendtechnology.screen.gui.texture;

import com.akroZora.highendtechnology.HighEndTechnology;
import net.minecraft.resources.ResourceLocation;

public enum DecorationElement {

    CRAFTING_ARROW("crafting_arrow",22,15);

    private final ResourceLocation texture;
    private final int width;
    private final int height;

    private DecorationElement(String texture, int width, int height){
        this.texture = new ResourceLocation(HighEndTechnology.MOD_ID,"textures/gui/elements/"+texture+".png");
        this.width = width;
        this.height = height;
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
}
