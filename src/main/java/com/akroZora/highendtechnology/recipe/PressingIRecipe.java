package com.akroZora.highendtechnology.recipe;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class PressingIRecipe extends ItemStackToItemStackRecipe {


    public PressingIRecipe(ResourceLocation id, ItemStackIngredient input, ItemStack output) {
        super(id, input, output);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return (RecipeSerializer) HighEndTechnologyRecipeSerializers.PRESSING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return (RecipeType) HighEndTechnologyRecipeType.PRESSING;
    }
}
