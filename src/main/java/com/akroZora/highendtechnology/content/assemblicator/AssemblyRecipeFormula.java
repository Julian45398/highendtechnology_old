package com.akroZora.highendtechnology.content.assemblicator;

import com.akroZora.highendtechnology.recipe.AssemblyContainer;
import com.akroZora.highendtechnology.recipe.AssemblyStationRecipe;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.util.StackUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class AssemblyRecipeFormula {
        public final NonNullList<ItemStack> input;
        @Nullable
        public AssemblyStationRecipe recipe;
        private final AssemblyContainer dummy;

        public AssemblyRecipeFormula(Level world, NonNullList<ItemStack> inv) {
            this.input = NonNullList.withSize(12, ItemStack.EMPTY);
            this.dummy = AssemblyContainer.createDummy(3);
            System.out.println("new Assembly Recipe Formula");
            for(int i = 0; i < 12; ++i) {
                this.dummy.setItem(i, inv.get(i));
            }
            this.recipe = getRecipeFromGrid(this.dummy, world);
            if(this.recipe != null){
                for(int i = 0; i < 9; ++i) {
                    this.input.set(i, StackUtils.size((ItemStack)inv.get(i), 1));
                }
                for (int i = 0; i < 3; i++) {
                    int index = 9+i;
                    for (int ingredientSlot = 0; ingredientSlot < this.recipe.getAssemblyIngredients().size(); ingredientSlot++) {
                        ItemStack stack = inv.get(index);
                        int assemblyCount = this.recipe.getAssemblyCount(ingredientSlot);
                        if(this.recipe.getAssemblyIngredients().get(ingredientSlot).test(stack)&&assemblyCount<=stack.getCount()){
                            this.input.set(index, StackUtils.size(stack, assemblyCount));
                            break;
                        }
                    }
                }
            }


            this.resetToRecipe();
            this.recipe = getRecipeFromGrid(this.dummy, world);
        }

        public AssemblyRecipeFormula(Level world, List<IInventorySlot> craftingGridSlots, AssemblyStationRecipe assemblyStationRecipe) {
            this.input = NonNullList.withSize(12, ItemStack.EMPTY);
            this.dummy = AssemblyContainer.createDummy(3);
            if(assemblyStationRecipe != null){
                for(int i = 0; i < 9; ++i) {
                    IInventorySlot craftingSlot = (IInventorySlot)craftingGridSlots.get(i);
                    if (!craftingSlot.isEmpty()) {
                        this.input.set(i, StackUtils.size(craftingSlot.getStack(), 1));
                        System.out.println("Slot: "+i+" Stack: "+this.input.get(i));
                    }else {
                        System.out.println("Slot: "+i+" is Empty");
                    }
                }
                for (int i = 0; i < 3; i++) {
                    int index = i+9;
                    IInventorySlot craftingSlot = (IInventorySlot)craftingGridSlots.get(index);
                    if (!craftingSlot.isEmpty()) {
                        for (int ingredientSlot = 0; ingredientSlot < assemblyStationRecipe.getAssemblyIngredients().size(); ingredientSlot++) {
                            ItemStack stack = craftingSlot.getStack();
                            int assemblyCount = assemblyStationRecipe.getAssemblyCount(ingredientSlot);
                            if(assemblyStationRecipe.getAssemblyIngredients().get(ingredientSlot).test(stack)&&assemblyCount<=stack.getCount()){
                                this.input.set(index, StackUtils.size(stack, assemblyCount));
                                break;
                            }
                        }
                    }
                }
            }
            this.resetToRecipe();
            this.recipe = getRecipeFromGrid(this.dummy, world);
        }

        public ItemStack getInputStack(int slot) {
            return this.input.get(slot);
        }

        private void resetToRecipe() {
            for(int i = 0; i < 12; ++i) {
                this.dummy.setItem(i, this.input.get(i));
            }
        }

        public boolean matches(Level world, List<IInventorySlot> craftingGridSlots) {
            if (this.recipe == null) {
                return false;
            } else {
                for(int i = 0; i < craftingGridSlots.size(); ++i) {
                    this.dummy.setItem(i, craftingGridSlots.get(i).getStack());
                }

                return this.recipe.matches(this.dummy, world);
            }
        }

        public ItemStack assemble() {
            return this.recipe == null ? ItemStack.EMPTY : this.recipe.assemble(this.dummy);
        }

        public NonNullList<ItemStack> getRemainingItems() {
            return this.recipe == null ? NonNullList.create() : this.recipe.getRemainingItems(this.dummy);
        }

        public boolean isIngredientInPos(Level world, ItemStack stack, int i) {
            if (this.recipe == null) {
                return false;
            } else if (stack.isEmpty() && !((ItemStack)this.input.get(i)).isEmpty()) {
                return false;
            } else {
                this.resetToRecipe();
                this.dummy.setItem(i, stack);
                return this.recipe.matches(this.dummy, world);
            }
        }
        public IntList getIngredientIndices(Level world, ItemStack stack) {
            IntList ret = new IntArrayList();
            if (this.recipe != null) {
                for(int i = 0; i < 12; ++i) {
                    this.dummy.setItem(i, stack);
                    if (this.recipe.matches(this.dummy, world)) {
                        ret.add(i);
                    }

                    this.dummy.setItem(i, (ItemStack)this.input.get(i));
                }
            }

            return ret;
        }

        public boolean isValidFormula() {
            return this.getRecipe() != null;
        }

        @Nullable
        public AssemblyStationRecipe getRecipe() {
            return this.recipe;
        }

        public boolean isFormulaEqual(AssemblyRecipeFormula formula) {
            return formula.getRecipe() == this.getRecipe();
        }

        public void setStack(Level world, int index, ItemStack stack) {
            this.input.set(index, stack);
            this.resetToRecipe();
            this.recipe = getRecipeFromGrid(this.dummy, world);
        }

        @Nullable
        private static AssemblyStationRecipe getRecipeFromGrid(AssemblyContainer inv, Level world) {
            Optional<AssemblyStationRecipe> recipeOptional = world.getRecipeManager().getRecipeFor(AssemblyStationRecipe.Type.INSTANCE,inv,world);
            return recipeOptional.orElse(null);
        }
}
