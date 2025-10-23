package net.teekay.axess.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.AxessConfig;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.client.AxessClientMenus;
import net.teekay.axess.network.AxessPacketHandler;
import net.teekay.axess.network.packets.server.CtSModifyNetworkPacket;
import net.teekay.axess.screen.component.*;
import net.teekay.axess.utilities.AxessColors;

import java.util.function.Consumer;

public class NetworkEditorScreen extends Screen {

    private static final Component TITLE_LABEL = Component.translatable("gui."+Axess.MODID+".network_editor");

    private static final Component DONE_BUTTON_LABEL = Component.translatable("gui."+Axess.MODID+".button.done");
    private static final Component CANCEL_BUTTON_LABEL = Component.translatable("gui."+Axess.MODID+".button.cancel");
    private static final Component ADD_BUTTON_LABEL = Component.translatable("gui."+Axess.MODID+".button.add_access_level");
    private static final Component NETWORK_NAME_LABEL = Component.translatable("gui."+Axess.MODID+".input.network_name");
    private static final Component LEVELS_LABEL = Component.translatable("gui."  + Axess.MODID + ".network_editor.access_levels");
    private static final Component HELP_LABEL = Component.translatable("gui."+Axess.MODID+".help.network_editor");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/network_editor.png");
    private static final ResourceLocation ADD_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/create_button.png");
    private static final ResourceLocation CONFIRM_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/confirm_button.png");
    private static final ResourceLocation CANCEL_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/back_button.png");

    public final int imageWidth, imageHeight;

    public int leftPos, topPos;

    public final AccessNetwork network;

    // UI Elements
    private HumbleImageButton addButton;
    private AccessLevelList accessLevelList;
    private HumbleImageButton doneButton;
    private HumbleImageButton cancelButton;
    private EditBox nameEdit;

    public Consumer<AbstractWidget> childrenAdder = this::addWidget;
    public Consumer<AbstractWidget> childrenRemover = this::addWidget;

    public NetworkEditorScreen(AccessNetwork net) {
        super(TITLE_LABEL);

        this.network = AccessNetwork.fromNBT(net.toNBT());

        this.imageWidth = 256;
        this.imageHeight = 198;
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

        this.nameEdit = addRenderableWidget(
                new TexturedEditBox(Minecraft.getInstance().font,
                        this.leftPos + 12,
                        this.topPos + 26,
                        140,
                        20,
                        Component.literal(network.getName())
                )
        );
        this.nameEdit.setValue(network.getName());
        this.nameEdit.setResponder(network::setName);
        this.nameEdit.setTooltip(Tooltip.create(NETWORK_NAME_LABEL));
        this.nameEdit.setMaxLength(22);

        this.doneButton = addRenderableWidget(
                new HumbleImageButton(
                        leftPos + 12, topPos + 173,
                        20, 20,
                        0, 0,
                        20,
                        CONFIRM_BUTTON_TEXTURE,
                        32, 96,
                        btn -> {
                            for (AccessLevel accessLevel :
                                    this.network.getAccessLevels()) {
                                if (accessLevel.getName().isEmpty()) return;
                            }
                            if (network.getName().isEmpty()) return;
                            AccessNetworkDataClient.setNetwork(this.network);
                            AxessPacketHandler.sendToServer(new CtSModifyNetworkPacket(this.network));
                            AxessClientMenus.openNetworkManagerScreen();
                         })
        );
        this.doneButton.setTooltip(Tooltip.create(DONE_BUTTON_LABEL));

        this.cancelButton = addRenderableWidget(
                new HumbleImageButton(
                        leftPos + 12 + 20 + 5, topPos + 173,
                        20, 20,
                        0, 0,
                        20,
                        CANCEL_BUTTON_TEXTURE,
                        32, 64,
                        btn -> {
                            AxessClientMenus.openNetworkManagerScreen();
                        })
        );
        this.cancelButton.setTooltip(Tooltip.create(CANCEL_BUTTON_LABEL));

        int pastPos = 0;
        if (this.accessLevelList != null) {
            pastPos = this.accessLevelList.scrollPos;
        }

        this.accessLevelList = new AccessLevelList(this);
        this.accessLevelList.scrollPos = pastPos;

        HumbleImageButton addButton = new HumbleImageButton(
                this.leftPos + 218,
                this.topPos + 26,
                20, 20,
                0, 0,
                20,
                ADD_TEXTURE,
                32, 96,
                btn -> {
                    AccessLevel newAl = new AccessLevel(this.network.getUUID());
                    newAl.setPriority(999999);
                    this.network.addAccessLevel(newAl);
                    this.accessLevelList.addElement(newAl);
                }
        );

        addButton.setTooltip(Tooltip.create(ADD_BUTTON_LABEL));

        this.addButton = addRenderableWidget(addButton);

        for (AccessLevel accessLevel : this.network.getAccessLevels()) {
            this.accessLevelList.addElement(accessLevel);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);

        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        this.accessLevelList.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        //int networks = this.accessLevelList.getSize();
        //pGuiGraphics.drawString(this.font, Component.literal(String.valueOf(networks)).append(" ").append(networks == 1 ? NETWORK_LABEL : NETWORKS_LABEL),
        //        this.leftPos+13, this.topPos+32, AxessColors.MAIN, false);
        pGuiGraphics.drawString(this.font, TITLE_LABEL, this.leftPos+8, this.topPos+8, AxessColors.MAIN.getRGB(), false);

        int accessLevelCount = this.accessLevelList.getSize();
        int accessLevelMaxCount = AxessConfig.maxLevelsPerNetwork;

        this.addButton.active = accessLevelCount < accessLevelMaxCount;

        String countLabel = accessLevelCount + "/" + accessLevelMaxCount + " " + LEVELS_LABEL.getString();

        pGuiGraphics.drawString(this.font, countLabel, this.leftPos + 239 - this.font.width(countLabel), this.topPos + 179, AxessColors.MAIN.getRGB(), false);
        pGuiGraphics.drawString(this.font, TITLE_LABEL, this.leftPos+8, this.topPos+8, AxessColors.MAIN.getRGB(), false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        this.accessLevelList.scroll((int) (pDelta) * -7);
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        this.accessLevelList.mouseReleased(pMouseX, pMouseY, pButton);
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
}
