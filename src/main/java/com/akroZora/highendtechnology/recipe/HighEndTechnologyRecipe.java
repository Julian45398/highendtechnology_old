package com.akroZora.highendtechnology.recipe;

import mekanism.api.inventory.IgnoredIInventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class HighEndTechnologyRecipe implements Recipe<IgnoredIInventory> {
    private final ResourceLocation id;

    protected HighEndTechnologyRecipe(ResourceLocation id) {
        this.id = (ResourceLocation) Objects.requireNonNull(id, "Recipe name cannot be null.");
    }

    public abstract void write(FriendlyByteBuf var1);

    @Nonnull
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }

    public boolean matches(@Nonnull IgnoredIInventory inv, @Nonnull Level world) {
        return !this.isIncomplete();
    }

    public boolean isSpecial() {
        return true;
    }

    public abstract boolean isIncomplete();

    @Nonnull
    public ItemStack assemble(@Nonnull IgnoredIInventory inv) {
        return ItemStack.EMPTY;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Nonnull
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }
}
