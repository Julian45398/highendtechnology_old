package com.akroZora.highendtechnology.event;

import com.akroZora.highendtechnology.HighEndTechnology;
import com.akroZora.highendtechnology.recipe.AssemblyStationRecipe;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HighEndTechnology.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvent {


    @SubscribeEvent
    public static void registerRecipeTypes(final RegistryEvent.Register<RecipeSerializer<?>> event) {
        Registry.register(Registry.RECIPE_TYPE, AssemblyStationRecipe.Type.ID,AssemblyStationRecipe.Type.INSTANCE);
    }
}
