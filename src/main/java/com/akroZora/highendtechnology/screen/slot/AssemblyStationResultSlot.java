package com.akroZora.highendtechnology.screen.slot;

import com.akroZora.highendtechnology.recipe.AssemblyStationRecipe;
import com.akroZora.highendtechnology.screen.AssemblyStationMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

public class AssemblyStationResultSlot extends Slot {
    private final CraftingContainer craftSlots;
    private final SimpleContainer storageSlots;
    private final Player player;
    private int removeCount;
    private final AssemblyStationMenu menu;

    public AssemblyStationResultSlot(Player pPlayer, AssemblyStationMenu menu, int pSlot, int pXPosition, int pYPosition) {
        super(menu.getResultContainer(), pSlot, pXPosition, pYPosition);
        this.player = pPlayer;
        this.craftSlots = menu.getCraftingContainer();
        this.storageSlots = menu.getStorageContainer();
        this.menu = menu;
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    public boolean mayPlace(ItemStack pStack) {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new stack.
     */
    public ItemStack remove(int pAmount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(pAmount, this.getItem().getCount());
        }

        return super.remove(pAmount);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onQuickCraft(ItemStack pStack, int pAmount) {
        this.removeCount += pAmount;
        this.checkTakeAchievements(pStack);
    }

    protected void onSwapCraft(int pNumItemsCrafted) {
        this.removeCount += pNumItemsCrafted;
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void checkTakeAchievements(ItemStack pStack) {
        if (this.removeCount > 0) {
            pStack.onCraftedBy(this.player.level, this.player, this.removeCount);
            net.minecraftforge.event.ForgeEventFactory.firePlayerCraftingEvent(this.player, pStack, this.craftSlots);
        }

        if (this.container instanceof RecipeHolder) {
            ((RecipeHolder)this.container).awardUsedRecipes(this.player);
        }

        this.removeCount = 0;
    }

    private void setRemainingItemsInStorageSlots(ItemStack remainingItems){
        for (int i = 0; i < this.storageSlots.getContainerSize(); i++) {
            ItemStack storageSlotItem = this.storageSlots.getItem(i);
            if(ItemStack.isSame(storageSlotItem, remainingItems) && ItemStack.tagMatches(storageSlotItem, remainingItems) && remainingItems.getCount()+this.storageSlots.getItem(i).getCount()<=remainingItems.getMaxStackSize()){
                remainingItems.grow(storageSlotItem.getCount());
                this.storageSlots.setItem(i, remainingItems);
                return;
            }
        }
        for (int i = 0; i < this.storageSlots.getContainerSize(); i++) {
            ItemStack storageSlotItem = this.storageSlots.getItem(i);
            if(storageSlotItem.isEmpty()){
                this.storageSlots.setItem(i, remainingItems);
                return;
            }
        }
        if (!this.player.getInventory().add(remainingItems)) {
            this.player.drop(remainingItems, false);
        }
    }

    private void onTakeCraftingRecipe(NonNullList<ItemStack> nonnulllist){
        System.out.println("onTakeCraftingRecipe");
        for(int i = 0; i < nonnulllist.size(); ++i) {
            boolean reducedStorageSlots = false;
            ItemStack craftSlotsItem = this.craftSlots.getItem(i);
            ItemStack remainingItems = nonnulllist.get(i);
            if (!craftSlotsItem.isEmpty()) {
                reducedStorageSlots = reduceGridSlots(craftSlotsItem,i);
                craftSlotsItem = this.craftSlots.getItem(i);
            }
            if (!remainingItems.isEmpty()) {
                if(reducedStorageSlots){
                    setRemainingItemsInStorageSlots(remainingItems);
                } else if (craftSlotsItem.isEmpty()) {
                    this.craftSlots.setItem(i, remainingItems);
                } else if (ItemStack.isSame(craftSlotsItem, remainingItems) && ItemStack.tagMatches(craftSlotsItem, remainingItems)) {
                    remainingItems.grow(craftSlotsItem.getCount());
                    this.craftSlots.setItem(i, remainingItems);
                } else if (!this.player.getInventory().add(remainingItems)) {
                    this.player.drop(remainingItems, false);
                }
            }
        }
    }
    private void onTakeAssemblyRecipe(AssemblyStationRecipe assemblyStationRecipe){
        NonNullList<ItemStack> nonNullList = assemblyStationRecipe.getRemainingItems(this.craftSlots);
        for(int i = 0; i < nonNullList.size(); ++i) {
            boolean reducedStorageSlots = false;
            ItemStack craftSlotsItem = this.craftSlots.getItem(i);
            ItemStack remainingItems = nonNullList.get(i);
            if (!craftSlotsItem.isEmpty()) {
                if(i<9){
                    reducedStorageSlots = reduceGridSlots(craftSlotsItem,i);
                }else{
                    reducedStorageSlots = reduceAssemblySlots(assemblyStationRecipe,i);
                }
                craftSlotsItem = this.craftSlots.getItem(i);
            }
            if (!remainingItems.isEmpty()) {
                if(reducedStorageSlots){
                    setRemainingItemsInStorageSlots(remainingItems);
                } else if (craftSlotsItem.isEmpty()) {
                    this.craftSlots.setItem(i, remainingItems);
                } else if (ItemStack.isSame(craftSlotsItem, remainingItems) && ItemStack.tagMatches(craftSlotsItem, remainingItems)) {
                    remainingItems.grow(craftSlotsItem.getCount());
                    this.craftSlots.setItem(i, remainingItems);
                } else if (!this.player.getInventory().add(remainingItems)) {
                    this.player.drop(remainingItems, false);
                }
            }
        }
    }
    private boolean reduceGridSlots(ItemStack stack, int craftSlot){
        for (int i = this.storageSlots.getContainerSize()-1; 0 <= i ; i--) {
            if(this.storageSlots.getItem(i).getItem().equals(stack.getItem())){
                this.storageSlots.removeItem(i,1);
                return true;
            }
        }
        this.craftSlots.removeItem(craftSlot, 1);
        return false;
    }

    //Reduce Assembly Ingredients in Storage Slots if Items match and returns the reduced Count
    private int reduceAssemblyItemsInStorage(ItemStack stack, int count){
        int toCraft = count;
        System.out.println("Item Stack: in Container: "+stack);
        System.out.println("Required count to Craft: "+count);
        for (int i = this.storageSlots.getContainerSize()-1; 0 <=i ; i--) {
            if(toCraft ==0){
                return 0;
            }
            if(this.storageSlots.getItem(i).getItem().equals(stack.getItem())){
                System.out.println("Stack in Slot "+i+"equals Item Stack");
                int countInStorage = this.storageSlots.getItem(i).getCount();
                if(toCraft<=countInStorage){
                    this.storageSlots.removeItem(i,toCraft);
                    return 0;
                }else {
                    this.storageSlots.setItem(i,ItemStack.EMPTY);
                    toCraft = toCraft-countInStorage;
                }
            }
        }
        return toCraft;
    }
    //checks the Container Slot for a Matching Ingredient. If the Item is Present in the Storage Slots it Reduces them first.
    //Returns true if items where reduced from the Storage
    private boolean reduceAssemblySlots(AssemblyStationRecipe assemblyStationRecipe,int slot){
        int assemblyIngredientCount = assemblyStationRecipe.getAssemblyIngredients().size();
        boolean reducedAssemblyItemsfromStorage = false;
        for (int j = 0; j < assemblyIngredientCount; j++) { //for every assembly Ingredient
            Ingredient ingredient = assemblyStationRecipe.getAssemblyIngredients().get(j);
            int count = assemblyStationRecipe.getAssemblyCount(j);
            if(ingredient.test(this.craftSlots.getItem(slot))&&count<=this.craftSlots.getItem(slot).getCount()){
                int remainingCount = reduceAssemblyItemsInStorage(this.craftSlots.getItem(slot),count);
                System.out.println("Remaining Count: "+remainingCount);
                if(remainingCount==0){
                    reducedAssemblyItemsfromStorage = true;
                } else if (remainingCount==count) {
                    this.craftSlots.removeItem(slot,count);
                    reducedAssemblyItemsfromStorage = false;
                }else {
                    this.craftSlots.removeItem(slot,remainingCount);
                    reducedAssemblyItemsfromStorage = true;
                }
            }
        }
        return reducedAssemblyItemsfromStorage;
    }

    public void onTake(Player pPlayer, ItemStack pStack) {
        this.checkTakeAchievements(pStack);
        System.out.println("on Take");
        if(pPlayer.level.isClientSide){
            return;
        }
        menu.setDoUpdate(false);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(pPlayer);
        Optional<AssemblyStationRecipe> assemblyStationRecipeOptional = pPlayer.level.getServer().getRecipeManager().getRecipeFor(AssemblyStationRecipe.Type.INSTANCE, this.craftSlots, pPlayer.level);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);
        if(assemblyStationRecipeOptional.isPresent()){
            onTakeAssemblyRecipe(assemblyStationRecipeOptional.get());
        } else {
            NonNullList<ItemStack> nonNullList = pPlayer.level.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING,this.craftSlots,pPlayer.level);
            onTakeCraftingRecipe(nonNullList);
        }
        menu.setDoUpdate(true);
        menu.slotsChanged(this.craftSlots);
    }
}
