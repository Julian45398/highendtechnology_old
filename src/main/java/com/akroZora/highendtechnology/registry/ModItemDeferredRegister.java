package com.akroZora.highendtechnology.registry;

import com.akroZora.highendtechnology.HighEndTechnology;
import mekanism.api.providers.IItemProvider;
import mekanism.api.text.EnumColor;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.Mekanism;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.item.ItemModule;
import mekanism.common.registration.WrappedForgeDeferredRegister;
import mekanism.common.registration.impl.EntityTypeRegistryObject;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.registration.impl.ModuleRegistryObject;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModItemDeferredRegister extends WrappedForgeDeferredRegister<Item> {
    private final List<IItemProvider> allItems = new ArrayList();

    public ModItemDeferredRegister(String modid) {
        super(modid, ForgeRegistries.ITEMS);
    }

    public static Item.Properties getMekBaseProperties() {
        return (new Item.Properties()).tab(HighEndTechnology.CREATIVE_MODE_TAB);
    }

    public ItemRegistryObject<Item> register(String name) {
        return this.register(name, Item::new);
    }

    public ItemRegistryObject<Item> registerUnburnable(String name) {
        return this.registerUnburnable(name, Item::new);
    }

    public ItemRegistryObject<Item> register(String name, Rarity rarity) {
        return this.register(name, (properties) -> {
            return new Item(properties.rarity(rarity));
        });
    }

    public ItemRegistryObject<Item> register(String name, EnumColor color) {
        return this.register(name, (properties) -> {
            return new Item(properties) {
                @Nonnull
                public Component getName(@Nonnull ItemStack stack) {
                    return TextComponentUtil.build(new Object[]{color, super.getName(stack)});
                }
            };
        });
    }

    public ItemRegistryObject<ItemModule> registerModule(ModuleRegistryObject<?> moduleDataSupplier) {
        return this.register("module_" + moduleDataSupplier.getInternalRegistryName(), () -> {
            return ModuleHelper.INSTANCE.createModuleItem(moduleDataSupplier, getMekBaseProperties());
        });
    }

    public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Function<Item.Properties, ITEM> sup) {
        return this.register(name, () -> {
            return sup.apply(getMekBaseProperties());
        });
    }

    public <ITEM extends Item> ItemRegistryObject<ITEM> registerUnburnable(String name, Function<Item.Properties, ITEM> sup) {
        return this.register(name, () -> {
            return sup.apply(getMekBaseProperties().fireResistant());
        });
    }

    public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Supplier<? extends ITEM> sup) {
        ItemRegistryObject<ITEM> registeredItem = (ItemRegistryObject)this.register(name, sup, ItemRegistryObject::new);
        this.allItems.add(registeredItem);
        return registeredItem;
    }

    public <ENTITY extends Mob> ItemRegistryObject<ForgeSpawnEggItem> registerSpawnEgg(EntityTypeRegistryObject<ENTITY> entityTypeProvider, int primaryColor, int secondaryColor) {
        return this.register(entityTypeProvider.getInternalRegistryName() + "_spawn_egg", (props) -> {
            return new ForgeSpawnEggItem(entityTypeProvider, primaryColor, secondaryColor, props);
        });
    }

    public List<IItemProvider> getAllItems() {
        return Collections.unmodifiableList(this.allItems);
    }
}
