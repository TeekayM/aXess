package net.teekay.axess.screen.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.MathUtilities;

import java.time.Duration;

public class TexturedButton extends Button {

    public TexturedButton(int x, int y, int width, int height, Component title, OnPress onPress) {
        super(x, y, width, height, title, onPress, btn -> {
            return Component.empty();
        });
    }

    public static ResourceLocation DISABLED_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "button_disabled");
    public static ResourceLocation SELECTED_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "button_selected");

    public float timePassed = 0f;

    public int textPaddingLeft = 0;

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
    public boolean isFocused() {
        return false;
    }

    @Override
    public boolean isHoveredOrFocused() {
        return super.isHoveredOrFocused() && this.mouseInBoundingBox;
    }

    public enum ButtonColor {

        NORMAL(ResourceLocation.fromNamespaceAndPath(Axess.MODID, "button"), AxessColors.MAIN.getRGB()),
        RED(ResourceLocation.fromNamespaceAndPath(Axess.MODID, "button_red"), AxessColors.RED.getRGB()),
        GREEN(ResourceLocation.fromNamespaceAndPath(Axess.MODID, "button_green"), AxessColors.GREEN.getRGB()),
        GREEN_LESS(ResourceLocation.fromNamespaceAndPath(Axess.MODID, "button_green_less"), AxessColors.GREEN_LESS.getRGB());

        ResourceLocation texture;

        int color;

        public ResourceLocation getTexture() {
            return texture;
        }

        public int getColor() {
            return color;
        }

        ButtonColor(ResourceLocation texture, int color) {
            this.texture = texture;
            this.color = color;
        }
    }

    public ButtonColor getColor() {
        return ButtonColor.NORMAL;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!this.mouseInBoundingBox)
            return false;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.mouseInBoundingBox = hasBounds ? (mouseX <= boundMaxX && mouseX >= boundMinX && mouseY <= boundMaxY && mouseY >= boundMinY) : true;
        this.setTooltipDelay(Duration.ofDays(mouseInBoundingBox ? 0 : 999));
        this.isHovered = (mouseX >= getX() && mouseX < getX() + width && mouseY >= getY() && mouseY < getY() + height);
        graphics.blitSprite(isHoveredOrFocused() ? SELECTED_TEXTURE : getColor().texture, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        Font font = Minecraft.getInstance().font;
        int textWidth;
        if (this.getMessage() == null || this.getMessage().getString().isEmpty()) {
            textWidth = 0;
        } else {
            textWidth = font.width(this.getMessage());
        }
        int textX = this.getX() + textPaddingLeft + ((this.width - textPaddingLeft) - textWidth) / 2 + 1;
        int textY = this.getY() + (this.height - 8) / 2;
        graphics.enableScissor(getX() + textPaddingLeft + 2, getY(), getX() + width - 2, getY() + height);
        int offset = 0;
        if (textWidth > this.width - 4) {
            float x = ((textWidth - this.width + 4f) / 2f);
            offset = Math.round(MathUtilities.clampFloat((float) (Math.sin(timePassed / 20f) * x * 2f), -x, x));
        }
        graphics.drawString(font, this.getMessage(), textX + offset, textY, isHoveredOrFocused() ? 0xFFFFFF : getColor().getColor(), false);
        graphics.disableScissor();
        timePassed += partialTick;
    }
}
