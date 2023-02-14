package com.akroZora.highendtechnology.network;

import com.akroZora.highendtechnology.HighEndTechnology;
import mekanism.common.network.BasePacketHandler;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPacketHandler extends BasePacketHandler {
    private final SimpleChannel netHandler;


    public ModPacketHandler() {
        this.netHandler = createChannel(HighEndTechnology.rl(HighEndTechnology.MOD_ID), HighEndTechnology.instance.versionNumber);
    }

    @Override
    protected SimpleChannel getChannel() {
        return this.netHandler;
    }

    @Override
    public void initialize() {
        this.registerClientToServer(ModPacketGuiInteract.class, ModPacketGuiInteract::decode);

    }
}
