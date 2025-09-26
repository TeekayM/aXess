package net.teekay.axess.screen.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.MathUtil;

public class TexturedButton extends Button {
    public TexturedButton(int x, int y, int width, int height, Component title, OnPress onPress) {
        super(x, y, width, height, title, onPress, btn -> {return Component.empty();});
    }

    private static ResourceLocation BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/buttons.png");

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
    public boolean isHovered() {
        return super.isHovered() && this.mouseInBoundingBox;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!this.mouseInBoundingBox) return false;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.mouseInBoundingBox = hasBounds ? (pMouseX <= boundMaxX && pMouseX >= boundMinX && pMouseY <= boundMaxY && pMouseY >= boundMinY) : true;
        this.setTooltipDelay(mouseInBoundingBox ? 0 : 10000000);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        this.isHovered = (mouseX >= getX() && mouseX < getX() + width && mouseY >= getY() && mouseY < getY() + height);

        graphics.blitNineSliced(
                BUTTON_TEXTURE,
                this.getX(), this.getY(),
                this.width, this.height,
                1, 1,
                200, 20,
                0, (isHoveredOrFocused() ? 20 : 0));

        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(this.getMessage());

        int textX = this.getX() + textPaddingLeft +  ((this.width - textPaddingLeft) - textWidth) / 2 + 1;
        int textY = this.getY() + (this.height - 8) / 2;

        graphics.enableScissor(getX() + textPaddingLeft + 2, getY(), getX() + width - 2, getY() + height);

        int offset = 0;
        if (textWidth > this.width - 4) {
            float x = ((textWidth - this.width + 4f) / 2f);
            offset = Math.round(MathUtil.clampFloat((float) (Math.sin(timePassed / 20f) * x * 2f), -x, x));
        }

        graphics.drawString(font, this.getMessage(), textX + offset, textY, isHoveredOrFocused() ? 0xFFFFFF : AxessColors.MAIN.getRGB(), false);

        graphics.disableScissor();

        timePassed += partialTick;
    }
}
