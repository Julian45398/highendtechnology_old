package com.akroZora.highendtechnology.integration;

import net.minecraftforge.fml.ModList;

public class HETIntegration {
    public boolean infernalExpansionLoaded;
    public boolean ecologicsLoaded;
    public boolean mekanismToolsLoaded;
    public boolean makanismAdditionsLoaded;


    public HETIntegration(){
        ModList modList = ModList.get();
        infernalExpansionLoaded = modList.isLoaded("infernalexp");
        ecologicsLoaded = modList.isLoaded("ecologics");
        mekanismToolsLoaded = modList.isLoaded("mekanismtools");
    }

}
