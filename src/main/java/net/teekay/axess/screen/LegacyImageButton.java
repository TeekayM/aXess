package net.teekay.axess.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class LegacyImageButton extends ImageButton {
	protected final int xTexStart;
	protected final int yTexStart;
	protected final int yDiffTex;
	protected final ResourceLocation resourceLocation;
	protected final int textureWidth;
	protected final int textureHeight;

	public LegacyImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation texture, OnPress onPress) {
		this(x, y, width, height, xTexStart, yTexStart, yDiffTex, texture, 256, 256, onPress, CommonComponents.EMPTY);
	}

	public LegacyImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress) {
		this(x, y, width, height, xTexStart, yTexStart, yDiffTex, texture, textureWidth, textureHeight, onPress, CommonComponents.EMPTY);
	}

	public LegacyImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress, Component message) {
		super(x, y, width, height, new WidgetSprites(texture, texture), onPress, message);
		this.xTexStart = xTexStart;
		this.yTexStart = yTexStart;
		this.yDiffTex = yDiffTex;
		this.resourceLocation = texture;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int v = this.yTexStart;
		if (this.isHoveredOrFocused()) {
			v += this.yDiffTex;
		}
		guiGraphics.blit(this.resourceLocation, this.getX(), this.getY(), (float) this.xTexStart, (float) v, this.width, this.height, this.textureWidth, this.textureHeight);
	}
}
