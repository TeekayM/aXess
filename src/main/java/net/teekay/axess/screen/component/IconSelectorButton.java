package net.teekay.axess.screen.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.client.AxessClientMenus;
import net.teekay.axess.registry.AxessIconRegistry;

import java.awt.*;
import java.util.function.Consumer;

public class IconSelectorButton extends HumbleImageButton {

    private static final ResourceLocation EMPTY_BUTTON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/empty_button.png");

    private final Consumer<AxessIconRegistry.AxessIcon> onSelectIcon;
    private final OnPress externalOnPress;

    public AxessIconRegistry.AxessIcon icon;

    public IconSelectorButton(
            int pX,
            int pY,
            int pWidth,
            int pHegiht,
            AxessIconRegistry.AxessIcon initIcon,
            Consumer<AxessIconRegistry.AxessIcon> onSelectIcon,
            OnPress onPress
    ) {
        super(
                pX,
                pY,
                pWidth,
                pHegiht,
                0,
                0,
                20,
                EMPTY_BUTTON_TEXTURE,
                32,
                64,
                btn -> {}
        );

        this.icon = initIcon;

        this.onSelectIcon = onSelectIcon;
        this.externalOnPress = onPress;
    }

    @Override
    public void onPress() {
        externalOnPress.onPress(this);

        AxessClientMenus.openIconSelectionScreen((icon) -> {
            this.icon = icon;
            onSelectIcon.accept(icon);
        });
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        g.blit(icon.TEXTURE, getX() + 1, getY() + 1, 0, 0,
                getWidth() - 2, getHeight() - 2, getWidth() - 2, getHeight() - 2);
    }
}
