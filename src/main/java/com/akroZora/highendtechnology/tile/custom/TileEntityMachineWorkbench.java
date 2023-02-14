package com.akroZora.highendtechnology.tile.custom;

import com.akroZora.highendtechnology.recipe.AssemblyContainer;
import com.akroZora.highendtechnology.recipe.AssemblyStationRecipe;
import com.akroZora.highendtechnology.registration.HighendtechnologyBlocks;
import com.akroZora.highendtechnology.screen.slot.CraftingInputSlot;
import com.akroZora.highendtechnology.screen.slot.CraftingOutputSlot;
import com.akroZora.highendtechnology.tile.CraftingTile;
import mekanism.api.Action;
import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TileEntityMachineWorkbench extends TileEntityMekanism implements CraftingTile{

    public static final int craftingSize = 3;
    public static final int storageRows = 2;

    private final AssemblyContainer dummyContainer = AssemblyContainer.createDummy(3);
    public TileEntityMachineWorkbench(BlockPos pos, BlockState state) {
        super(HighendtechnologyBlocks.MACHINE_WORKBENCH, pos, state);
    }
    private boolean doRecipeCheck = true;

    private List<IInventorySlot> storageSlots;
    private List<IInventorySlot> gridSlots;
    private IInventorySlot outputSlot;

    private AssemblyStationRecipe recipe = null;



    @Nullable
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        this.storageSlots = new ArrayList<>();
        this.gridSlots = new ArrayList<>();
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);

        for(int slotY = 0; slotY < craftingSize; ++slotY) {
            for(int slotX = 0; slotX < craftingSize; ++slotX) {
                IInventorySlot gridSlot = CraftingInputSlot.at(listener,20 + slotX * 18, 17 + slotY * 18);
                builder.addSlot(gridSlot);
                this.gridSlots.add(gridSlot);
            }
        }
        for(int slotY = 0; slotY < craftingSize; ++slotY) {
            IInventorySlot assemblySlot = CraftingInputSlot.at(listener,80, 17 + slotY * 18);
            builder.addSlot(assemblySlot);
            this.gridSlots.add(assemblySlot);
        }

        this.outputSlot = CraftingOutputSlot.at(listener,140,35);

        builder.addSlot(this.outputSlot);


        for(int slotY = 0; slotY < storageRows; ++slotY) {
            for(int slotX = 0; slotX < 9; ++slotX) {
                IInventorySlot storageSlot = BasicInventorySlot.at(listener, 8 + slotX * 18, 80 + slotY * 18);
                builder.addSlot(storageSlot);
                this.storageSlots.add(storageSlot);
            }
        }
        return builder.build();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        onOutputChanged();
        onGridSlotsChanged();
    }

    @Override
    public void onContentsChanged() {
        super.onContentsChanged();
    }

    @Override
    public void onGridSlotsChanged() {
        if(this.doRecipeCheck){
            this.recipe = recalculateRecipe().orElse(null);
            setOutputSlot();
        }
    }

    public Optional<AssemblyStationRecipe> recalculateRecipe(){
        System.out.println("recalculatingRecipe");;
        if(this.level==null){
            return Optional.empty();
        }
        for(int i = 0; i < this.gridSlots.size(); ++i) {
            this.dummyContainer.setItem(i, this.gridSlots.get(i).getStack());
        }
        return this.level.getRecipeManager().getRecipeFor(AssemblyStationRecipe.Type.INSTANCE,this.dummyContainer,this.level);
    }

    @Override
    public void onOutputChanged() {
        if(isItemTakout()){
            this.doRecipeCheck = false;
            reduceGridSlots();
            this.recipe = recalculateRecipe().orElse(null);
            setOutputSlot();
        }
        this.doRecipeCheck = true;
    }

    public boolean isItemTakout(){
        if(this.recipe==null){
            return false;
        } else return this.outputSlot.isEmpty();
    }

    public void reduceGridSlots(){
        int gridSlotAmount = craftingSize * craftingSize;
        for (int i = 0; i < gridSlotAmount; i++) {
            this.gridSlots.get(i).growStack(-1, Action.EXECUTE);
        }
        NonNullList<Ingredient> assemblyIngredients = this.recipe.getAssemblyIngredients();
        for (int assemblySlotIndex = gridSlotAmount; assemblySlotIndex < craftingSize+gridSlotAmount; assemblySlotIndex++) {
            ItemStack assemblySlotItem = this.gridSlots.get(assemblySlotIndex).getStack();
            for (int ingredientIndex = 0; ingredientIndex < assemblyIngredients.size(); ingredientIndex++) {
                Ingredient ingredient = assemblyIngredients.get(ingredientIndex);
                if(ingredient.test(assemblySlotItem)){
                    this.gridSlots.get(assemblySlotIndex).growStack(-this.recipe.getAssemblyCount(ingredientIndex),Action.EXECUTE);
                    break;
                }
            }
        }
    }

    public void setOutputSlot(){
        if(this.recipe!=null){
            this.outputSlot.setStack(this.recipe.getResultItem());
        }else {
            this.outputSlot.setStack(ItemStack.EMPTY);
        }
    }

    public IInventorySlot getOutputSlot() {
        return outputSlot;
    }
}
