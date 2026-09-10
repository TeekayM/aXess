package net.teekay.axess.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessPermission;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.screen.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class AxessClientMenus {
    public static boolean openNetworkManagerScreen() {
        Minecraft.getInstance().setScreen(new NetworkManagerScreen());
        return true;
    }

    public static boolean openNetworkDeletionScreen(AccessNetwork net) {
        Minecraft.getInstance().setScreen(new NetworkDeletionScreen(net));
        return true;
    }

    public static boolean openNetworkCreationScreen() {
        Minecraft.getInstance().setScreen(new NetworkCreationScreen());
        return true;
    }

    public static boolean openNetworkEditorScreen(AccessNetwork net) {
        Minecraft.getInstance().setScreen(new NetworkEditorScreen(net));
        return true;
    }

    public static boolean openIconSelectionScreen(Consumer<AxessIconRegistry.AxessIcon> e) {
        Minecraft.getInstance().pushGuiLayer(new IconSelectionScreen(e));
        return true;
    }

    public static boolean openColorSelectionScreen(Consumer<Color> e, Color initColor) {
        Minecraft.getInstance().pushGuiLayer(new ColorSelectionScreen(e, initColor));
        return true;
    }

    public static boolean openNetworkSelectionScreen(BiConsumer<AccessNetwork, AccessLevel> e) {
        Minecraft.getInstance().pushGuiLayer(new NetworkSelectionScreen(e));
        return true;
    }

    public static boolean openPlayerPermissionEditor(HashMap<UUID, EnumSet<AccessPermission>> initPerms) {
        Minecraft.getInstance().pushGuiLayer(new NetworkPlayerPermissionScreen(initPerms));
        return true;
    }

    public static boolean openPlayerSelectionScreen(Consumer<UUID> e) {
        Minecraft.getInstance().pushGuiLayer(new PlayerSelectionScreen(e));
        return true;
    }

    public static boolean openAccessLevelSelectionScreen(AccessNetwork net, Consumer<AccessLevel> e) {
        Minecraft.getInstance().pushGuiLayer(new AccessLevelSelectionScreen(net, e));
        return true;
    }

    public static boolean openKeycardOverridesScreen(ArrayList<Pair<AccessNetwork, AccessLevel>> pairs) {
        Minecraft.getInstance().pushGuiLayer(new KeycardReaderOverridesScreen(pairs));
        return true;
    }

    public static boolean openPermissionEditorScreen(UUID playerUUID, EnumSet<AccessPermission> permissions) {
        Minecraft.getInstance().pushGuiLayer(new PermissionEditorScreen(playerUUID, permissions));
        return true;
    }

    public static boolean popGuiLayer() {
        Minecraft.getInstance().popGuiLayer();
        return true;
    }

    public static boolean returnToScreen(Screen s) {
        Minecraft.getInstance().setScreen(s);
        return true;
    }

    public static boolean closeScreen() {
        Minecraft.getInstance().setScreen(null);
        return true;
    }
}
