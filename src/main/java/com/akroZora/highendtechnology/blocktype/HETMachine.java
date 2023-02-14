package com.akroZora.highendtechnology.blocktype;

import com.akroZora.highendtechnology.HighEndTechnologyLang;
import mekanism.api.Upgrade;
import mekanism.common.block.attribute.*;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.lib.math.Pos3D;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import java.util.EnumSet;
import java.util.function.Supplier;

public class HETMachine<TILE extends TileEntityMekanism> extends BlockTypeTile<TILE> {
    public HETMachine(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar, HighEndTechnologyLang description) {
        super(tileEntityRegistrar, description);
        this.add(new Attribute[]{(new AttributeParticleFX()).add(ParticleTypes.SMOKE, (rand) -> {
            return new Pos3D((double)(rand.nextFloat() * 0.6F - 0.3F), (double)(rand.nextFloat() * 6.0F / 16.0F), 0.52);
        }).add(DustParticleOptions.REDSTONE, (rand) -> {
            return new Pos3D((double)(rand.nextFloat() * 0.6F - 0.3F), (double)(rand.nextFloat() * 6.0F / 16.0F), 0.52);
        })});
        this.add(new Attribute[]{Attributes.ACTIVE_LIGHT, new AttributeStateFacing(), Attributes.INVENTORY, Attributes.SECURITY, Attributes.REDSTONE, Attributes.COMPARATOR});
        this.add(new Attribute[]{new AttributeUpgradeSupport(EnumSet.of(Upgrade.SPEED, Upgrade.ENERGY, Upgrade.MUFFLING))});
    }

    public static class MachineBuilder<MACHINE extends HETMachine<TILE>, TILE extends TileEntityMekanism, T extends MachineBuilder<MACHINE, TILE, T>> extends BlockTypeTile.BlockTileBuilder<MACHINE, TILE, T> {
        protected MachineBuilder(MACHINE holder) {
            super(holder);
        }

        public static <TILE extends TileEntityMekanism> MachineBuilder<HETMachine<TILE>, TILE, ?> createMachine(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar, HighEndTechnologyLang description) {
            return new MachineBuilder(new HETMachine(tileEntityRegistrar, description));
        }

        public static <TILE extends TileEntityMekanism> MachineBuilder<FactoryHETMachine<TILE>, TILE, ?> createFactoryMachine(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar, HighEndTechnologyLang description, FactoryType factoryType) {
            return new MachineBuilder(new FactoryHETMachine(tileEntityRegistrar, description, factoryType));
        }
    }

    public static class FactoryHETMachine<TILE extends TileEntityMekanism> extends HETMachine<TILE> {
        public FactoryHETMachine(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntitySupplier, HighEndTechnologyLang description, FactoryType factoryType) {
            super(tileEntitySupplier, description);
            this.add(new Attribute[]{new AttributeFactoryType(factoryType), new AttributeUpgradeable(() -> {
                return MekanismBlocks.getFactory(FactoryTier.BASIC, ((AttributeFactoryType)this.get(AttributeFactoryType.class)).getFactoryType());
            })});
        }
    }
}
