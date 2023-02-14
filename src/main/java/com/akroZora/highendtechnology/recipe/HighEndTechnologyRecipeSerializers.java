package com.akroZora.highendtechnology.recipe;

import com.akroZora.highendtechnology.HighEndTechnology;
import mekanism.api.recipes.*;
import mekanism.common.recipe.serializer.*;
import mekanism.common.registration.impl.RecipeSerializerDeferredRegister;
import mekanism.common.registration.impl.RecipeSerializerRegistryObject;

public class HighEndTechnologyRecipeSerializers {
    public static final RecipeSerializerDeferredRegister RECIPE_SERIALIZERS = new RecipeSerializerDeferredRegister(HighEndTechnology.MOD_ID);
    public static final RecipeSerializerRegistryObject<ItemStackToItemStackRecipe> PRESSING;


    private HighEndTechnologyRecipeSerializers() {
    }

    static {
        PRESSING = RECIPE_SERIALIZERS.register("pressing", () -> {
            return new ItemStackToItemStackRecipeSerializer(PressingIRecipe::new);
        });
    }
}
