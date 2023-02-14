package com.akroZora.highendtechnology.integration.jei;

import com.akroZora.highendtechnology.screen.AssemblyStationMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;

import java.util.List;

public class AssemblyStationTransferInfo implements IRecipeTransferInfo<AssemblyStationMenu, CraftingRecipe> {
    @Override
    public Class<AssemblyStationMenu> getContainerClass() {
        return AssemblyStationMenu.class;
    }

    @Override
    public RecipeType<CraftingRecipe> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public boolean canHandle(AssemblyStationMenu container, CraftingRecipe recipe) {
        return false;
    }

    @Override
    public List<Slot> getRecipeSlots(AssemblyStationMenu container, CraftingRecipe recipe) {
        return container.slots.subList(1,9);
    }

    @Override
    public List<Slot> getInventorySlots(AssemblyStationMenu container, CraftingRecipe recipe) {
        return container.slots.subList(10,36);
    }

    @Deprecated(
            forRemoval = true
    )
    public Class<CraftingRecipe> getRecipeClass() {
        return CraftingRecipe.class;
    }

    @Deprecated(
            forRemoval = true
    )
    public ResourceLocation getRecipeCategoryUid() {
        return this.getRecipeType().getUid();
    }
}
