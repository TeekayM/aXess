package net.teekay.axess.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.client.AxessClientMenus;
import net.teekay.axess.screen.component.HelpButton;
import net.teekay.axess.screen.component.HumbleImageButton;
import net.teekay.axess.screen.component.NetworkEntry;
import net.teekay.axess.screen.component.NetworkList;
import net.teekay.axess.utilities.AxessColors;

public class NetworkManagerScreen extends Screen {

    private static final Component TITLE_LABEL = Component.translatable("gui."+Axess.MODID+".network_manager");

    private static final Component EXIT_BUTTON_LABEL = Component.translatable("gui."+Axess.MODID+".buttons.exit");
    private static final Component ADD_BUTTON_LABEL = Component.translatable("gui."+Axess.MODID+".buttons.add_network");
    private static final Component NETWORK_LABEL = Component.translatable("gui."+Axess.MODID+".network_manager.network");
    private static final Component NETWORKS_LABEL = Component.translatable("gui."+Axess.MODID+".network_manager.networks");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/network_manager.png");
    private static final ResourceLocation ADD_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/create_button.png");

    private static final Component HELP_LABEL = Component.translatable("gui."+Axess.MODID+".help.network_manager");

    private final int imageWidth, imageHeight;

    private int leftPos, topPos;

    // UI Elements
    private HumbleImageButton addButton;
    private NetworkList networkList;

    public NetworkManagerScreen() {
        super(TITLE_LABEL);

        this.imageWidth = 201;
        this.imageHeight = 181;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        if (this.minecraft == null) return;
        ClientLevel level = this.minecraft.level;
        if (level == null) return;

        addRenderableWidget(new HelpButton(leftPos, topPos, imageWidth, imageHeight, HELP_LABEL));

        HumbleImageButton addButton = new HumbleImageButton(
                this.leftPos + 163,
                this.topPos + 25,
                20,
                20,
                0,
                0,
                20,
                ADD_TEXTURE,
                32, 64,
                btn -> {
                    AxessClientMenus.openNetworkCreationScreen();
                }
        );

        addButton.setTooltip(Tooltip.create(ADD_BUTTON_LABEL));

        this.addButton = addRenderableWidget(addButton);

        this.networkList = new NetworkList(leftPos + 14, topPos + 51, 169, 116);

        for (AccessNetwork network : AccessNetworkDataClient.getNetworkRegistry().values()) {
            if (!network.getOwnerUUID().equals(Minecraft.getInstance().player.getUUID())) continue;
            NetworkEntry btn = this.networkList.addElement(network);
            addWidget(btn.button);
            addWidget(btn.trashButton);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);

        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        this.networkList.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        int networks = this.networkList.getSize();
        pGuiGraphics.drawString(this.font, Component.literal(String.valueOf(networks)).append(" ").append(networks == 1 ? NETWORK_LABEL : NETWORKS_LABEL),
                this.leftPos+13, this.topPos+32, AxessColors.MAIN.getRGB(), false);
        pGuiGraphics.drawString(this.font, TITLE_LABEL, this.leftPos+8, this.topPos+8, AxessColors.MAIN.getRGB(), false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        this.networkList.scroll((int) (pDelta) * -7);
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }



}
