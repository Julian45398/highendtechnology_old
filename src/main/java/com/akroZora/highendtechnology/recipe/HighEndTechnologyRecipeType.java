package com.akroZora.highendtechnology.recipe;

import com.akroZora.highendtechnology.HighEndTechnology;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.registration.impl.RecipeTypeDeferredRegister;
import mekanism.common.registration.impl.RecipeTypeRegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class HighEndTechnologyRecipeType {

    public static final RecipeTypeDeferredRegister RECIPE_TYPES = new RecipeTypeDeferredRegister("highendtechnology");

    public static final RecipeTypeRegistryObject<ItemStackToItemStackRecipe, InputRecipeCache.SingleItem<ItemStackToItemStackRecipe>>
            PRESSING = (RecipeTypeRegistryObject<ItemStackToItemStackRecipe, InputRecipeCache.SingleItem<ItemStackToItemStackRecipe>>) register("pressing");


    static <T extends Recipe<?>> RecipeType<T> register(final String pIdentifier) {
        return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(HighEndTechnology.MOD_ID,pIdentifier), new RecipeType<T>() {
            public String toString() {
                return pIdentifier;
            }
        });
    }
    private final ResourceLocation registryName;
    private HighEndTechnologyRecipeType(String name) {
        this.registryName = HighEndTechnology.rl(name);

    }

}