package com.akroZora.highendtechnology;

import mekanism.api.text.ILangEntry;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import net.minecraft.Util;

public enum HighEndTechnologyLang implements ILangEntry {

    HIGH_END_TECHNOLOGY("constants", "mod_name"),



    DESCRIPTION_STORAGE_CRATE("description","storage_crate"),
    DESCRIPTION_MACHINE_CRAFTING_BENCH("description","machine_crafting_bench");






    private final String key;

    private HighEndTechnologyLang(String type, String path) {
        this(Util.makeDescriptionId(type, HighEndTechnology.rl(path)));
    }

    private HighEndTechnologyLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return this.key;
    }
}
