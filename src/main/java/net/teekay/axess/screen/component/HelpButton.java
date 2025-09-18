package net.teekay.axess.screen.component;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HelpButton extends ImageButton {
    public static ResourceLocation HELP_BUTTON_TEX = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/help_button.png");

    public HelpButton(int leftPos, int topPos, int width, int height, Component tooltip) {
        super(leftPos+width-7-3, topPos+3, 7, 7, 0, 0, 0, HELP_BUTTON_TEX, 32, 64, btn -> {});
        this.setTooltip(Tooltip.create(tooltip));
    }
}
