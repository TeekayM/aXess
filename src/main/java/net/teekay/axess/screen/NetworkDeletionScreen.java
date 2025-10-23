package net.teekay.axess.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.client.AxessClientMenus;
import net.teekay.axess.network.AxessPacketHandler;
import net.teekay.axess.network.packets.server.CtSDeleteNetworkPacket;
import net.teekay.axess.screen.component.HumbleImageButton;
import net.teekay.axess.utilities.AxessColors;

public class NetworkDeletionScreen extends Screen {

    private static final Component TITLE_LABEL = Component.translatable("gui."+Axess.MODID+".network_deletion");
    private static final Component DELETE_LABEL = Component.translatable("gui."+Axess.MODID+".network_deletion.text");

    private static final Component DELETE_BUTTON_LABEL = Component.translatable("gui."+Axess.MODID+".button.delete");
    private static final Component BACK_BUTTON_LABEL = Component.translatable("gui."+Axess.MODID+".button.back");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/network_deletion.png");

    private static final ResourceLocation TEXTURE_DELETE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/delete_button_serious.png");
    private static final ResourceLocation TEXTURE_BACK = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/back_button.png");

    private final int imageWidth, imageHeight;

    private int leftPos, topPos;

    // UI Elements
    private HumbleImageButton deleteButton;
    private HumbleImageButton backButton;

    private AccessNetwork network;

    public NetworkDeletionScreen(AccessNetwork network) {
        super(TITLE_LABEL);

        this.imageWidth = 138;
        this.imageHeight = 83;

        this.network = network;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        if (this.minecraft == null) return;
        ClientLevel level = this.minecraft.level;
        if (level == null) return;

        this.deleteButton = addRenderableWidget(
                new HumbleImageButton(
                        this.leftPos + 37,
                        this.topPos + 51,
                        20,
                        20,
                        0,
                        0,
                        20,
                        TEXTURE_DELETE,
                        32, 64,
                        btn -> {
                            AccessNetworkDataClient.removeNetwork(network.getUUID());
                            AxessPacketHandler.sendToServer(new CtSDeleteNetworkPacket(network.getUUID()));
                            AxessClientMenus.openNetworkManagerScreen();
                        })
        );

        this.deleteButton.setTooltip(Tooltip.create(DELETE_BUTTON_LABEL));

        this.backButton = addRenderableWidget(
                new HumbleImageButton(
                        this.leftPos + 81,
                        this.topPos + 51,
                        20,
                        20,
                        0,
                        0,
                        20,
                        TEXTURE_BACK,
                        32, 64,
                        btn -> {
                            AxessClientMenus.openNetworkManagerScreen();
                        })
        );

        this.backButton.setTooltip(Tooltip.create(BACK_BUTTON_LABEL));

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);

        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        int textLen = font.width(DELETE_LABEL);
        int textLen2 = font.width(network.getName());

        pGuiGraphics.drawString(this.font, DELETE_LABEL, this.leftPos + (this.imageWidth - textLen) / 2, this.topPos+8, AxessColors.MAIN.getRGB(), false);
        pGuiGraphics.drawString(this.font, network.getName(), this.leftPos + (this.imageWidth - textLen2) / 2, this.topPos+30, 0xFFFFFF, false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
