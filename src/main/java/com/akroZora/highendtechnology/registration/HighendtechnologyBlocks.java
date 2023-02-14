package com.akroZora.highendtechnology.registration;

import com.akroZora.highendtechnology.HighEndTechnology;
import com.akroZora.highendtechnology.block.HighEndTechnologyItemBlockMachine;
import com.akroZora.highendtechnology.block.HETItemBlockTooltip;
import com.akroZora.highendtechnology.block.custom.AssemblyStationBlock;
import com.akroZora.highendtechnology.block.custom.EnderiumBlock;
import com.akroZora.highendtechnology.block.custom.StorageCrateBlock;
import com.akroZora.highendtechnology.registry.HETBlockDeferredRegister;
import com.akroZora.highendtechnology.tile.custom.MachineAssemblicatorBlockEntity;
import com.akroZora.highendtechnology.tile.custom.TileEntityMachineWorkbench;
import mekanism.common.block.basic.BlockResource;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.item.block.ItemBlockResource;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.resource.BlockResourceInfo;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import static com.akroZora.highendtechnology.block.custom.EnderiumBlock.LIT;

public class HighendtechnologyBlocks {
    public static final HETBlockDeferredRegister BLOCKS = new HETBlockDeferredRegister(HighEndTechnology.MOD_ID);

    public static final BlockRegistryObject<BlockTile<MachineAssemblicatorBlockEntity, Machine<MachineAssemblicatorBlockEntity>>, HighEndTechnologyItemBlockMachine> MACHINE_ASSEMBLICATOR;

    public static final BlockRegistryObject<StorageCrateBlock, HETItemBlockTooltip<StorageCrateBlock>> STORAGE_CRATE;
    public static final BlockRegistryObject<BlockTile.BlockTileModel<TileEntityMachineWorkbench, BlockTypeTile<TileEntityMachineWorkbench>>, HighEndTechnologyItemBlockMachine> MACHINE_WORKBENCH;



    public static final BlockRegistryObject<AssemblyStationBlock, BlockItem> ASSEMBLY_STATION;
    public static final BlockRegistryObject<EnderiumBlock, BlockItem> ENDERIUM_BLOCK;
    public static final BlockRegistryObject<Block, BlockItem> ENDERIUM_ORE;
    public static final BlockRegistryObject<Block, BlockItem> CRYSTAL_BLOCK;



    //public static final BlockRegistryObject<BlockResource, ItemBlockResource> TEST_OBSIDIAN_BLOCK;

    private HighendtechnologyBlocks() {
    }

    private static BlockRegistryObject<BlockResource, ItemBlockResource> registerResourceBlock(BlockResourceInfo resource) {
        return BLOCKS.registerDefaultProperties("block_" + resource.getRegistrySuffix(), () -> {
            return new BlockResource(resource);
        }, (block, properties) -> {
            if (!block.getResourceInfo().burnsInFire()) {
                properties = properties.fireResistant();
            }

            return new ItemBlockResource(block, properties);
        });
    }

    static {
        CreativeModeTab tab = HighEndTechnology.CREATIVE_MODE_TAB;
        //TEST_OBSIDIAN_BLOCK = registerResourceBlock(BlockResourceInfo.REFINED_OBSIDIAN);
        ENDERIUM_BLOCK = BLOCKS.register("enderium_block", () ->
                new EnderiumBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL).strength(12).requiresCorrectToolForDrops().
                        lightLevel((state) -> state.getValue(LIT) ? 3 : 0)), enderiumBlock -> new BlockItem(enderiumBlock,new Item.Properties().rarity(Rarity.EPIC).tab(tab)));
        CRYSTAL_BLOCK = BLOCKS.register("crystal_block",BlockBehaviour.Properties.of(Material.AMETHYST, MaterialColor.COLOR_LIGHT_GREEN).strength(3).
                        lightLevel((state) -> 5), new Item.Properties().rarity(Rarity.EPIC));
        ENDERIUM_ORE = BLOCKS.register("enderium_ore", () ->
                new Block(BlockBehaviour.Properties.of(Material.STONE).strength(10).requiresCorrectToolForDrops()));
        ASSEMBLY_STATION = BLOCKS.register("assembly_station", () ->
                new AssemblyStationBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));

        if(HighEndTechnology.integration.ecologicsLoaded){

        }else {

        }

        STORAGE_CRATE = BLOCKS.register("storage_crate", () ->
                new StorageCrateBlock(HighendtechnologyBlockTypes.STORAGE_CRATE), HETItemBlockTooltip::new);

        MACHINE_WORKBENCH = BLOCKS.register("machine_workbench", () -> new BlockTile.BlockTileModel<>(HighendtechnologyBlockTypes.MACHINE_WORKBENCH), HighEndTechnologyItemBlockMachine::new);

        MACHINE_ASSEMBLICATOR = BLOCKS.register("machine_assemblicator", () ->
                new BlockTile(HighendtechnologyBlockTypes.MACHINE_ASSEMBLICATOR), HighEndTechnologyItemBlockMachine::new);
    }
}
