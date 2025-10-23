package net.teekay.axess.access;

import net.minecraft.network.chat.Component;
import net.teekay.axess.Axess;

import java.io.Serializable;

public enum AccessCompareMode {

    SPECIFIC("SPECIFIC"),
    LESSER_THAN_OR_EQUAL("LESSER_THAN_OR_EQUAL"),
    BIGGER_THAN_OR_EQUAL("BIGGER_THAN_OR_EQUAL");

    public String id;
    public Component name;
    public Component description;

    AccessCompareMode(String id) {
        this.id = id;
        this.name = Component.translatable("gui." + Axess.MODID + ".compare_mode."+id);
        this.description = Component.translatable("gui." + Axess.MODID + ".compare_mode."+id+".description");
    }

    public String toString() {
        return this.id;
    }

    public Component getName() {
        return name;
    }

    public Component getDescription() {
        return description;
    }
}
