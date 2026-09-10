package net.teekay.axess.screen.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.time.Duration;

public class HumbleImageButton extends LegacyImageButton {

    public HumbleImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pOnPress);
    }

    public HumbleImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pTextureWidth, pTextureHeight, pOnPress);
    }

    public HumbleImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, OnPress pOnPress, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pTextureWidth, pTextureHeight, pOnPress, pMessage);
    }

    private boolean mouseInBoundingBox = false;

    private int boundMinX = -1;

    private int boundMaxX = -1;

    private int boundMinY = -1;

    private int boundMaxY = -1;

    private boolean hasBounds = false;

    public void setBounds(int minX, int minY, int maxX, int maxY) {
        boundMinX = minX;
        boundMaxX = maxX;
        boundMinY = minY;
        boundMaxY = maxY;
        hasBounds = true;
    }

    @Override
    public boolean isHovered() {
        return super.isHovered() && this.mouseInBoundingBox;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!this.mouseInBoundingBox)
            return false;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.mouseInBoundingBox = hasBounds ? (pMouseX <= boundMaxX && pMouseX >= boundMinX && pMouseY <= boundMaxY && pMouseY >= boundMinY) : true;
        this.setTooltipDelay(Duration.ofDays(mouseInBoundingBox ? 0 : 999));
    }

    @Override
    public boolean isFocused() {
        return false;
    }
}
