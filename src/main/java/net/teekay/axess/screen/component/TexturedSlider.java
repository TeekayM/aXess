package net.teekay.axess.screen.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import net.teekay.axess.Axess;
import net.teekay.axess.AxessConfig;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.MathUtil;

public class TexturedSlider extends ForgeSlider {
    public static final ResourceLocation SLIDER_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/sliders.png");

    public TexturedSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
    }

    public TexturedSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, drawString);
    }

    protected int getTextureY() {
        int i = this.isHoveredOrFocused() ? 1 : 0;
        return i * 20;
    }

    protected int getHandleTextureY() {
        int i = this.isHoveredOrFocused() ? 3 : 2;
        return i * 20;
    }

    private float timePassed = 0f;

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        final Minecraft mc = Minecraft.getInstance();
        guiGraphics.blitWithBorder(SLIDER_TEXTURE, this.getX(), this.getY(), 0, getTextureY(), this.width, this.height, 200, 20, 2, 3, 2, 2);

        guiGraphics.blitWithBorder(SLIDER_TEXTURE, this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 0, getHandleTextureY(), 8, this.height, 200, 20 , 2, 3, 2, 2);

        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(this.getMessage());

        int textX = this.getX() + ((this.width) - textWidth) / 2 + 1;
        int textY = this.getY() + (this.height - 8) / 2;

        guiGraphics.enableScissor(getX() + 2, getY(), getX() + width - 2, getY() + height);

        int offset = 0;
        if (textWidth > this.width - 4) {
            float x = ((textWidth - this.width + 4f) / 2f);
            offset = Math.round(MathUtil.clampFloat((float) (Math.sin(timePassed / 20f) * x * 2f), -x, x));
        }

        guiGraphics.drawString(font, this.getMessage(), textX + offset, textY, isHoveredOrFocused() ? 0xFFFFFF : AxessColors.MAIN.getRGB(), false);

        guiGraphics.disableScissor();

        timePassed += partialTick;

        //renderScrollingString(guiGraphics, mc.font, 2, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
    }
}
