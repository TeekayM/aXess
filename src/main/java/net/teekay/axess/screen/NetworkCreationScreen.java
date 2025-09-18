package net.teekay.axess.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
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
import net.teekay.axess.network.packets.server.CtSModifyNetworkPacket;
import net.teekay.axess.screen.component.HumbleImageButton;
import net.teekay.axess.screen.component.TexturedEditBox;
import net.teekay.axess.utilities.AxessColors;

public class NetworkCreationScreen extends Screen {

    private static final Component TITLE_LABEL = Component.translatable("gui."+Axess.MODID+".network_creation");
    private static final Component CREATE_LABEL = Component.translatable("gui."+Axess.MODID+".network_creation.text");

    private static final Component CREATE_BUTTON_LABEL = Component.translatable("gui."+Axess.MODID+".buttons.create");
    private static final Component BACK_BUTTON_LABEL = Component.translatable("gui."+Axess.MODID+".buttons.back");
    private static final Component INPUT_LABEL = Component.translatable("gui."+Axess.MODID+".input.network_name");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/network_deletion.png");

    private static final ResourceLocation TEXTURE_CREATE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/create_button_serious.png");
    private static final ResourceLocation TEXTURE_BACK = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/back_button.png");

    private final int imageWidth, imageHeight;

    private int leftPos, topPos;

    // UI Elements
    private HumbleImageButton createButton;
    private HumbleImageButton backButton;
    private EditBox editBox;

    private AccessNetwork network;

    public NetworkCreationScreen() {
        super(TITLE_LABEL);

        this.imageWidth = 138;
        this.imageHeight = 83;

        if (Minecraft.getInstance().player != null)
            this.network = new AccessNetwork(Minecraft.getInstance().player.getUUID());
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        if (this.minecraft == null) return;
        ClientLevel level = this.minecraft.level;
        if (level == null) return;

        this.createButton = addRenderableWidget(
                new HumbleImageButton(
                        this.leftPos + 37,
                        this.topPos + 51,
                        20,
                        20,
                        0,
                        0,
                        20,
                        TEXTURE_CREATE,
                        32, 64,
                        btn -> {
                            if (this.editBox.getValue().isEmpty()) return;

                            network.setName(this.editBox.getValue());
                            AccessNetworkDataClient.setNetwork(network);
                            AxessPacketHandler.sendToServer(new CtSModifyNetworkPacket(network));
                            AxessClientMenus.openNetworkManagerScreen();
                        })
        );

        this.createButton.setTooltip(Tooltip.create(CREATE_BUTTON_LABEL));

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

        this.editBox = addRenderableWidget(
                new TexturedEditBox(font, leftPos+8, topPos+23, 122, 20, Component.literal(this.network.getName()))
        );

        this.editBox.setTooltip(Tooltip.create(INPUT_LABEL));

        //this.editBox.
        this.editBox.setMaxLength(22);
        this.editBox.setValue(this.network.getName());

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);

        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        int textLen = font.width(CREATE_LABEL);
        //int textLen2 = font.width(network.getName());

        pGuiGraphics.drawString(this.font, CREATE_LABEL, this.leftPos + (this.imageWidth - textLen) / 2, this.topPos+8, AxessColors.MAIN.getRGB(), false);
        //pGuiGraphics.drawString(this.font, network.getName(), this.leftPos + (this.imageWidth - textLen2) / 2, this.topPos+30, 0xFFFFFF, false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
