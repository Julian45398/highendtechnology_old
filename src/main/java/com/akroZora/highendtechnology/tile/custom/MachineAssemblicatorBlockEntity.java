package com.akroZora.highendtechnology.tile.custom;

import com.akroZora.highendtechnology.registration.HighendtechnologyBlocks;
import com.akroZora.highendtechnology.content.assemblicator.AssemblyRecipeFormula;
import com.akroZora.highendtechnology.item.custom.ItemAssemblyFormula;
import com.akroZora.highendtechnology.recipe.AssemblyContainer;
import com.akroZora.highendtechnology.recipe.AssemblyStationRecipe;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.Upgrade;
import mekanism.api.annotations.NonNull;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.FloatingLong;
import mekanism.common.CommonWorldTickHandler;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.container.sync.SyncableItemStack;
import mekanism.common.inventory.slot.*;
import mekanism.common.item.ItemCraftingFormula;
import mekanism.common.lib.inventory.HashedItem;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.interfaces.IHasMode;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StackUtils;
import mekanism.common.util.UpgradeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MachineAssemblicatorBlockEntity extends TileEntityConfigurableMachine implements IHasMode {
    private static final NonNullList<ItemStack> EMPTY_LIST = NonNullList.create();
    private static final Predicate<@NonNull ItemStack> formulaSlotValidator = (stack) -> stack.getItem() instanceof ItemAssemblyFormula;
    private static final int BASE_TICKS_REQUIRED = 40;
    private final AssemblyContainer dummyInv = AssemblyContainer.createDummy(3);
    private int ticksRequired = 40;
    private int operatingTicks;
    private boolean autoMode = false;
    private boolean isRecipe = false;
    private boolean stockControl = false;
    private boolean needsOrganize = true;
    private final HashedItem[] stockControlMap = new HashedItem[18];
    private int pulseOperations;
    public AssemblyRecipeFormula recipeFormula;
    @Nullable
    private AssemblyStationRecipe cachedRecipe = null;
    @SyntheticComputerMethod(
            getter = "getExcessRemainingItems"
    )
    private NonNullList<ItemStack> lastRemainingItems;
    private ItemStack lastFormulaStack;
    private ItemStack lastOutputStack;
    private MachineEnergyContainer<MachineAssemblicatorBlockEntity> energyContainer;
    private List<IInventorySlot> craftingGridSlots;
    private List<IInventorySlot> inputSlots;
    private List<IInventorySlot> outputSlots;
    @WrappingComputerMethod(
            wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class,
            methodNames = {"getFormulaItem"}
    )
    private BasicInventorySlot formulaSlot;
    @WrappingComputerMethod(
            wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class,
            methodNames = {"getEnergyItem"}
    )
    private EnergyInventorySlot energySlot;

    public MachineAssemblicatorBlockEntity(BlockPos pos, BlockState state) {
        super(HighendtechnologyBlocks.MACHINE_ASSEMBLICATOR, pos, state);
        this.lastRemainingItems = EMPTY_LIST;
        this.lastFormulaStack = ItemStack.EMPTY;
        this.lastOutputStack = ItemStack.EMPTY;
        this.configComponent = new TileComponentConfig(this, new TransmissionType[]{TransmissionType.ITEM, TransmissionType.ENERGY});
        this.configComponent.setupItemIOConfig(this.inputSlots, this.outputSlots, this.energySlot, false);
        ConfigInfo itemConfig = this.configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, new IInventorySlot[]{this.formulaSlot}));
            itemConfig.setDefaults();
        }

        this.configComponent.setupInputConfig(TransmissionType.ENERGY, this.energyContainer);
        this.ejectorComponent = new TileComponentEjector(this);
        this.ejectorComponent.setOutputData(this.configComponent, new TransmissionType[]{TransmissionType.ITEM});
    }

    @Nonnull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addContainer(this.energyContainer = MachineEnergyContainer.input(this, listener));
        return builder.build();
    }

    @Nonnull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        this.craftingGridSlots = new ArrayList();
        this.inputSlots = new ArrayList();
        this.outputSlots = new ArrayList();
        InventorySlotHelper builder = InventorySlotHelper.forSideWithConfig(this::getDirection, this::getConfig);
        ((BasicInventorySlot)builder.addSlot(this.formulaSlot = BasicInventorySlot.at(formulaSlotValidator, listener, 6, 26))).setSlotOverlay(SlotOverlay.FORMULA);

        int slotY;
        int slotX;
        for(slotY = 0; slotY < 2; ++slotY) {
            for(slotX = 0; slotX < 9; ++slotX) {
                int index = slotY * 9 + slotX;
                InputInventorySlot inputSlot = InputInventorySlot.at((stack) -> {
                    if (this.recipeFormula == null) {
                        return true;
                    } else {
                        int temp = stack.getCount();
                        stack.setCount(stack.getMaxStackSize());
                        IntList indices = this.recipeFormula.getIngredientIndices(this.level, stack);
                        stack.setCount(temp);
                        if (!indices.isEmpty()) {
                            HashedItem stockItem = this.stockControlMap[index];
                            return this.stockControl && stockItem != null ? ItemHandlerHelper.canItemStacksStack(stockItem.getStack(), stack) : true;
                        } else {
                            return false;
                        }
                    }
                }, (item) -> {
                    return true;
                }, listener, 8 + slotX * 18, 98 + slotY * 18);
                builder.addSlot(inputSlot);
                this.inputSlots.add(inputSlot);
            }
        }

        for(slotY = 0; slotY < 3; ++slotY) {
            for(slotX = 0; slotX < 3; ++slotX) {
                IInventorySlot craftingSlot = FormulaicCraftingSlot.at(this::getAutoMode, listener, 26 + slotX * 18, 17 + slotY * 18);
                builder.addSlot(craftingSlot);
                this.craftingGridSlots.add(craftingSlot);
            }
        }

        for(slotY = 0; slotY < 3; ++slotY) {
            IInventorySlot craftingSlot = FormulaicCraftingSlot.at(this::getAutoMode, listener, 83, 17 + slotY * 18);
            builder.addSlot(craftingSlot);
            this.craftingGridSlots.add(craftingSlot);
        }

        for(slotY = 0; slotY < 3; ++slotY) {
            for(slotX = 0; slotX < 2; ++slotX) {
                OutputInventorySlot outputSlot = OutputInventorySlot.at(listener, 124 + slotX * 18, 17 + slotY * 18);
                builder.addSlot(outputSlot);
                this.outputSlots.add(outputSlot);
            }
        }

        builder.addSlot(this.energySlot = EnergyInventorySlot.fillOrConvert(this.energyContainer, this::getLevel, listener, 152, 76));
        return builder.build();
    }

    public BasicInventorySlot getFormulaSlot() {
        return this.formulaSlot;
    }
    @Override
    public void onLoad() {
        super.onLoad();
        if (!this.isRemote()) {
            this.checkFormula();
            this.recalculateRecipe();
        }

    }
    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (CommonWorldTickHandler.flushTagAndRecipeCaches) {
            this.cachedRecipe = null;
            this.recalculateRecipe();
        }

        if (this.recipeFormula != null && this.stockControl && this.needsOrganize) {
            this.needsOrganize = false;
            this.buildStockControlMap();
            this.organizeStock();
        }

        this.energySlot.fillContainerOrConvert();
        if (this.getControlType() != RedstoneControl.PULSE) {
            this.pulseOperations = 0;
        } else if (MekanismUtils.canFunction(this)) {
            ++this.pulseOperations;
        }

        this.checkFormula();
        if (this.autoMode && this.recipeFormula == null) {
            this.nextMode();
        }

        if (!this.autoMode || this.recipeFormula == null || (this.getControlType() != RedstoneControl.PULSE || this.pulseOperations <= 0) && !MekanismUtils.canFunction(this)) {
            this.operatingTicks = 0;
        } else {
            boolean canOperate = true;
            if (!this.isRecipe) {
                canOperate = this.moveItemsToGrid();
            }

            if (canOperate) {
                this.isRecipe = true;
                if (this.operatingTicks >= this.ticksRequired) {
                    if (this.doSingleCraft()) {
                        this.operatingTicks = 0;
                        if (this.pulseOperations > 0) {
                            --this.pulseOperations;
                        }
                    }
                } else {
                    FloatingLong energyPerTick = this.energyContainer.getEnergyPerTick();
                    if (this.energyContainer.extract(energyPerTick, Action.SIMULATE, AutomationType.INTERNAL).equals(energyPerTick)) {
                        this.energyContainer.extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL);
                        ++this.operatingTicks;
                    }
                }
            } else {
                this.operatingTicks = 0;
            }
        }

    }

    private void checkFormula() {
        ItemStack formulaStack = this.formulaSlot.getStack();
        if (!formulaStack.isEmpty() && formulaStack.getItem() instanceof ItemAssemblyFormula) {
            if (this.recipeFormula == null || this.lastFormulaStack != formulaStack) {
                this.loadFormula();
            }
        } else {
            this.recipeFormula = null;
        }

        this.lastFormulaStack = formulaStack;
    }

    private void loadFormula() {
        ItemStack formulaStack = this.formulaSlot.getStack();
        ItemAssemblyFormula formulaItem = (ItemAssemblyFormula)formulaStack.getItem();
        if (formulaItem.isInvalid(formulaStack)) {
            this.recipeFormula = null;
        } else {
            NonNullList<ItemStack> formulaInventory = formulaItem.getInventory(formulaStack);
            if (formulaInventory == null) {
                this.recipeFormula = null;
            } else {
                AssemblyRecipeFormula recipe = new AssemblyRecipeFormula(this.level, formulaInventory);
                if (recipe.isValidFormula()) {
                    if (this.recipeFormula == null) {
                        this.recipeFormula = recipe;
                    } else if (!this.recipeFormula.isFormulaEqual(recipe)) {
                        this.recipeFormula = recipe;
                        this.operatingTicks = 0;
                    }
                } else {
                    this.recipeFormula = null;
                    formulaItem.setInvalid(formulaStack, true);
                }
            }

        }
    }
    @Override
    protected void setChanged(boolean updateComparator) {
        super.setChanged(updateComparator);
        this.recalculateRecipe();
    }

    private void recalculateRecipe() {
        if (this.level != null && !this.isRemote()) {
            if (this.recipeFormula != null && this.recipeFormula.isValidFormula()) {
                this.isRecipe = this.recipeFormula.matches(this.level, this.craftingGridSlots);
                if (this.isRecipe) {
                    this.lastOutputStack = this.recipeFormula.assemble();
                    this.lastRemainingItems = this.recipeFormula.getRemainingItems();
                } else {
                    this.lastOutputStack = ItemStack.EMPTY;
                }
            } else {
                for(int i = 0; i < this.craftingGridSlots.size(); ++i) {
                    this.dummyInv.setItem(i, this.craftingGridSlots.get(i).getStack());
                }

                this.lastRemainingItems = EMPTY_LIST;
                if (this.cachedRecipe == null || !this.cachedRecipe.matches(this.dummyInv, this.level)) {
                    Optional<AssemblyStationRecipe> optional = this.level.getRecipeManager().getRecipeFor(AssemblyStationRecipe.Type.INSTANCE,this.dummyInv,this.level);
                    this.cachedRecipe = optional.orElse(null);
                }

                if (this.cachedRecipe == null) {
                    this.lastOutputStack = ItemStack.EMPTY;
                } else {
                    this.lastOutputStack = this.cachedRecipe.assemble(this.dummyInv);
                    this.lastRemainingItems = this.cachedRecipe.getRemainingItems(this.dummyInv);
                }

                this.isRecipe = !this.lastOutputStack.isEmpty();
            }

            this.needsOrganize = true;
        }

    }

    private boolean doSingleCraft() {
        this.recalculateRecipe();
        ItemStack output = this.lastOutputStack;
        if (output.isEmpty() || !this.tryMoveToOutput(output, Action.SIMULATE) || !this.lastRemainingItems.isEmpty() && !this.lastRemainingItems.stream().allMatch((it) -> {
            return it.isEmpty() || this.tryMoveToOutput(it, Action.SIMULATE);
        })) {
            return false;
        } else {
            this.tryMoveToOutput(output, Action.EXECUTE);
            Iterator var2 = this.lastRemainingItems.iterator();

            while(var2.hasNext()) {
                ItemStack remainingItem = (ItemStack)var2.next();
                if (!remainingItem.isEmpty()) {
                    this.tryMoveToOutput(remainingItem, Action.EXECUTE);
                }
            }

            reduceAssemblyItems(this.craftingGridSlots);


            if (this.recipeFormula != null) {
                this.moveItemsToGrid();
            }

            this.markForSave();
            return true;
        }
    }

    public void reduceAssemblyItems(List<IInventorySlot> craftingGridSlots){
        AssemblyStationRecipe recipe;
        if(this.recipeFormula!=null){
            if(!this.recipeFormula.isValidFormula()){
                System.out.println("Formula is not valid");
                return;
            }
            recipe = this.recipeFormula.getRecipe();
            if(recipe==null){
                System.out.println("formula doesn't have a recipe");
                return;
            }
        } else if (this.cachedRecipe!= null) {
            recipe = this.cachedRecipe;

        }else{
            System.out.println("no Recipe cached and no formula Present");
            return;
        }
        for (int i = 0; i < 9 ; i++) {
            IInventorySlot craftingSlot = craftingGridSlots.get(i);
            if(!craftingSlot.isEmpty()){
                MekanismUtils.logMismatchedStackSize((long)craftingSlot.shrinkStack(1,Action.EXECUTE),1L);
            }
        }
        for (int ingredientIndex = 0; ingredientIndex < recipe.getAssemblyIngredients().size(); ingredientIndex++) {
            for (int j = 0; j < 3; j++) {
                int slotIndex = j+9;
                IInventorySlot craftingSlot = craftingGridSlots.get(slotIndex);
                if(recipe.getAssemblyIngredients().get(ingredientIndex).test(craftingSlot.getStack())){
                    int shrinkAmount = recipe.getAssemblyCount(ingredientIndex);
                    MekanismUtils.logMismatchedStackSize((long)craftingSlot.shrinkStack(shrinkAmount,Action.EXECUTE),(long)shrinkAmount);
                }
            }
        }
    }


    public boolean craftSingle() {
        if (this.recipeFormula == null) {
            return this.doSingleCraft();
        } else {
            boolean canOperate = true;
            if (!this.recipeFormula.matches(this.getLevel(), this.craftingGridSlots)) {
                canOperate = this.moveItemsToGrid();
            }

            return canOperate ? this.doSingleCraft() : false;
        }
    }

    private boolean moveItemsToGrid() {
        boolean ret = true;

        for(int i = 0; i < this.craftingGridSlots.size(); ++i) {
            IInventorySlot recipeSlot = (IInventorySlot)this.craftingGridSlots.get(i);
            ItemStack recipeStack = recipeSlot.getStack();
            if (!this.recipeFormula.isIngredientInPos(this.level, recipeStack, i)) {
                if (!recipeStack.isEmpty()&&i<9) {
                    System.out.println("Recipe stack in slot: "+i+" is not empty");
                    recipeSlot.setStack(recipeStack = this.tryMoveToInput(recipeStack));
                    this.markForSave();
                    if (!recipeStack.isEmpty()) {
                        ret = false;
                    }
                } else {
                    boolean found = false;
                    for(int j = this.inputSlots.size() - 1; j >= 0; --j) {
                        IInventorySlot stockSlot = (IInventorySlot)this.inputSlots.get(j);
                        if (!stockSlot.isEmpty()) {
                            int temp = stockSlot.getStack().getCount();
                            stockSlot.growStack(64,Action.EXECUTE);
                            stockSlot.getStack().setCount(stockSlot.getStack().getMaxStackSize());
                            ItemStack stockStack = stockSlot.getStack();
                            boolean itemMatches = this.recipeFormula.isIngredientInPos(this.level, stockStack, i);
                            stockSlot.getStack().setCount(temp);
                            stockStack = stockSlot.getStack();
                            if (!itemMatches) {

                            } else if (i<9) {
                                recipeSlot.setStack(StackUtils.size(stockStack, 1));
                                MekanismUtils.logMismatchedStackSize((long)stockSlot.shrinkStack(1, Action.EXECUTE), 1L);
                                this.markForSave();
                                found = true;
                                break;
                            } else {
                                if(this.recipeFormula.recipe !=null){
                                    int requiredCount = this.recipeFormula.getInputStack(i).getCount();
                                    int size = Math.min(requiredCount, stockStack.getCount());
                                    if(recipeStack.isEmpty()){
                                        System.out.println("recipe Stack is empty in slot: "+i+" moving Count: "+size+" to slot");
                                        recipeSlot.setStack(StackUtils.size(stockStack, size));
                                        MekanismUtils.logMismatchedStackSize((long)stockSlot.shrinkStack(size, Action.EXECUTE), size);
                                    }else{
                                        size = Math.min(requiredCount-recipeStack.getCount(),size);
                                        recipeSlot.growStack(size,Action.EXECUTE);
                                        //recipeSlot.setStack(StackUtils.size(stockStack, size));
                                        MekanismUtils.logMismatchedStackSize((long)stockSlot.shrinkStack(size, Action.EXECUTE), size);
                                    }
                                }
                                if(recipeSlot.getCount()>=this.recipeFormula.getInputStack(i).getCount()){
                                    this.markForSave();
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (!found) {
                        ret = false;
                    }
                }
            }
        }

        return ret;
    }

    public void craftAll() {
        while(this.craftSingle()) {
        }

    }

    public void moveItems() {
        if (this.recipeFormula == null) {
            this.moveItemsToInput(true);
        } else {
            this.moveItemsToGrid();
        }

    }

    private void moveItemsToInput(boolean forcePush) {
        for(int i = 0; i < this.craftingGridSlots.size(); ++i) {
            IInventorySlot recipeSlot = (IInventorySlot)this.craftingGridSlots.get(i);
            ItemStack recipeStack = recipeSlot.getStack();
            if (!recipeStack.isEmpty() && (forcePush || this.recipeFormula != null && !this.recipeFormula.isIngredientInPos(this.getLevel(), recipeStack, i))) {
                recipeSlot.setStack(this.tryMoveToInput(recipeStack));
            }
        }

        this.markForSave();
    }

    public void nextMode() {
        if (this.autoMode) {
            this.operatingTicks = 0;
            this.autoMode = false;
            this.markForSave();
        } else if (this.recipeFormula != null) {
            this.moveItemsToInput(false);
            this.autoMode = true;
            this.markForSave();
        }

    }

    @ComputerMethod
    public boolean hasRecipe() {
        return this.isRecipe;
    }

    @ComputerMethod(
            nameOverride = "getRecipeProgress"
    )
    public int getOperatingTicks() {
        return this.operatingTicks;
    }

    @ComputerMethod
    public int getTicksRequired() {
        return this.ticksRequired;
    }

    public boolean getStockControl() {
        return this.stockControl;
    }

    public boolean getAutoMode() {
        return this.autoMode;
    }

    public void toggleStockControl() {
        if (!this.isRemote() && this.recipeFormula != null) {
            this.stockControl = !this.stockControl;
            if (this.stockControl) {
                this.organizeStock();
            }
        }

    }

    private void organizeStock() {
        if (this.recipeFormula != null) {
            Object2IntMap<HashedItem> storedMap = new Object2IntOpenHashMap();
            Iterator var2 = this.inputSlots.iterator();

            while(var2.hasNext()) {
                IInventorySlot inputSlot = (IInventorySlot)var2.next();
                ItemStack stack = inputSlot.getStack();
                if (!stack.isEmpty()) {
                    HashedItem hashed = HashedItem.create(stack);
                    storedMap.put(hashed, storedMap.getOrDefault(hashed, 0) + stack.getCount());
                }
            }

            IntSet unused = new IntOpenHashSet();

            int stored;
            IInventorySlot inputSlot;
            for(int i = 0; i < this.inputSlots.size(); ++i) {
                HashedItem hashedItem = this.stockControlMap[i];
                if (hashedItem == null) {
                    unused.add(i);
                } else if (storedMap.containsKey(hashedItem)) {
                    stored = storedMap.getInt(hashedItem);
                    int count = Math.min(hashedItem.getStack().getMaxStackSize(), stored);
                    if (count == stored) {
                        storedMap.removeInt(hashedItem);
                    } else {
                        storedMap.put(hashedItem, stored - count);
                    }

                    setSlotIfChanged((IInventorySlot)this.inputSlots.get(i), hashedItem, count);
                } else {
                    inputSlot = (IInventorySlot)this.inputSlots.get(i);
                    if (!inputSlot.isEmpty()) {
                        inputSlot.setEmpty();
                    }
                }
            }

            boolean empty = storedMap.isEmpty();
            IntIterator var11 = unused.iterator();

            while(var11.hasNext()) {
                stored = (Integer)var11.next();
                IInventorySlot slot = (IInventorySlot)this.inputSlots.get(stored);
                if (empty) {
                    if (!slot.isEmpty()) {
                        slot.setEmpty();
                    }
                } else {
                    empty = this.setSlotIfChanged(storedMap, slot);
                }
            }

            if (!empty) {
                Iterator var12 = this.inputSlots.iterator();

                do {
                    if (!var12.hasNext()) {
                        if (!storedMap.isEmpty()) {
                            Mekanism.logger.error("Critical error: Machine Assemblicator had items left over after organizing stock. Impossible!");
                        }

                        return;
                    }

                    inputSlot = (IInventorySlot)var12.next();
                } while(!inputSlot.isEmpty() || !this.setSlotIfChanged(storedMap, inputSlot));

            }
        }
    }

    private boolean setSlotIfChanged(Object2IntMap<HashedItem> storedMap, IInventorySlot inputSlot) {
        boolean empty = false;
        Object2IntMap.Entry<HashedItem> next = (Object2IntMap.Entry)storedMap.object2IntEntrySet().iterator().next();
        HashedItem item = (HashedItem)next.getKey();
        int stored = next.getIntValue();
        int count = Math.min(item.getStack().getMaxStackSize(), stored);
        if (count == stored) {
            storedMap.removeInt(item);
            empty = storedMap.isEmpty();
        } else {
            next.setValue(stored - count);
        }

        setSlotIfChanged(inputSlot, item, count);
        return empty;
    }

    private static void setSlotIfChanged(IInventorySlot slot, HashedItem item, int count) {
        ItemStack stack = item.createStack(count);
        if (!ItemStack.matches(slot.getStack(), stack)) {
            slot.setStack(stack);
        }

    }

    private void buildStockControlMap() {
        if (this.recipeFormula != null) {
            for(int i = 0; i < 9; ++i) {
                int j = i * 2;
                ItemStack stack = this.recipeFormula.getInputStack(i);
                if (stack.isEmpty()) {
                    this.stockControlMap[j] = null;
                    this.stockControlMap[j + 1] = null;
                } else {
                    HashedItem hashedItem = HashedItem.create(stack);
                    this.stockControlMap[j] = hashedItem;
                    this.stockControlMap[j + 1] = hashedItem;
                }
            }

        }
    }

    private ItemStack tryMoveToInput(ItemStack stack) {
        Iterator var2 = this.inputSlots.iterator();

        while(var2.hasNext()) {
            IInventorySlot stockSlot = (IInventorySlot)var2.next();
            stack = stockSlot.insertItem(stack, Action.EXECUTE, AutomationType.INTERNAL);
            if (stack.isEmpty()) {
                break;
            }
        }

        return stack;
    }

    private boolean tryMoveToOutput(ItemStack stack, Action action) {
        Iterator var3 = this.outputSlots.iterator();

        while(var3.hasNext()) {
            IInventorySlot outputSlot = (IInventorySlot)var3.next();
            stack = outputSlot.insertItem(stack, action, AutomationType.INTERNAL);
            if (stack.isEmpty()) {
                break;
            }
        }

        return stack.isEmpty();
    }


    public void encodeFormula() {
        if (!this.formulaSlot.isEmpty()) {
            System.out.println("Formula Slot not Empty");
            ItemStack formulaStack = this.formulaSlot.getStack();
            Item formulaItem = formulaStack.getItem();
            if (formulaItem instanceof ItemAssemblyFormula) {
                System.out.println("Is Instance of Assembly Formula");
                ItemAssemblyFormula item = (ItemAssemblyFormula)formulaItem;
                if (item.getInventory(formulaStack) == null) {
                    System.out.println("Inventory is Empty");
                    if(this.cachedRecipe==null){
                        System.out.println("no Recipe Present");
                        return;
                    }
                    AssemblyRecipeFormula formula = new AssemblyRecipeFormula(this.level, this.craftingGridSlots, this.cachedRecipe);
                    if (formula.isValidFormula()) {
                        System.out.println("Formula is Valid");
                        item.setInventory(formulaStack, formula.input);
                        this.markForSave();
                    }
                }
            }
        }

    }
    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.autoMode = nbt.getBoolean("auto");
        this.operatingTicks = nbt.getInt("progress");
        this.pulseOperations = nbt.getInt("pulse");
        this.stockControl = nbt.getBoolean("stockControl");
    }
    @Override
    public void saveAdditional(@Nonnull CompoundTag nbtTags) {
        super.saveAdditional(nbtTags);
        nbtTags.putBoolean("auto", this.autoMode);
        nbtTags.putInt("progress", this.operatingTicks);
        nbtTags.putInt("pulse", this.pulseOperations);
        nbtTags.putBoolean("stockControl", this.stockControl);
    }
    @Override
    public boolean canPulse() {
        return true;
    }
    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.SPEED) {
            this.ticksRequired = MekanismUtils.getTicks(this, 40);
        }

    }

    @Override
    public List<Component> getInfo(Upgrade upgrade) {
        return UpgradeUtils.getMultScaledInfo(this, upgrade);
    }

    public MachineEnergyContainer<MachineAssemblicatorBlockEntity> getEnergyContainer() {
        return this.energyContainer;
    }
    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(this::getAutoMode, (value) -> {
            this.autoMode = value;
        }));
        container.track(SyncableInt.create(this::getOperatingTicks, (value) -> {
            this.operatingTicks = value;
        }));
        container.track(SyncableInt.create(this::getTicksRequired, (value) -> {
            this.ticksRequired = value;
        }));
        container.track(SyncableBoolean.create(this::hasRecipe, (value) -> {
            this.isRecipe = value;
        }));
        container.track(SyncableBoolean.create(this::getStockControl, (value) -> {
            this.stockControl = value;
        }));
        container.track(SyncableBoolean.create(() -> {
            return this.recipeFormula != null;
        }, (hasFormula) -> {
            if (hasFormula) {
                if (this.recipeFormula == null && this.isRemote()) {
                    this.recipeFormula = new AssemblyRecipeFormula(this.getLevel(), NonNullList.withSize(12, ItemStack.EMPTY));
                }
            } else {
                this.recipeFormula = null;
            }

        }));

        for(int i = 0; i < 12; ++i) {
            int finalI = i;
            container.track(SyncableItemStack.create(() -> {
                return this.recipeFormula == null ? ItemStack.EMPTY : (ItemStack)this.recipeFormula.input.get(finalI);
            }, (stack) -> {
                if (!stack.isEmpty() && this.recipeFormula == null && this.isRemote()) {
                    this.recipeFormula = new AssemblyRecipeFormula(this.getLevel(), NonNullList.withSize(12, ItemStack.EMPTY));
                }

                if (this.recipeFormula != null) {
                    this.recipeFormula.setStack(this.getLevel(), finalI, stack);
                }

            }));
        }

    }

    @ComputerMethod
    private ItemStack getCraftingInputSlot(int slot) throws ComputerException {
        if (slot >= 0 && slot < this.craftingGridSlots.size()) {
            return ((IInventorySlot)this.craftingGridSlots.get(slot)).getStack();
        } else {
            throw new ComputerException("Crafting Input Slot '%d' is out of bounds, must be between 0 and %d.", new Object[]{slot, this.craftingGridSlots.size()});
        }
    }

    @ComputerMethod
    private int getCraftingOutputSlots() {
        return this.outputSlots.size();
    }

    @ComputerMethod
    private ItemStack getCraftingOutputSlot(int slot) throws ComputerException {
        int size = this.getCraftingOutputSlots();
        if (slot >= 0 && slot < size) {
            return ((IInventorySlot)this.outputSlots.get(slot)).getStack();
        } else {
            throw new ComputerException("Crafting Output Slot '%d' is out of bounds, must be between 0 and %d.", new Object[]{slot, size});
        }
    }

    @ComputerMethod
    private boolean hasValidFormula() {
        return this.recipeFormula != null && this.recipeFormula.isValidFormula();
    }

    @ComputerMethod(
            nameOverride = "getSlots"
    )
    private int computerGetSlots() {
        return this.inputSlots.size();
    }

    @ComputerMethod
    private ItemStack getItemInSlot(int slot) throws ComputerException {
        int size = this.computerGetSlots();
        if (slot >= 0 && slot < size) {
            return ((IInventorySlot)this.inputSlots.get(slot)).getStack();
        } else {
            throw new ComputerException("Slot '%d' is out of bounds, must be between 0 and %d.", new Object[]{slot, size});
        }
    }

    @ComputerMethod(
            nameOverride = "encodeFormula"
    )
    private void computerEncodeFormula() throws ComputerException {
        this.validateSecurityIsPublic();
        ItemStack formulaStack = this.formulaSlot.getStack();
        if (!formulaStack.isEmpty()) {
            Item var3 = formulaStack.getItem();
            if (var3 instanceof ItemCraftingFormula) {
                ItemCraftingFormula craftingFormula = (ItemCraftingFormula)var3;
                if ((this.recipeFormula == null || !this.recipeFormula.isValidFormula()) && craftingFormula.getInventory(formulaStack) == null) {
                    if (!this.hasRecipe()) {
                        throw new ComputerException("Encoding formulas require that there is a valid recipe to actually encode.");
                    }

                    this.encodeFormula();
                    return;
                }

                throw new ComputerException("Formula has already been encoded.");
            }
        }

        throw new ComputerException("No formula found.");
    }

    @ComputerMethod
    private void fillOrEmptyGrid() throws ComputerException {
        this.validateSecurityIsPublic();
        if (this.autoMode) {
            throw new ComputerException("Filling/Emptying the grid requires Auto-Mode to be disabled.");
        } else {
            this.moveItems();
        }
    }

    private void validateCanCraft() throws ComputerException {
        this.validateSecurityIsPublic();
        if (!this.hasRecipe()) {
            throw new ComputerException("Unable to perform craft as there is currently no matching recipe in the grid.");
        } else if (this.autoMode) {
            throw new ComputerException("Unable to perform craft as Auto-Mode is enabled.");
        }
    }

    @ComputerMethod
    private void craftSingleItem() throws ComputerException {
        this.validateCanCraft();
        this.craftSingle();
    }

    @ComputerMethod
    private void craftAvailableItems() throws ComputerException {
        this.validateCanCraft();
        this.craftAll();
    }

    private void validateHasValidFormula(String operation) throws ComputerException {
        this.validateSecurityIsPublic();
        if (!this.hasValidFormula()) {
            throw new ComputerException("%s requires a valid formula.", new Object[]{operation});
        }
    }

    @ComputerMethod(
            nameOverride = "getStockControl"
    )
    private boolean computerGetStockControl() throws ComputerException {
        this.validateHasValidFormula("Stock Control");
        return this.getStockControl();
    }

    @ComputerMethod
    private void setStockControl(boolean mode) throws ComputerException {
        this.validateHasValidFormula("Stock Control");
        if (this.stockControl != mode) {
            this.toggleStockControl();
        }

    }

    @ComputerMethod(
            nameOverride = "getAutoMode"
    )
    private boolean computerGetAutoMode() throws ComputerException {
        this.validateHasValidFormula("Auto-Mode");
        return this.getAutoMode();
    }

    @ComputerMethod
    private void setAutoMode(boolean mode) throws ComputerException {
        this.validateHasValidFormula("Auto-Mode");
        if (this.autoMode != mode) {
            this.nextMode();
        }

    }

}
