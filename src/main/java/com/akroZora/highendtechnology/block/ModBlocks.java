package com.akroZora.highendtechnology.block;

import com.akroZora.highendtechnology.HighEndTechnology;
import com.akroZora.highendtechnology.block.custom.AssemblyStationBlock;
import com.akroZora.highendtechnology.block.custom.EnderiumBlock;
import com.akroZora.highendtechnology.item.ModCreativeModeTab;
import com.akroZora.highendtechnology.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.akroZora.highendtechnology.block.custom.EnderiumBlock.LIT;


public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, HighEndTechnology.MOD_ID);

    //Enderium Block
    public static final RegistryObject<Block> ENDERIUM_BLOCK = registerBlock("enderium_block",
            () -> new EnderiumBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL).strength(12).requiresCorrectToolForDrops().
                    lightLevel((state) -> state.getValue(LIT) ? 3 : 0)),
            ModCreativeModeTab.HIGH_END_TECHNOLOGY);
    //Enderium Ore
    public static final RegistryObject<Block> ENDERIUM_ORE = registerBlock("enderium_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(10).requiresCorrectToolForDrops()),
            ModCreativeModeTab.HIGH_END_TECHNOLOGY);

    //Assembly Station
    public static final RegistryObject<Block> ASSEMBLY_STATION = registerBlock("assembly_station",
            () -> new AssemblyStationBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()),
            ModCreativeModeTab.HIGH_END_TECHNOLOGY);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
