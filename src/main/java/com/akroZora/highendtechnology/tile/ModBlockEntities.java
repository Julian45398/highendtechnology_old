package com.akroZora.highendtechnology.tile;

import com.akroZora.highendtechnology.HighEndTechnology;
import com.akroZora.highendtechnology.registration.HighendtechnologyBlocks;
import com.akroZora.highendtechnology.tile.custom.AssemblyStationBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, HighEndTechnology.MOD_ID);

    public static final RegistryObject<BlockEntityType<AssemblyStationBlockEntity>> ASSEMBLY_STATION_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("assembly_station_block_entity", () ->
                    BlockEntityType.Builder.of(AssemblyStationBlockEntity::new,
                            HighendtechnologyBlocks.ASSEMBLY_STATION.getBlock()).build(null));



    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
