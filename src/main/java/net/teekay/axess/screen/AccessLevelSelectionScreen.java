package net.teekay.axess.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.client.AxessClientMenus;
import net.teekay.axess.screen.component.*;
import net.teekay.axess.utilities.AxessColors;

import java.util.function.Consumer;

public class AccessLevelSelectionScreen extends Screen {

    private static final Component TITLE_LABEL = Component.translatable("gui."+Axess.MODID+".access_level_selection_screen");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/network_selection_screen.png");
    private static final ResourceLocation BACK_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/back_button.png");

    private final int imageWidth, imageHeight;

    private int leftPos, topPos;

    private AccessLevelList levelList;

    private AccessNetwork net;

    private Consumer<AccessLevel> consumer;

    public AccessLevelSelectionScreen(AccessNetwork net, Consumer<AccessLevel> consumer) {
        super(TITLE_LABEL);

        this.imageWidth = 201;
        this.imageHeight = 156;

        this.consumer = consumer;

        this.net = net;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        if (this.minecraft == null) return;
        ClientLevel level = this.minecraft.level;
        if (level == null) return;

        this.levelList = new AccessLevelList(leftPos + 14, topPos + 26, 169, 116,
                (selLevel) -> {
                    AxessClientMenus.popGuiLayer();
                    consumer.accept(selLevel);
                });

        for (AccessLevel aclevel : net.getAccessLevels()) {
            AccessLevelEntry btn = this.levelList.addElement(aclevel);
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

        this.levelList.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.drawString(this.font, TITLE_LABEL, this.leftPos+8, this.topPos+8, AxessColors.MAIN.getRGB(), false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double scrollX, double scrollY) {
        this.levelList.scroll((int) (scrollY) * -7);
        return super.mouseScrolled(pMouseX, pMouseY, 0, scrollY);
    }
}
