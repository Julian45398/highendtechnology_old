package com.akroZora.highendtechnology.registration;

import com.akroZora.highendtechnology.HighEndTechnology;
import com.akroZora.highendtechnology.config.GearConfig;
import com.akroZora.highendtechnology.item.ModTiers;
import com.akroZora.highendtechnology.item.custom.ItemAssemblyFormula;
import com.akroZora.highendtechnology.item.custom.ModArmorMaterials;
import com.akroZora.highendtechnology.registry.ModItemDeferredRegister;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.resource.IResource;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class HighEndTechnologyItems {

    public static final ModItemDeferredRegister ITEMS = new ModItemDeferredRegister(HighEndTechnology.MOD_ID);


    public static final Table<ResourceType, PrimaryResource, ItemRegistryObject<Item>> PROCESSED_RESOURCES = HashBasedTable.create();

    public static final ItemRegistryObject<Item> ENDERIUM_INGOT;
    public static final ItemRegistryObject<Item> RAW_ENDERIUM;
    public static final ItemRegistryObject<SwordItem> ENDERIUM_SWORD;
    public static final ItemRegistryObject<PickaxeItem> ENDERIUM_PICKAXE;
    public static final ItemRegistryObject<AxeItem> ENDERIUM_AXE;
    public static final ItemRegistryObject<ShovelItem> ENDERIUM_SHOVEL;
    public static final ItemRegistryObject<HoeItem> ENDERIUM_HOE;
    public static final ItemRegistryObject<ArmorItem> ENDERIUM_HELMET;
    public static final ItemRegistryObject<ArmorItem> ENDERIUM_CHESTPLATE;
    public static final ItemRegistryObject<ArmorItem> ENDERIUM_LEGGINGS;
    public static final ItemRegistryObject<ArmorItem> ENDERIUM_BOOTS;
    public static final ItemRegistryObject<ItemAssemblyFormula> ASSEMBLY_FORMULA;



    //Enderium Ingot

    HighEndTechnologyItems(){
    }

    private static ItemRegistryObject<Item> registerResource(ResourceType type, IResource resource) {
        ModItemDeferredRegister var10000 = ITEMS;
        String var10001 = type.getRegistryPrefix();
        return var10000.register(var10001 + "_" + resource.getRegistrySuffix());
    }

    private static ItemRegistryObject<Item> registerUnburnableResource(ResourceType type, IResource resource) {
        ModItemDeferredRegister var10000 = ITEMS;
        String var10001 = type.getRegistryPrefix();
        return var10000.registerUnburnable(var10001 + "_" + resource.getRegistrySuffix());
    }

    private static ItemRegistryObject<Item> registerMetal(String namePrefix,int slurryColor,Rarity rarity){

        HiEndTechSlurries.SLURRIES.register(namePrefix,(slurryBuilder -> slurryBuilder.color(slurryColor)));

        ITEMS.register(namePrefix+"_dust",rarity);
        ITEMS.register(namePrefix+"_shard",rarity);
        ITEMS.register(namePrefix+"_clump",rarity);
        ITEMS.register(namePrefix+"_crystal",rarity);
        ITEMS.register(namePrefix+"_nugget",rarity);

        return ITEMS.register(namePrefix+"_ingot", rarity);
    }

    static {
        ENDERIUM_INGOT = ITEMS.register("enderium_ingot", Rarity.EPIC);
        RAW_ENDERIUM = ITEMS.register("raw_enderium", Rarity.EPIC);
        ENDERIUM_SWORD = ITEMS.register("enderium_sword",(properties) -> new SwordItem(ModTiers.ENDERIUM,new GearConfig().getEnderiumSwordDamage(), 0f,properties.rarity(Rarity.EPIC)));
        ENDERIUM_PICKAXE = ITEMS.register("enderium_pickaxe", (properties) -> new PickaxeItem(ModTiers.ENDERIUM,0,-2.8f,properties));
        ENDERIUM_AXE = ITEMS.register("enderium_axe", (properties) -> new AxeItem(ModTiers.ENDERIUM,5,-3.2f,properties));
        ENDERIUM_SHOVEL = ITEMS.register("enderium_shovel", (properties) -> new ShovelItem(ModTiers.ENDERIUM,1,-2.8f,properties));
        ENDERIUM_HOE = ITEMS.register("enderium_hoe", (properties) -> new HoeItem(ModTiers.ENDERIUM,1,-2.8f, properties));
        ENDERIUM_HELMET = ITEMS.register("enderium_helmet", (properties) -> new ArmorItem(ModArmorMaterials.ENDERIUM, EquipmentSlot.HEAD,properties));
        ENDERIUM_CHESTPLATE = ITEMS.register("enderium_chestplate", (properties) -> new ArmorItem(ModArmorMaterials.ENDERIUM, EquipmentSlot.CHEST,properties));
        ENDERIUM_LEGGINGS = ITEMS.register("enderium_leggings", (properties) -> new ArmorItem(ModArmorMaterials.ENDERIUM, EquipmentSlot.LEGS,properties));
        ENDERIUM_BOOTS = ITEMS.register("enderium_boots", (properties) -> new ArmorItem(ModArmorMaterials.ENDERIUM, EquipmentSlot.FEET,properties));

        ASSEMBLY_FORMULA = ITEMS.register("assembly_formula", ItemAssemblyFormula::new);
    }

    //Enderium Gear
    //public static final RegistryObject<Item> ENDERIUM_SWORD = ITEM.register("enderium_sword",
       //     () -> new SwordItem(ModTiers.ENDERIUM,3,-2.2f,new Item.Properties().tab(HighEndTechnology.CREATIVE_MODE_TAB)));



    //Enderium Armor
    /*
    public static final RegistryObject<Item> ENDERIUM_CHESTPLATE = ITEM.register("enderium_chestplate",
            () -> new ArmorItem(ModArmorMaterials.ENDERIUM, EquipmentSlot.CHEST,new Item.Properties().tab(HighEndTechnology.CREATIVE_MODE_TAB)));
    public static final RegistryObject<Item> ENDERIUM_LEGGINGS = ITEM.register("enderium_leggings",
            () -> new ArmorItem(ModArmorMaterials.ENDERIUM, EquipmentSlot.LEGS,new Item.Properties().tab(HighEndTechnology.CREATIVE_MODE_TAB)));
    public static final RegistryObject<Item> ENDERIUM_BOOTS = ITEM.register("enderium_boots",
            () -> new ArmorItem(ModArmorMaterials.ENDERIUM, EquipmentSlot.FEET,new Item.Properties().tab(HighEndTechnology.CREATIVE_MODE_TAB)));

     */


}
