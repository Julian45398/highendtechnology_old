package com.akroZora.highendtechnology;

import com.akroZora.highendtechnology.registration.*;
import com.akroZora.highendtechnology.config.HETModConfig;
import com.akroZora.highendtechnology.integration.HETIntegration;
import com.akroZora.highendtechnology.network.ModPacketHandler;
import com.akroZora.highendtechnology.recipe.ModRecipes;
import com.akroZora.highendtechnology.screen.AssemblyStationScreen;
import com.akroZora.highendtechnology.screen.ModMenuTypes;
import com.akroZora.highendtechnology.tile.ModBlockEntities;
import com.mojang.logging.LogUtils;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.common.Mekanism;
import mekanism.common.lib.Version;
import mekanism.common.registries.MekanismSlurries;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HighEndTechnology.MOD_ID)
public class HighEndTechnology {
    public static final String MOD_ID = "highendtechnology";

    public static HighEndTechnology instance;
    public static HETIntegration integration;

    public final Version versionNumber;

    public final ModPacketHandler packetHandler;


    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final CreativeModeTab CREATIVE_MODE_TAB = new HighEndTechnologyCreativeTab();

    public HighEndTechnology()
    {
        instance = this;
        integration = new HETIntegration();

        //Register the Config files
        HighEndTechnologyConfig.registerConfigs(ModLoadingContext.get());
        // Register the setup method for modloading
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::onConfigLoad);

        HighEndTechnologyItems.ITEMS.register(eventBus);
        HighendtechnologyBlocks.BLOCKS.register(eventBus);
        ModBlockEntities.register(eventBus);

        HighEndTechnologyContainerTypes.CONTAINER_TYPES.register(eventBus);

        System.out.println("registering TileEntities");
        HighendtechnologyTileEntityTypes.TILE_ENTITY_TYPES.register(eventBus);
        System.out.println("registered TileEntities");
        ModMenuTypes.register(eventBus);

        ModRecipes.register(eventBus);


        // Register the enqueueIMC method for modloading
        eventBus.addListener(this::enqueueIMC);
        eventBus.addListener(this::processIMC);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::commonSetup);

        HiEndTechSlurries.SLURRIES.createAndRegister(eventBus, Slurry.class, (builder) -> {
            return builder.hasTags().setDefaultKey(rl("empty"));
        });

        this.versionNumber = new Version(ModLoadingContext.get().getActiveContainer());
        this.packetHandler = new ModPacketHandler();


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLClientSetupEvent event){

    }

    private void clientSetup(final FMLClientSetupEvent event){
        MenuScreens.register(ModMenuTypes.ASSEMBLY_STATION_MENU.get(), AssemblyStationScreen::new);
    }
    private void onConfigLoad(ModConfigEvent configEvent){
        ModConfig config = configEvent.getConfig();
        //Make sure it is for the same modid as us
        if (config.getModId().equals(MOD_ID) && config instanceof HETModConfig hetConfig) {
            hetConfig.clearCache();
        }
    }

    private void commonSetup(FMLCommonSetupEvent event){
        this.packetHandler.initialize();
    }


    private void enqueueIMC(final InterModEnqueueEvent event)
    {
       
    }

    private void processIMC(final InterModProcessEvent event)
    {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static ModPacketHandler packetHandler() {
        return instance.packetHandler;
    }



    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
        {
            // Register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
