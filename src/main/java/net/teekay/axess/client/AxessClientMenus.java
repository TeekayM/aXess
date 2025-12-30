package net.teekay.axess.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.screen.*;
import net.teekay.axess.screen.component.AccessLevelEntry;

import java.awt.*;
import java.util.concurrent.Callable;
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

    public static boolean returnToScreen(Screen s) {
        Minecraft.getInstance().setScreen(s);
        return true;
    }

    public static boolean closeScreen() {
        Minecraft.getInstance().setScreen(null);
        return true;
    }
}
