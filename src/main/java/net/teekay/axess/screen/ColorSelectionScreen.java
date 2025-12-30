package net.teekay.axess.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.client.AxessClientMenus;
import net.teekay.axess.network.AxessPacketHandler;
import net.teekay.axess.network.packets.server.CtSModifyNetworkPacket;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.screen.component.AccessLevelEntry;
import net.teekay.axess.screen.component.HumbleImageButton;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.MathUtil;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class ColorSelectionScreen extends Screen {

    private static final Component TITLE = Component.translatable("gui." + Axess.MODID + ".color_selection_screen");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/color_selector.png");
    private static final ResourceLocation CONFIRM_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/confirm_button.png");
    private static final ResourceLocation BACK_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/back_button.png");

    private static final Component CONFIRM_BUTTON_LABEL = Component.translatable("gui."+Axess.MODID+".button.confirm");
    private static final Component BACK_BUTTON_LABEL = Component.translatable("gui."+Axess.MODID+".button.back");

    private Color displayedColor;
    private AxessColors.HSVColor selColor;

    private Consumer<Color> callback;

    public ColorSelectionScreen(Consumer<Color> callback, Color initColor) {
        super(TITLE);
        //System.out.println("COLOR");
        this.selColor = AxessColors.toHsv(initColor);

        this.imageWidth = 150;
        this.imageHeight = 144;

        this.callback = callback;
    }

    private int leftPos, topPos;
    private int imageWidth, imageHeight;

    private ArrayList<Color> hueColors = new ArrayList<>();
    private ArrayList<Color> saturationColors = new ArrayList<>();
    private ArrayList<Color> vibranceColors = new ArrayList<>();

    private ImageButton confirmButton;
    private ImageButton backButton;

    private void remakeColors() {

        //System.out.println("HUE CALCS");
        // HUE
        if (hueColors.size() == 0) for (int i = 0; i < 120; i++) {
            hueColors.add(AxessColors.hsvToRgb(3f * i, 100f, 100f));
        }

        //System.out.println("SAT CALCS");
        // SAT
        saturationColors.clear();
        for (int i = 0; i < 120; i++) {
            saturationColors.add(AxessColors.hsvToRgb(selColor.h, ((float)(i) / 119f) * 100f, selColor.v));
        }

        //System.out.println("VIB CALCS");
        // VIB
        vibranceColors.clear();
        for (int i = 0; i < 120; i++) {
            vibranceColors.add(AxessColors.hsvToRgb(selColor.h, selColor.s,((float)(i) / 119f) * 100f));
        }

        displayedColor = AxessColors.toRGB(selColor);
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width  - this.imageWidth ) / 2;
        this.topPos  = (this.height - this.imageHeight) / 2;

        if (this.minecraft == null) return;
        ClientLevel level = this.minecraft.level;
        if (level == null) return;

        remakeColors();

        this.confirmButton = addRenderableWidget(
                new HumbleImageButton(
                        this.leftPos + 44,
                        this.topPos + 117,
                        20,
                        20,
                        0,
                        0,
                        20,
                        CONFIRM_BUTTON_TEXTURE,
                        32, 96,
                        btn -> {
                            callback.accept(displayedColor);
                            Minecraft.getInstance().popGuiLayer();
                        })
        );
        this.confirmButton.setTooltip(Tooltip.create(CONFIRM_BUTTON_LABEL));

        this.backButton = addRenderableWidget(
                new HumbleImageButton(
                        this.leftPos + 86,
                        this.topPos + 117,
                        20,
                        20,
                        0,
                        0,
                        20,
                        BACK_BUTTON_TEXTURE,
                        32, 64,
                        btn -> {
                            Minecraft.getInstance().popGuiLayer();
                        })
        );
        this.backButton.setTooltip(Tooltip.create(BACK_BUTTON_LABEL));

    }

    int cOffset = 23;
    private String dragging = null;

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.fill(leftPos+26, topPos+23, leftPos+26+98, topPos+24+17, displayedColor.getRGB());

        int hProg = (int)((selColor.h / 360f) * 119);
        int sProg = (int)((selColor.s / 100f) * 119);
        int vProg = (int)((selColor.v / 100f) * 119);

        for (int pos = 0; pos < 120; pos++) {
            pGuiGraphics.fill(leftPos+15+pos, topPos+48,                 leftPos+15+pos+1, topPos+48+16,                 hueColors.get(pos).getRGB()       );
            pGuiGraphics.fill(leftPos+15+pos, topPos+48+cOffset,         leftPos+15+pos+1, topPos+48+16+cOffset,         saturationColors.get(pos).getRGB());
            pGuiGraphics.fill(leftPos+15+pos, topPos+48+cOffset+cOffset, leftPos+15+pos+1, topPos+48+16+cOffset+cOffset, vibranceColors.get(pos).getRGB()  );
        }

        pGuiGraphics.blit(TEXTURE, leftPos+15+hProg-1, topPos+48-4, 3, 144, 3,2);
        pGuiGraphics.blit(TEXTURE, leftPos+15+sProg-1, topPos+48-4+cOffset, 3, 144, 3,2);
        pGuiGraphics.blit(TEXTURE, leftPos+15+vProg-1, topPos+48-4+cOffset+cOffset, 3, 144, 3,2);

        int textLen = font.width(TITLE);
        pGuiGraphics.drawString(this.font, TITLE, this.leftPos + (this.imageWidth - textLen) / 2, this.topPos+8, AxessColors.MAIN.getRGB(), false);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pMouseX < leftPos+15 || pMouseX > leftPos+15+120) return super.mouseClicked(pMouseX, pMouseY, pButton);

        if (pMouseY >= topPos+48 && pMouseY <= topPos+48+16) { // HUE
            dragging = "h";
            updateHeld((int)pMouseX);
        } else if (pMouseY >= topPos+48+cOffset && pMouseY <= topPos+48+16+cOffset) { // SAT
            dragging = "s";
            updateHeld((int)pMouseX);
        } else if (pMouseY >= topPos+48+cOffset+cOffset && pMouseY <= topPos+48+16+cOffset+cOffset) { // VIB
            dragging = "v";
            updateHeld((int)pMouseX);
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        dragging = null;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (dragging != null) updateHeld((int)pMouseX);
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    public void updateHeld(int mouseX) {
        if (dragging.equals("h")) {
            selColor.h = MathUtil.clampInt((int)((float)(mouseX - leftPos-15)/120f * 360f), 0, 360);
        } else if (dragging.equals("s")) {
            selColor.s = MathUtil.clampInt((int)((float)(mouseX - leftPos-15)/120f * 100f), 0, 100);
        } else if (dragging.equals("v")) {
            selColor.v = MathUtil.clampInt((int)((float)(mouseX - leftPos-15)/120f * 100f), 0, 100);
        } else {
            return;
        }

        remakeColors();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
