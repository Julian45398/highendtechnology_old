package com.akroZora.highendtechnology.registration;

import com.akroZora.highendtechnology.HighEndTechnology;
import com.akroZora.highendtechnology.registration.HighEndTechnologyContainerTypes;
import com.akroZora.highendtechnology.screen.gui.GuiMachineAssemblicator;
import com.akroZora.highendtechnology.screen.gui.GuiMachineWorkbench;
import com.akroZora.highendtechnology.screen.gui.GuiStorageCrate;
import mekanism.client.ClientRegistrationUtil;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = HighEndTechnology.MOD_ID,
        value = {Dist.CLIENT},
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ClientRegistration {


    public ClientRegistration() {
    }

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {

    }


    @SubscribeEvent(
            priority = EventPriority.LOW
    )
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {


        ClientRegistrationUtil.registerScreen(HighEndTechnologyContainerTypes.STORAGE_CRATE, GuiStorageCrate::new);
        ClientRegistrationUtil.registerScreen(HighEndTechnologyContainerTypes.MACHINE_WORKBENCH, GuiMachineWorkbench::new);
        ClientRegistrationUtil.registerScreen(HighEndTechnologyContainerTypes.MACHINE_ASSEMBLICATOR, GuiMachineAssemblicator::new);
    }

}
