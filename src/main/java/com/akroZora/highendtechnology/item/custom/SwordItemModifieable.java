package com.akroZora.highendtechnology.item.custom;

import com.akroZora.highendtechnology.registration.HighEndTechnologyConfig;
import com.akroZora.highendtechnology.item.ModTiers;
import com.google.common.collect.ImmutableMultimap;
import mekanism.common.config.MekanismConfig;
import mekanism.common.config.value.CachedValue;
import mekanism.common.lib.attribute.AttributeCache;
import mekanism.common.lib.attribute.IAttributeRefresher;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class SwordItemModifieable extends SwordItem implements IAttributeRefresher{


    private static final int damageModifier = HighEndTechnologyConfig.gearConfig.getEnderiumSwordDamage();

    private final AttributeCache attributeCache;


    public SwordItemModifieable(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
        this.attributeCache = new AttributeCache(this, new CachedValue[]{MekanismConfig.gear.armoredJetpackArmor});
    }

    public SwordItemModifieable(){


        this(ModTiers.ENDERIUM,damageModifier,2f,new Properties().rarity(Rarity.EPIC));


    }

    @Override
    public void addToBuilder(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {

    }
}
