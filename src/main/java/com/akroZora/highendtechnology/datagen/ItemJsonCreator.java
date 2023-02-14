package com.akroZora.highendtechnology.datagen;

import com.akroZora.highendtechnology.block.custom.AssemblyStationBlock;
import com.akroZora.highendtechnology.block.custom.EnderiumBlock;
import com.akroZora.highendtechnology.registration.HighEndTechnologyItems;
import com.akroZora.highendtechnology.registration.HighendtechnologyBlocks;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.providers.IItemProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;

public class ItemJsonCreator extends ItemModelProvider {

    public ItemJsonCreator(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        List<IItemProvider> itemList = HighEndTechnologyItems.ITEMS.getAllItems();
        for(int i = 0;i<itemList.size();i++){
            itemList.get(i).asItem();

        }
        List<IBlockProvider> blockList = HighendtechnologyBlocks.BLOCKS.getAllBlocks();
        for(int i = 0;i< blockList.size(); i++){
            Block block = blockList.get(i).getBlock();
            if(block instanceof EnderiumBlock){
                for (int j = 0; j < 10; j++) {
                    System.out.println("this is an Enderium Block!!!!!!!"+i);
                }
            }
            if (block instanceof AssemblyStationBlock){
                for (int j = 0; j < 10; j++) {
                    System.out.println("this is an Assembly Block!!!!!!!"+i);
                }
            }
        }

    }

    /*private ItemModelBuilder registerSimpleItem(IItemProvider registryObject){
        ItemModelBuilder builder;
        if(registryObject.asItem() instanceof BlockItem){

        }
        return builder;
    }

     */



}
