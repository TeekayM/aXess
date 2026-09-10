package net.teekay.axess.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.client.AxessClientMenus;
import net.teekay.axess.screen.component.*;
import net.teekay.axess.utilities.AxessColors;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PlayerSelectionScreen extends Screen {

    private static final Component TITLE_LABEL = Component.translatable("gui."+Axess.MODID+".player_selection_screen");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/network_selection_screen.png");
    private static final ResourceLocation BACK_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/back_button.png");

    private final int imageWidth, imageHeight;

    private int leftPos, topPos;

    private PlayerList playerList;

    private Consumer<UUID> consumer;

    public PlayerSelectionScreen(Consumer<UUID> consumer) {
        super(TITLE_LABEL);

        this.imageWidth = 201;
        this.imageHeight = 156;

        this.consumer = consumer;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        if (this.minecraft == null) return;
        ClientLevel level = this.minecraft.level;
        if (level == null) return;

        this.playerList = new PlayerList(leftPos + 14, topPos + 26, 169, 116,
                (uuid -> {
                    this.consumer.accept(uuid);
                    AxessClientMenus.popGuiLayer();
                }));

        Collection<PlayerInfo> players = Objects.requireNonNull(Minecraft.getInstance().getConnection()).getOnlinePlayers();

        for (PlayerInfo player : players) {
            if (Minecraft.getInstance().player == null) continue;
            if (player.getProfile().getId().equals(Minecraft.getInstance().player.getGameProfile().getId())) continue;
            PlayerEntry btn = this.playerList.addElement(player.getProfile().getId(), player.getProfile().getName());
            addWidget(btn.button);
        }

        addRenderableWidget(new HumbleImageButton(
                this.leftPos + 179,
                this.topPos + 2,
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


    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        for(Renderable renderable : this.renderables) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        this.playerList.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.drawString(this.font, TITLE_LABEL, this.leftPos+8, this.topPos+8, AxessColors.MAIN.getRGB(), false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double scrollX, double scrollY) {
        this.playerList.scroll((int) (scrollY) * -7);
        return super.mouseScrolled(pMouseX, pMouseY, 0, scrollY);
    }
}
