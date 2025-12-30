package net.teekay.axess.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.screen.component.AccessLevelEntry;
import net.teekay.axess.screen.component.HumbleImageButton;
import net.teekay.axess.utilities.AxessColors;

import java.util.ArrayList;
import java.util.function.Consumer;

public class IconSelectionScreen extends Screen {

    private static final Component TITLE = Component.translatable("gui." + Axess.MODID + ".icon_selection_screen");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/icon_selector.png");
    private static final ResourceLocation EMPTY_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/empty_button.png");

    private Consumer<AxessIconRegistry.AxessIcon> callback;

    public IconSelectionScreen(Consumer<AxessIconRegistry.AxessIcon> callback) {
        super(TITLE);

        this.imageWidth = 150;
        this.imageHeight = 112;

        this.callback = callback;
    }

    private int leftPos, topPos;
    private int imageWidth, imageHeight;

    private int windowX, windowY;
    private int windowWidth, windowHeight;

    private int scrollerWidth = 3;
    private int scrollerHeight = 12;

    private ArrayList<HumbleImageButton> iconButtons = new ArrayList<>();
    private ArrayList<AxessIconRegistry.AxessIcon> icons;

    private final int itemsPerRow = 6;

    private int scrollPos = 0;
    private int maxScrollPos = 0;

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.windowX = this.leftPos + 11;
        this.windowY = this.topPos + 25;

        this.windowWidth = 125;
        this.windowHeight = 76;

        if (this.minecraft == null) return;
        ClientLevel level = this.minecraft.level;
        if (level == null) return;

        this.icons = AxessIconRegistry.getAllEntries();

        for (int index = 0; index < AxessIconRegistry.getAllEntries().size(); index++)
        {
            int selfIndex = index;
            iconButtons.add(new HumbleImageButton(
                    windowX + (index % itemsPerRow) * (20 + 1), windowY,
                    20, 20,
                    0, 0,
                    20,
                    EMPTY_BUTTON_TEXTURE,
                    32, 64,
                    btn -> {
                        select(selfIndex);
                    }
            ));
        }

        for (HumbleImageButton btn :
                iconButtons) {
            addWidget(btn);
            btn.setBounds(this.windowX, this.windowY, this.windowX + this.windowWidth, this.windowY + this.windowHeight);
        }

        maxScrollPos = (iconButtons.size() / itemsPerRow) * (20 + 1) - windowHeight - 1;
    }

    private void select(int index) {
        callback.accept(this.icons.get(index));
        Minecraft.getInstance().popGuiLayer();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        pGuiGraphics.enableScissor(this.windowX, this.windowY, this.windowX + this.windowWidth + this.scrollerWidth + 10, this.windowY + this.windowHeight);

        for (int index = 0; index < iconButtons.size(); index++) {
            HumbleImageButton btn = iconButtons.get(index);
            AxessIconRegistry.AxessIcon icon = icons.get(index);

            btn.setY(this.windowY + (index / itemsPerRow) * (20 + 1) - this.scrollPos);
            btn.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            pGuiGraphics.blit(icon.TEXTURE, btn.getX() + 1, btn.getY() + 1, 0, 0, 18, 18, 18, 18);
        }

        int scrollerImgPos = (int) ((float)(windowHeight-scrollerHeight+1) * ((float)scrollPos / (float)maxScrollPos));
        pGuiGraphics.blit(TEXTURE, windowX+windowWidth+1, windowY+scrollerImgPos, 150, 0, scrollerWidth, scrollerHeight);

        pGuiGraphics.disableScissor();

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        int textLen = font.width(TITLE);
        pGuiGraphics.drawString(this.font, TITLE, this.leftPos + (this.imageWidth - textLen) / 2, this.topPos+8, AxessColors.MAIN.getRGB(), false);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        scroll((int) (pDelta) * -7);
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    public void scroll(int delta) {
        this.scrollPos = Math.max(Math.min(scrollPos + delta, maxScrollPos), 0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
