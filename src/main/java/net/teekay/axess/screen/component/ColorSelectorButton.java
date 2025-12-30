package net.teekay.axess.screen.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.client.AxessClientMenus;

import java.awt.*;
import java.util.function.Consumer;

public class ColorSelectorButton extends HumbleImageButton {

    private static final ResourceLocation EMPTY_BUTTON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/empty_button.png");

    private final Consumer<Color> onSelectColor;
    private final OnPress externalOnPress;

    public Color sColor;

    public ColorSelectorButton(
            int pX,
            int pY,
            int pWidth,
            int pHeight,
            Color initColor,
            Consumer<Color> onSelectColor,
            OnPress onPress
    ) {
        super(
                pX,
                pY,
                pWidth,
                pHeight,
                0,
                0,
                20,
                EMPTY_BUTTON_TEXTURE,
                32,
                64,
                btn -> {}
        );

        this.sColor = initColor;
        this.onSelectColor = onSelectColor;
        this.externalOnPress = onPress;
    }

    @Override
    public void onPress() {
        externalOnPress.onPress(this);

        AxessClientMenus.openColorSelectionScreen(color -> {
            this.sColor = color;
            onSelectColor.accept(color);
        }, this.sColor);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        g.fill(
                getX() + 2,
                getY() + 2,
                getX() + getWidth() - 2,
                getY() + getHeight() - 2,
                sColor.getRGB()
        );
    }
}
