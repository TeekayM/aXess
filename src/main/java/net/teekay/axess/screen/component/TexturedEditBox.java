package net.teekay.axess.screen.component;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class TexturedEditBox extends EditBox {

    private static ResourceLocation EDIT_BOX_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "button");

    private boolean redIfBlank;

    public TexturedEditBox(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        this(pFont, pX, pY, pWidth, pHeight, pMessage, true);
    }

    @SuppressWarnings("this-escape")
    public TexturedEditBox(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage, boolean redIfBlank) {
        super(pFont, pX, pY, pWidth, pHeight, pMessage);
        this.setBordered(false);
        this.redIfBlank = redIfBlank;
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        super.onClick(pMouseX, pMouseY);
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
        return super.isHovered() && this.mouseInBoundingBox && this.active;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!this.mouseInBoundingBox)
            return false;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }


    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.mouseInBoundingBox = hasBounds ? (pMouseX <= boundMaxX && pMouseX >= boundMinX && pMouseY <= boundMaxY && pMouseY >= boundMinY) : true;
        this.setTooltipDelay(Duration.ofDays(mouseInBoundingBox ? 0 : 10000000));
        boolean isHovering = isHoveredOrFocused();
        boolean isBlank = redIfBlank && this.getValue().isEmpty();
        pGuiGraphics.blitSprite(EDIT_BOX_TEXTURE, this.getX(), this.getY(), this.width, this.height);
        //pGuiGraphics.blitSprite(EDIT_BOX_TEXTURE, this.getX() + width - 1, this.getY(), this.width, this.height);
        int x = this.getX();
        int y = this.getY();
        this.setX(x + 4);
        this.setY(y + (this.height - 8) / 2);
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.setX(x);
        this.setY(y);
    }
}
