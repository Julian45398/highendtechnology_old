package com.akroZora.highendtechnology.registration;

import com.akroZora.highendtechnology.HighEndTechnologyLang;
import com.akroZora.highendtechnology.blocktype.ModMachine;
import com.akroZora.highendtechnology.content.blocktype.CustomShapes;
import com.akroZora.highendtechnology.content.blocktype.ModFactoryType;
import com.akroZora.highendtechnology.tile.custom.MachineAssemblicatorBlockEntity;
import com.akroZora.highendtechnology.tile.custom.TileEntityMachineWorkbench;
import com.akroZora.highendtechnology.tile.custom.TileEntityMetalPress;
import com.akroZora.highendtechnology.tile.custom.TileEntityStorageCrate;
import com.akroZora.highendtechnology.tile.factory.TileEntityMetalPressFactory;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import mekanism.api.Upgrade;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeCustomSelectionBox;
import mekanism.common.block.attribute.AttributeStateFacing;
import mekanism.common.block.attribute.AttributeUpgradeable;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.Factory;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.registries.MekanismSounds;
import mekanism.common.tier.FactoryTier;
import mekanism.common.util.MekanismUtils;
import net.minecraft.core.BlockPos;

import java.util.EnumSet;
import java.util.function.Supplier;

public class HighEndTechnologyBlockTypes {

    private static final Table<FactoryTier, ModFactoryType, Factory<?>> FACTORIES = HashBasedTable.create();
    public static final BlockTypeTile<TileEntityStorageCrate> STORAGE_CRATE = BlockTypeTile.BlockTileBuilder.createBlock(() -> {
        return HighendtechnologyTileEntityTypes.STORAGE_CRATE;
    }, HighEndTechnologyLang.DESCRIPTION_STORAGE_CRATE).withGui(() -> {
        return HighEndTechnologyContainerTypes.STORAGE_CRATE;
    }).with(new AttributeStateFacing()).build();

    public static final BlockTypeTile<TileEntityMachineWorkbench> MACHINE_WORKBENCH =  BlockTypeTile.BlockTileBuilder
            .createBlock(() -> HighendtechnologyTileEntityTypes.MACHINE_WORKBENCH, HighEndTechnologyLang.DESCRIPTION_STORAGE_CRATE)
            .withGui(() -> HighEndTechnologyContainerTypes.MACHINE_WORKBENCH)
            .with(new AttributeStateFacing()).withCustomShape(CustomShapes.MACHINE_WORKBENCH)
                .with(AttributeCustomSelectionBox.JSON).withBounding((pos, state, builder) -> {
        builder.add(pos.above());
        BlockPos rightPos = pos.relative(MekanismUtils.getRight(Attribute.getFacing(state)));
        builder.add(rightPos);
        builder.add(rightPos.above());
    }).build();

    /*public static final ModMachine<TileEntityMetalPress> PRESS = ModMachine.ModMachineBuilder
            .createFactoryMachine(() -> HighendtechnologyTileEntityTypes.PRESS, HighEndTechnologyLang.PRESSING, ModFactoryType.PRESSING)
            .withGui(() -> HighEndTechnologyContainerTypes.PRESS)
            .withSound(MekanismSounds.CRUSHER)
            .withEnergyConfig(MekanismConfig.usage.crusher, MekanismConfig.storage.crusher)
            .withComputerSupport("press")
            .build();

     */


    //public static final ModMachine.ModFactoryMachine<TileEntityMetalPressFactory> BASIC_PRESS = createPressingFactory(FactoryTier.BASIC,HighendtechnologyTileEntityTypes.PRESS, )

    public static final ModMachine<MachineAssemblicatorBlockEntity> MACHINE_ASSEMBLICATOR = ModMachine.ModMachineBuilder.createMachine(() -> {
        return HighendtechnologyTileEntityTypes.MACHINE_ASSEMBLICATOR;
    }, HighEndTechnologyLang.DESCRIPTION_MACHINE_CRAFTING_BENCH).withGui(() -> {
        return HighEndTechnologyContainerTypes.MACHINE_ASSEMBLICATOR;
    }).withEnergyConfig(MekanismConfig.usage.formulaicAssemblicator, MekanismConfig.storage.formulaicAssemblicator)
            .withSupportedUpgrades(EnumSet.of(Upgrade.SPEED, Upgrade.ENERGY))
            .withComputerSupport("machineAssemblicator")
            .build();


    /*private static <TILE extends TileEntityMetalPressFactory> ModMachine<TILE> createPressingFactory(FactoryTier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock){
        return ModMachine.ModMachineBuilder.createMachine(tile, HighEndTechnologyLang.DESCRIPTION_MACHINE_CRAFTING_BENCH)
                .withGui(() -> HighEndTechnologyContainerTypes.PRESS).withEnergyConfig(MekanismConfig.storage.crusher,MekanismConfig.usage.crusher)
                .with(new AttributeUpgradeable(upgradeBlock)).withSupportedUpgrades(EnumSet.of(Upgrade.SPEED, Upgrade.ENERGY, Upgrade.MUFFLING))
                .withComputerSupport(tier.name())
                .build();

    }

     */

    public static Factory<?> getFactory(FactoryTier tier, ModFactoryType type) {
        return FACTORIES.get(tier, type);
    }

}
