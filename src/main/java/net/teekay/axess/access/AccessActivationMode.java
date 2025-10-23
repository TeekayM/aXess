package net.teekay.axess.access;

import net.minecraft.network.chat.Component;
import net.teekay.axess.Axess;

public enum AccessActivationMode {

    TOGGLE("TOGGLE"),
    PULSE("PULSE");

    public String id;
    public Component name;
    public Component description;

    AccessActivationMode(String id) {
        this.id = id;
        this.name = Component.translatable("gui." + Axess.MODID + ".activation_mode."+id);
        this.description = Component.translatable("gui." + Axess.MODID + ".activation_mode."+id+".description");
    }

    public String toString() {
        return this.id;
    }

    public Component getDescription() {
        return description;
    }

    public Component getName() {
        return name;
    }

}
