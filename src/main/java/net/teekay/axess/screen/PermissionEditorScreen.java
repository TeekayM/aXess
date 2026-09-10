package net.teekay.axess.screen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessPermission;
import net.teekay.axess.client.AxessClientMenus;
import net.teekay.axess.screen.component.HumbleImageButton;
import net.teekay.axess.screen.component.PlayerEntry;
import net.teekay.axess.screen.component.PlayerList;
import net.teekay.axess.screen.component.TexturedSmallCheckbox;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.name_cache.ClientPlayerNameCache;

import java.util.*;
import java.util.function.Consumer;

public class PermissionEditorScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/player_permissions.png");
    private static final ResourceLocation BACK_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/back_button.png");

    private static final List<Pair<Component, List<Pair<Component, AccessPermission>>>> sections = List.of(
            Pair.of(
                    Component.translatable("gui."+Axess.MODID+".permissions.section.general"),
                    List.of(
                            Pair.of(
                                    Component.translatable("gui."+Axess.MODID+".permissions.view"),
                                    AccessPermission.VIEW
                            )
                    )
            ),

            Pair.of(
                    Component.translatable("gui."+Axess.MODID+".permissions.section.reader"),
                    List.of(
                            Pair.of(
                                    Component.translatable("gui."+Axess.MODID+".permissions.reader_edit"),
                                    AccessPermission.READER_EDIT
                            ),
                            Pair.of(
                                    Component.translatable("gui."+Axess.MODID+".permissions.reader_link"),
                                    AccessPermission.READER_LINK
                            ),
                            Pair.of(
                                    Component.translatable("gui."+Axess.MODID+".permissions.reader_overrides"),
                                    AccessPermission.READER_OVERRIDES
                            )
                    )
            ),

            Pair.of(
                    Component.translatable("gui."+Axess.MODID+".permissions.section.access_level"),
                    List.of(
                            Pair.of(
                                    Component.translatable("gui."+Axess.MODID+".permissions.al_edit"),
                                    AccessPermission.AL_EDIT
                            )
                    )
            ),

            Pair.of(
                    Component.translatable("gui."+Axess.MODID+".permissions.section.keycard"),
                    List.of(
                            Pair.of(
                                    Component.translatable("gui."+Axess.MODID+".permissions.keycard_assign"),
                                    AccessPermission.KEYCARD_ASSIGN
                            )
                    )
            )
    );

    private final int imageWidth, imageHeight;

    private int listTopLeftX, listTopLeftY;

    private int leftPos, topPos;

    private EnumSet<AccessPermission> permissions;

    private Component title;

    public PermissionEditorScreen(UUID playerUUID, EnumSet<AccessPermission> permissions) {
        super(Component.translatable("gui."+Axess.MODID+".permission_editor", ClientPlayerNameCache.getName(playerUUID)));
        this.title = Component.translatable("gui."+Axess.MODID+".permission_editor", ClientPlayerNameCache.getName(playerUUID));

        this.imageWidth = 148;
        this.imageHeight = 212;

        this.permissions = permissions;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        listTopLeftX = this.leftPos+8;
        listTopLeftY = this.topPos+29;

        if (this.minecraft == null) return;
        ClientLevel level = this.minecraft.level;
        if (level == null) return;

        addRenderableWidget(new HumbleImageButton( // BACK BUTTON
                this.leftPos + 125,
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

        int curX = listTopLeftX, curY = listTopLeftY;

        for (Pair<Component, List<Pair<Component, AccessPermission>>> section : sections) {
            curY += 11;
            for (Pair<Component, AccessPermission> permissionEntry : section.getSecond()) {
                addRenderableWidget(new TexturedSmallCheckbox(
                        curX, curY, 16, Component.empty(), (permissions.contains(permissionEntry.getSecond())),
                        (checkbox, state) -> {
                            if (state) {
                                permissions.add(permissionEntry.getSecond());
                            } else {
                                permissions.remove(permissionEntry.getSecond());
                            }
                        }
                ));
                curY += 19;
            }
            curY += 7;
        }

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        for(Renderable renderable : this.renderables) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        int curX = listTopLeftX, curY = listTopLeftY;

        for (Pair<Component, List<Pair<Component, AccessPermission>>> section : sections) {
            pGuiGraphics.drawString(this.font, section.getFirst(), curX, curY-1, AxessColors.MAIN.getRGB(), false);
            curY += 11;
            for (Pair<Component, AccessPermission> permissionEntry : section.getSecond()) {
                pGuiGraphics.drawString(this.font, permissionEntry.getFirst(), curX+20, curY+4, AxessColors.MAIN.getRGB(), false);
                curY += 19;
            }
            curY += 7;
        }

        pGuiGraphics.drawString(this.font, title, this.leftPos+8, this.topPos+10, AxessColors.MAIN.getRGB(), false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
