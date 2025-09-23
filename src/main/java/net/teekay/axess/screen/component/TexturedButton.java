package net.teekay.axess.screen.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.utilities.AxessColors;

public class TexturedButton extends Button {
    public TexturedButton(int x, int y, int width, int height, Component title, OnPress onPress) {
        super(x, y, width, height, title, onPress, btn -> {return Component.empty();});
    }

    private static ResourceLocation BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/buttons.png");

    public float timePassed = 0f;
    public int textPaddingLeft = 0;

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

        int textX = this.getX() + textPaddingLeft +  ((this.width - textPaddingLeft) - textWidth) / 2;
        int textY = this.getY() + (this.height - 8) / 2;

        graphics.enableScissor(getX() + textPaddingLeft + 1, getY(), getX() + width - 1 - 1, getY() + height);

        int offset = 0;
        if (textWidth > this.width - 2) {
            offset = (int) (Math.sin(timePassed) * ((textWidth - this.width) / 2));
        }

        graphics.drawString(font, this.getMessage(), textX + offset, textY, isHoveredOrFocused() ? 0xFFFFFF : AxessColors.MAIN.getRGB(), false);

        graphics.disableScissor();

        timePassed += partialTick;
    }
}
