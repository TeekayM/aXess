package net.teekay.axess.screen.component;

import net.minecraft.resources.ResourceLocation;

public class DraggableImageButton extends HumbleImageButton {

    OnPress onRelease;
    public boolean dragging = false;

    public DraggableImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, ResourceLocation pResourceLocation, OnPress pOnPress, OnPress pOnRelease) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pResourceLocation, pOnPress);
        this.onRelease = pOnRelease;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        onRelease.onPress(this);
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean isHoveredOrFocused() {
        return dragging;
    }
}
