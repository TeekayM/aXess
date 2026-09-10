package net.teekay.axess.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.AxessConfig;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.access.AccessPermission;
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
    private static final Component PERMISSIONS_LABEL = Component.translatable("gui."+Axess.MODID+".manage_permissions");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/network_editor.png");
    private static final ResourceLocation ADD_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/create_button.png");
    private static final ResourceLocation CONFIRM_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/confirm_button.png");
    private static final ResourceLocation CANCEL_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/back_button.png");
    private static final ResourceLocation PLAYER_PERMISSION_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/player_permission_button.png");


    public final int imageWidth, imageHeight;

    public int leftPos, topPos;

    public final AccessNetwork network;

    // UI Elements
    private HumbleImageButton permissionButton;
    private HumbleImageButton addButton;
    private AccessLevelListEditor accessLevelListEditor;
    private HumbleImageButton doneButton;
    private HumbleImageButton cancelButton;
    private EditBox nameEdit;

    public boolean cantEdit = false;

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

        if (!network.hasPermission(Minecraft.getInstance().player, AccessPermission.AL_EDIT)) {cantEdit = true;}

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
        if (this.accessLevelListEditor != null) {
            pastPos = this.accessLevelListEditor.scrollPos;
        }

        this.accessLevelListEditor = new AccessLevelListEditor(this);
        this.accessLevelListEditor.scrollPos = pastPos;

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
                    this.accessLevelListEditor.addElement(newAl);
                }
        );

        HumbleImageButton permissionButton = new HumbleImageButton(
                this.leftPos + 218 - 20 - 3,
                this.topPos + 26,
                20, 20,
                0, 0,
                20,
                PLAYER_PERMISSION_BUTTON_TEXTURE,
                32, 96,
                btn -> {
                    AxessClientMenus.openPlayerPermissionEditor(network.getPermissions());
                }
        );

        addButton.setTooltip(Tooltip.create(ADD_BUTTON_LABEL));

        permissionButton.setTooltip(Tooltip.create(PERMISSIONS_LABEL));

        this.addButton = addRenderableWidget(addButton);
        this.permissionButton = addRenderableWidget(permissionButton);

        if (!network.hasPermission(Minecraft.getInstance().player, AccessPermission.ADMIN)) {
            this.permissionButton.active = false;
        }

        for (AccessLevel accessLevel : this.network.getAccessLevels()) {
            this.accessLevelListEditor.addElement(accessLevel);
        }

        if (cantEdit) {
            this.addButton.active = false;
            this.nameEdit.active = false;
            this.doneButton.active = false;
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        for(Renderable renderable : this.renderables) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        this.accessLevelListEditor.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        //int networks = this.accessLevelList.getSize();
        //pGuiGraphics.drawString(this.font, Component.literal(String.valueOf(networks)).append(" ").append(networks == 1 ? NETWORK_LABEL : NETWORKS_LABEL),
        //        this.leftPos+13, this.topPos+32, AxessColors.MAIN, false);
        pGuiGraphics.drawString(this.font, TITLE_LABEL, this.leftPos+8, this.topPos+8, AxessColors.MAIN.getRGB(), false);

        int accessLevelCount = this.accessLevelListEditor.getSize();
        int accessLevelMaxCount = getMinecraft().player != null ? AxessConfig.getPlayerMaxLevelsPerNetwork(getMinecraft().player) : 0;

        if (!cantEdit)
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
    public boolean mouseScrolled(double pMouseX, double pMouseY, double scrollX, double scrollY) {
        this.accessLevelListEditor.scroll((int) (scrollY) * -7);
        return super.mouseScrolled(pMouseX, pMouseY, 0, scrollY);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        this.accessLevelListEditor.mouseReleased(pMouseX, pMouseY, pButton);
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
}
