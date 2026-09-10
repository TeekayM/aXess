package net.teekay.axess.screen.component;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;

import java.time.Duration;

public class TexturedCheckbox extends AbstractButton {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/checkbox.png");

    private boolean selected;
    private final Checkbox.OnValueChange callback;

    private boolean mouseInBoundingBox = false;
    private int boundMinX = -1;
    private int boundMaxX = -1;
    private int boundMinY = -1;
    private int boundMaxY = -1;
    private boolean hasBounds = false;

    public TexturedCheckbox(int pX, int pY, int pWidth, Component pMessage, boolean pSelected, Checkbox.OnValueChange callback) {
        super(pX, pY, pWidth, 20, pMessage);
        this.selected = pSelected;
        this.callback = callback;
    }

    public void setBounds(int minX, int minY, int maxX, int maxY) {
        boundMinX = minX;
        boundMaxX = maxX;
        boundMinY = minY;
        boundMaxY = maxY;
        hasBounds = true;
    }

    public boolean selected() {
        return this.selected;
    }

    @Override
    public void onPress() {
        this.selected = !this.selected;
        callback.onValueChange(null, this.selected);
    }

    @Override
    public boolean isHovered() {
        return super.isHovered() && this.mouseInBoundingBox;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!this.mouseInBoundingBox) return false;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.mouseInBoundingBox = hasBounds ? (pMouseX <= boundMaxX && pMouseX >= boundMinX && pMouseY <= boundMaxY && pMouseY >= boundMinY) : true;
        this.setTooltipDelay(Duration.ofDays(mouseInBoundingBox ? 0 : 999));
        RenderSystem.enableDepthTest();
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        pGuiGraphics.blit(TEXTURE, this.getX(), this.getY(),
                pMouseX >= this.getX() && pMouseY >= this.getY() && pMouseX < this.getX() + this.width && pMouseY < this.getY() + this.height ? 20.0F : 0.0F,
                this.selected ? 20.0F : 0.0F, 20, this.height, 64, 64);
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}