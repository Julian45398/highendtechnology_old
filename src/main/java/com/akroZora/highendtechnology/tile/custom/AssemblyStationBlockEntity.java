package com.akroZora.highendtechnology.tile.custom;

import com.akroZora.highendtechnology.tile.ModBlockEntities;
import com.akroZora.highendtechnology.recipe.AssemblyContainer;
import com.akroZora.highendtechnology.recipe.AssemblyStationRecipe;
import com.akroZora.highendtechnology.screen.AssemblyStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AssemblyStationBlockEntity extends BlockEntity implements MenuProvider, RecipeHolder {

    public static final int gridSlots = 9;
    public static final int assemblyItemSlots = 3;
    public static final int storageSlots = 9;
    public static final int outputSlots = 1;
    public static final int totalSlots = gridSlots +outputSlots+ storageSlots+assemblyItemSlots;

    boolean isItemTakeout = true;
    boolean update = true;
    protected Optional<CraftingRecipe> craftingRecipeUsed = Optional.empty();
    protected Optional<AssemblyStationRecipe> assemblyRecipeUsed = Optional.empty();
    private final EmptyHandler EMPTYHANDLER = new EmptyHandler();
    public final ItemStackHandler outputItemHandler = new ItemStackHandler(outputSlots) {
        @Override
        protected void onContentsChanged(int slot) {
            if(isItemTakeout){
                changeSlotsNoUpdate();
                updateRecipe(true);
            }
        }
    };
    public final ItemStackHandler gridItemHandler = new ItemStackHandler(gridSlots) {
        @Override
        protected void onContentsChanged(int slot) {
            updateRecipe(update);
            setChanged();
        }
    };
    public final ItemStackHandler assemblyItemHandler = new ItemStackHandler(assemblyItemSlots) {
        @Override
        protected void onContentsChanged(int slot) {
            updateRecipe(update);
            setChanged();
        }
    };
    public final ItemStackHandler storageItemHandler = new ItemStackHandler(storageSlots) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };



    public LazyOptional<IItemHandlerModifiable> gridHandler = LazyOptional.empty();
    public LazyOptional<IItemHandlerModifiable> assemblyHandler = LazyOptional.empty();
    public LazyOptional<IItemHandlerModifiable> outputHandler = LazyOptional.empty();
    public LazyOptional<IItemHandlerModifiable> storageHandler = LazyOptional.empty();

    AssemblyContainer assemblyContainer = new AssemblyContainer(gridItemHandler);

    SimpleContainer simpleContainer = new SimpleContainer(12);

    @Override
    public void onLoad() {
        super.onLoad();
        outputHandler = LazyOptional.of(() -> outputItemHandler);
        gridHandler = LazyOptional.of(() -> gridItemHandler);
        assemblyHandler = LazyOptional.of(() -> assemblyItemHandler);
        storageHandler = LazyOptional.of(() -> storageItemHandler);
        updateRecipe(true);
    }

    public AssemblyStationBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.ASSEMBLY_STATION_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Assembly Station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        assert this.level != null;
        return new AssemblyStationMenu(pContainerId, pInventory, this);
    }

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,int slotCategory) {
        return switch (slotCategory) {
            case 1 -> gridHandler.cast();
            case 2 -> outputHandler.cast();
            case 3 -> assemblyHandler.cast();
            case 4 -> storageHandler.cast();
            default -> super.getCapability(cap);
        };
    }

    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        outputHandler.invalidate();
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("MatrixInventory", gridItemHandler.serializeNBT());
        tag.put("AssemblyInventory", assemblyItemHandler.serializeNBT());
        tag.put("StorageInventory", storageItemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        gridItemHandler.deserializeNBT(nbt.getCompound("MatrixInventory"));
        assemblyItemHandler.deserializeNBT(nbt.getCompound("AssemblyInventory"));
        storageItemHandler.deserializeNBT(nbt.getCompound("StorageInventory"));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(totalSlots);
        for (int i = 0; i < gridItemHandler.getSlots(); i++) {
            inventory.setItem(i+ outputSlots, gridItemHandler.getStackInSlot(i));
        }
        for (int i = 0; i < assemblyItemHandler.getSlots(); i++) {
            inventory.setItem(i+ outputSlots+ gridSlots, assemblyItemHandler.getStackInSlot(i));
        }
        for (int i = 0; i < storageItemHandler.getSlots(); i++) {
            inventory.setItem(i+ outputSlots+ gridSlots+assemblyItemSlots, storageItemHandler.getStackInSlot(i));
        }
        if(this.hasLevel()) {
            assert this.level != null;
            Containers.dropContents(this.level, this.worldPosition, inventory);
        }

    }

    public void changeAssemblySlots(@NotNull Optional<AssemblyStationRecipe> recipeUsed){
        if(assemblyRecipeUsed.isPresent()){
            int ingredientSize = recipeUsed.get().getIngredients().size();
            for(int i=9; i<ingredientSize; i++){
                if(recipeUsed.get().getIngredients().get(i)==Ingredient.EMPTY){
                    return;
                }
                ItemStack ingredient = recipeUsed.get().getIngredients().get(i).getItems()[0];
                for(int j=0; j<assemblyItemSlots; j++){
                    ItemStack assemblyItemStack = assemblyItemHandler.getStackInSlot(j);
                    if(assemblyItemStack.getItem()==ingredient.getItem()){
                        assemblyItemHandler.extractItem(j,ingredient.getCount(),false);
                    }
                }
            }
        }
    }
    public void changeGridSlots(){
        for(int i=0; i<9; i++){
            if(gridItemHandler.getStackInSlot(i)!=ItemStack.EMPTY){
                gridItemHandler.extractItem(i,1,false);
            }
        }
    }
    public void changeSlotsNoUpdate(){
        update=false;
        changeAssemblySlots(assemblyRecipeUsed);
        changeGridSlots();
        update=true;
    }
    public void updateRecipe(boolean updatesRecipe) {
        if(!updatesRecipe)  return;

        if (this.hasLevel()) {
            System.out.println("updatesRecipe");
            boolean hasAssemblyItems = false;
            for(int i=0;i<assemblyItemSlots;i++){
                //checking if Entity has items in Assembly Slots
                hasAssemblyItems = assemblyItemHandler.getStackInSlot(i)!=ItemStack.EMPTY||hasAssemblyItems;
            }
            if(hasAssemblyItems){
                //Checking for Assembly Recipes
                for (int i = 0; i < gridSlots; i++) {
                    simpleContainer.setItem(i, gridItemHandler.getStackInSlot(i).copy());
                }
                for (int i = 0; i < assemblyItemSlots; i++) {
                    simpleContainer.setItem(i+gridSlots, assemblyItemHandler.getStackInSlot(i).copy());
                }
                assemblyRecipeUsed = level.getRecipeManager().getRecipeFor(AssemblyStationRecipe.Type.INSTANCE, simpleContainer, level);
                if(assemblyRecipeUsed.isPresent()) {
                    isItemTakeout = false;
                    outputItemHandler.setStackInSlot(0, assemblyRecipeUsed.get().getResultItem().copy());
                    isItemTakeout=true;
                    return;
                }
            }
            else {
                //Checking for Crafting Recipes
                craftingRecipeUsed = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, assemblyContainer, level);
                if(craftingRecipeUsed.isPresent()){
                    isItemTakeout = false;
                    outputItemHandler.setStackInSlot(0, craftingRecipeUsed.get().getResultItem().copy());
                    isItemTakeout=true;
                    return;
                }
            }
            // Setting Output Slot Empty if no Recipe
            isItemTakeout = false;
            outputItemHandler.setStackInSlot(0,ItemStack.EMPTY);
            isItemTakeout=true;
        }
        System.out.println("updatedRecipe");
    }

    public void getItemsFromContainers(CraftingContainer craftingContainer, ResultContainer resultContainer){
        for(int i=0;i<craftingContainer.getContainerSize();i++){
            gridItemHandler.setStackInSlot(i,craftingContainer.getItem(i));
        }
        outputItemHandler.setStackInSlot(0,craftingContainer.getItem(0));
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> pRecipe) {

    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }
}
