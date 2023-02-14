package com.akroZora.highendtechnology.registration;

import mekanism.common.registration.impl.SlurryDeferredRegister;

public class HiEndTechSlurries {
    public static final SlurryDeferredRegister SLURRIES = new SlurryDeferredRegister("highendtechnology");


    private HiEndTechSlurries(){
    }

    static {
        SLURRIES.register("enderium",(slurryBuilder -> slurryBuilder.color(-56168710)));
    }

}
