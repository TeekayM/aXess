package net.teekay.axess.screen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessPermission;
import net.teekay.axess.client.AxessClientMenus;
import net.teekay.axess.screen.component.*;
import net.teekay.axess.utilities.AxessColors;

import java.util.*;

public class NetworkPlayerPermissionScreen extends Screen {

    private static final Component TITLE_LABEL = Component.translatable("gui."+Axess.MODID+".network_player_permission");

    private static final Component EXIT_BUTTON_LABEL = Component.translatable("gui."+Axess.MODID+".button.exit");
    private static final Component ADD_BUTTON_LABEL = Component.translatable("gui."+Axess.MODID+".button.add_player");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/network_manager.png");
    private static final ResourceLocation ADD_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/add_player_button.png");
    private static final ResourceLocation BACK_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/back_button.png");

    private final int imageWidth, imageHeight;

    private int leftPos, topPos;

    // UI Elements
    private HumbleImageButton addButton;
    private HumbleImageButton backButton;
    private PlayerPermissionList permissionList;

    private HashMap<UUID, EnumSet<AccessPermission>> permissions;

    public NetworkPlayerPermissionScreen(HashMap<UUID, EnumSet<AccessPermission>> permissions) {
        super(TITLE_LABEL);

        this.imageWidth = 201;
        this.imageHeight = 181;

        this.permissions = permissions;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        if (this.minecraft == null) return;
        ClientLevel level = this.minecraft.level;
        if (level == null) return;

        this.addButton = addRenderableWidget(new HumbleImageButton(
                this.leftPos + 14,
                this.topPos + 25,
                20,
                20,
                0,
                0,
                20,
                ADD_TEXTURE,
                32, 96,
                btn -> {
                    AxessClientMenus.openPlayerSelectionScreen((uuid) -> {
                        if (permissions.containsKey(uuid)) return;

                        EnumSet<AccessPermission> newPerms = EnumSet.noneOf(AccessPermission.class);

                        permissions.put(uuid, newPerms);

                        PlayerPermissionEntry newBtn = this.permissionList.addElement(uuid, newPerms);
                        addWidget(newBtn.button);
                        addWidget(newBtn.trashButton);
                    });
                }
        ));

        this.backButton = addRenderableWidget(new HumbleImageButton(
                this.leftPos + 178,
                this.topPos + 3,
                20,
                20,
                0,
                0,
                20,
                BACK_TEXTURE,
                32, 64,
                btn -> {
                    AxessClientMenus.popGuiLayer();
                }
        ));

        addButton.setTooltip(Tooltip.create(ADD_BUTTON_LABEL));

        this.permissionList = new PlayerPermissionList(leftPos + 14, topPos + 51, 169, 116,
                (pair) -> {
                    permissions.remove(pair);
                    permissionList.removeElement(pair, (elem) ->
                    {
                        removeWidget(elem.button);
                        removeWidget(elem.trashButton);
                    });
                });

        for (Map.Entry<UUID, EnumSet<AccessPermission>> pEntry : permissions.entrySet()) {
            PlayerPermissionEntry btn = this.permissionList.addElement(pEntry.getKey(), pEntry.getValue());
            addWidget(btn.button);
            addWidget(btn.trashButton);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        for(Renderable renderable : this.renderables) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        this.permissionList.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);


//        int networksCount = AccessNetworkDataClient.getNetworks().stream().filter((network -> network.isOwnedBy(getMinecraft().player))).toList().size();
//        int maxNetworksCount = getMinecraft().player != null ? AxessConfig.getPlayerMaxNetworks(getMinecraft().player) : 0;
//
//        this.addButton.active = networksCount < maxNetworksCount;
//
//        pGuiGraphics.drawString(this.font, Component.literal(String.valueOf(networksCount)).append("/").append(String.valueOf(maxNetworksCount)).append(" ").append(NETWORKS_LABEL),
//                this.leftPos+13, this.topPos+32, AxessColors.MAIN.getRGB(), false);
        pGuiGraphics.drawString(this.font, TITLE_LABEL, this.leftPos+8, this.topPos+8, AxessColors.MAIN.getRGB(), false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double scrollX, double scrollY) {
        this.permissionList.scroll((int) (scrollY) * -7);
        return super.mouseScrolled(pMouseX, pMouseY, 0, scrollY);
    }
}
