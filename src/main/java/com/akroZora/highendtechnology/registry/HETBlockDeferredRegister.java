package com.akroZora.highendtechnology.registry;

import com.akroZora.highendtechnology.HighEndTechnology;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.registration.DoubleForgeDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class HETBlockDeferredRegister extends DoubleForgeDeferredRegister<Block, Item> {
    private final List<IBlockProvider> allBlocks = new ArrayList<>();

    public HETBlockDeferredRegister(String modid) {

        super(modid, ForgeRegistries.BLOCKS, ForgeRegistries.ITEMS);
    }

    public BlockRegistryObject<Block, BlockItem> register(String name, BlockBehaviour.Properties properties) {
        return this.registerDefaultProperties(name, () -> {
            return new Block(BlockStateHelper.applyLightLevelAdjustments(properties));
        }, BlockItem::new);
    }
    public BlockRegistryObject<Block, BlockItem> register(String name, BlockBehaviour.Properties properties, Item.Properties itemProperties) {
                return this.registerDefaultProperties(name, () -> {
            return new Block(BlockStateHelper.applyLightLevelAdjustments(properties));
        }, BlockItem::new, itemProperties);
    }

    public <BLOCK extends Block> BlockRegistryObject<BLOCK, BlockItem> register(String name, Supplier<? extends BLOCK> blockSupplier) {
        return this.registerDefaultProperties(name, blockSupplier, BlockItem::new);
    }

    public <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerDefaultProperties(String name, Supplier<? extends BLOCK> blockSupplier, BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        return (BlockRegistryObject<BLOCK, ITEM>) this.register(name, blockSupplier, (block) -> {
            return (BlockItem)itemCreator.apply(block, HETItemDeferredRegister.getHighEndTechnologyBaseProperties());
        });
    }

    public <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerDefaultProperties(String name, Supplier<? extends BLOCK> blockSupplier, BiFunction<BLOCK, Item.Properties, ITEM> itemCreator, Item.Properties itemProperties) {
        Item.Properties finalItemProperties = itemProperties.tab(HighEndTechnology.CREATIVE_MODE_TAB);
        return (BlockRegistryObject<BLOCK, ITEM>) this.register(name, blockSupplier, (block) -> {
            return (BlockItem)itemCreator.apply(block, finalItemProperties);
        });
    }

    public <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> register(String name, Supplier<? extends BLOCK> blockSupplier, Function<BLOCK, ITEM> itemCreator) {
        BlockRegistryObject<BLOCK, ITEM> registeredBlock = (BlockRegistryObject<BLOCK, ITEM>)this.register(name, blockSupplier, itemCreator, BlockRegistryObject::new);
        this.allBlocks.add(registeredBlock);
        return registeredBlock;
    }

    public List<IBlockProvider> getAllBlocks() {
        return Collections.unmodifiableList(this.allBlocks);
    }
}
