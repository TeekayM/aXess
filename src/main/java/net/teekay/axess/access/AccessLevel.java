package net.teekay.axess.access;

import net.minecraft.nbt.CompoundTag;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.utilities.AxessColors;
import org.checkerframework.checker.units.qual.A;

import java.awt.*;
import java.util.UUID;

public class AccessLevel {
    private String displayName;

    private final UUID uuid;
    private final UUID networkUUID;

    private int priority;

    private AxessIconRegistry.AxessIcon icon;

    private Color color;

    public AccessLevel(UUID networkUUID) {
        this(networkUUID, UUID.randomUUID());
    }

    public AccessLevel(UUID networkUUID, UUID uuid) {
        this.uuid = uuid;
        this.displayName = "New Access Level";
        this.priority = -1;
        this.networkUUID = networkUUID;
        this.color = AxessColors.MAIN;
        this.icon = AxessIconRegistry.NONE;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public AxessIconRegistry.AxessIcon getIcon() {
        return icon;
    }
    public void setIcon(AxessIconRegistry.AxessIcon icon) {
        this.icon = icon;
    }

    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }

    public UUID getUUID() { return this.uuid; }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putUUID("UUID", uuid);
        tag.putUUID("NetworkUUID", networkUUID);

        tag.putString("Name", displayName);
        tag.putString("Icon", icon.ID);
        tag.putInt("Color", color.getRGB());

        tag.putInt("Priority", priority);

        return tag;
    }

    public static AccessLevel fromNBT(CompoundTag tag) {
        UUID uuid = tag.getUUID("UUID");
        UUID networkUUID = tag.getUUID("NetworkUUID");

        AccessLevel newAccessLevel = new AccessLevel(networkUUID, uuid);

        newAccessLevel.displayName = tag.getString("Name");
        newAccessLevel.icon = AxessIconRegistry.getIcon(tag.getString("Icon"));
        newAccessLevel.color = new Color(tag.getInt("Color"));

        newAccessLevel.priority = tag.getInt("Priority");

        return newAccessLevel;
    }

}
