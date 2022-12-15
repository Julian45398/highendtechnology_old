package com.akroZora.highendtechnology.item;

import com.akroZora.highendtechnology.HighEndTechnology;
import com.akroZora.highendtechnology.item.custom.ModArmorMaterials;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, HighEndTechnology.MOD_ID);

    //Enderium Ingot
    public static final RegistryObject<Item> ENDERIUM_INGOT = ITEMS.register("enderium_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.HIGH_END_TECHNOLOGY)));

    //Raw Enderium
    public static final RegistryObject<Item> RAW_ENDERIUM = ITEMS.register("raw_enderium",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.HIGH_END_TECHNOLOGY)));

    //Enderium Gear
    public static final RegistryObject<Item> ENDERIUM_SWORD = ITEMS.register("enderium_sword",
            () -> new SwordItem(ModTiers.ENDERIUM,3,-2.2f,new Item.Properties().tab(ModCreativeModeTab.HIGH_END_TECHNOLOGY)));
    public static final RegistryObject<Item> ENDERIUM_PICKAXE = ITEMS.register("enderium_pickaxe",
            () -> new PickaxeItem(ModTiers.ENDERIUM,0,-2.8f,new Item.Properties().tab(ModCreativeModeTab.HIGH_END_TECHNOLOGY)));
    public static final RegistryObject<Item> ENDERIUM_AXE = ITEMS.register("enderium_axe",
            () -> new AxeItem(ModTiers.ENDERIUM,5,-3.2f,new Item.Properties().tab(ModCreativeModeTab.HIGH_END_TECHNOLOGY)));
    public static final RegistryObject<Item> ENDERIUM_SHOVEL = ITEMS.register("enderium_shovel",
                () -> new ShovelItem(ModTiers.ENDERIUM,1,-2.8f,new Item.Properties().tab(ModCreativeModeTab.HIGH_END_TECHNOLOGY)));
    public static final RegistryObject<Item> ENDERIUM_HOE = ITEMS.register("enderium_hoe",
            () -> new HoeItem(ModTiers.ENDERIUM,1,-2.8f,new Item.Properties().tab(ModCreativeModeTab.HIGH_END_TECHNOLOGY)));
    //Enderium Armor
    public static final RegistryObject<Item> ENDERIUM_HELMET = ITEMS.register("enderium_helmet",
            () -> new ArmorItem(ModArmorMaterials.ENDERIUM, EquipmentSlot.HEAD,new Item.Properties().tab(ModCreativeModeTab.HIGH_END_TECHNOLOGY)));
    public static final RegistryObject<Item> ENDERIUM_CHESTPLATE = ITEMS.register("enderium_chestplate",
            () -> new ArmorItem(ModArmorMaterials.ENDERIUM, EquipmentSlot.CHEST,new Item.Properties().tab(ModCreativeModeTab.HIGH_END_TECHNOLOGY)));
    public static final RegistryObject<Item> ENDERIUM_LEGGINGS = ITEMS.register("enderium_leggings",
            () -> new ArmorItem(ModArmorMaterials.ENDERIUM, EquipmentSlot.LEGS,new Item.Properties().tab(ModCreativeModeTab.HIGH_END_TECHNOLOGY)));
    public static final RegistryObject<Item> ENDERIUM_BOOTS = ITEMS.register("enderium_boots",
            () -> new ArmorItem(ModArmorMaterials.ENDERIUM, EquipmentSlot.FEET,new Item.Properties().tab(ModCreativeModeTab.HIGH_END_TECHNOLOGY)));


    public static void register(IEventBus eventBus) {ITEMS.register(eventBus);
    }
}
