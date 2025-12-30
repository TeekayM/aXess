package net.teekay.axess.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.teekay.axess.Axess;
import net.teekay.axess.access.*;
import net.teekay.axess.block.readers.KeycardReaderBlockEntity;
import net.teekay.axess.network.AxessPacketHandler;
import net.teekay.axess.network.packets.server.CtSModifyKeycardReaderPacket;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.screen.component.*;
import net.teekay.axess.utilities.AccessUtils;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.MathUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KeycardReaderScreen extends AbstractContainerScreen<KeycardReaderMenu> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/keycard_reader.png");
    private static final ResourceLocation CONFIRM_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/confirm_button.png");

    public static final Component TITLE_LABEL = Component.translatable("gui."+Axess.MODID+".keycard_reader");
    public static final Component NO_NETWORK_LABEL = Component.translatable("gui."+Axess.MODID+".keycard_reader.no_network");
    public static final Component NO_LEVEL_LABEL = Component.translatable("gui."+Axess.MODID+".keycard_reader.no_level");
    public static final Component APPLY_CHANGES_LABEL = Component.translatable("gui."+Axess.MODID+".keycard_reader.apply_changes");

    public static final Component OVERRIDE_DISPLAY_LABEL = Component.translatable("gui."+Axess.MODID+".keycard_reader.override_display");

    public static final Component ACTIVATION_MODE_PREFIX = Component.translatable("gui."+Axess.MODID+".activation_mode");
    public static final Component COMPARE_MODE_PREFIX = Component.translatable("gui."+Axess.MODID+".compare_mode");

    public static final Component PULSE_DURATION_LABEL_PREFIX = Component.translatable("gui."+Axess.MODID+".keycard_reader.pulse_duration.prefix");
    public static final Component PULSE_DURATION_LABEL_SUFFIX = Component.translatable("gui."+Axess.MODID+".keycard_reader.pulse_duration.suffix");
    public static final Component PULSE_DURATION_LABEL = Component.translatable("gui."+Axess.MODID+".keycard_reader.pulse_duration");

    public static final Component SYNCED_LABEL = Component.translatable("gui."+Axess.MODID+".keycard_reader.synced");

    private AccessNetwork selectedNetwork;
    private ArrayList<AccessLevel> selectedLevels = new ArrayList<>();
    private AccessActivationMode selectedActivationMode;
    private AccessCompareMode selectedCompareMode;
    private int selectedPulseDurationTicks;
    private boolean selectedOverrideDisplay;
    private AxessIconRegistry.AxessIcon selectedOverrideIcon;
    private Color selectedOverrideColor;

    private ArrayList<SelectableNetworkEntry> networkEntries = new ArrayList<>();
    private ArrayList<SelectableLevelEntry> levelEntries = new ArrayList<>();
    private HumbleImageButton applyButton;
    private TexturedButton compareModeButton;
    private TexturedButton activationModeButton;
    private TexturedSlider pulseDurationTicksSlider;
    private ImageButton syncIcon;
    private IconSelectorButton iconSelectorButton;
    private ColorSelectorButton colorSelectorButton;
    private TexturedCheckbox overrideDisplayCheckbox;

    private int scrollerWidth = 3;

    private int scrollPosNetworks = 0;
    private int scrollMaxNetworks = 0;
    private int scrollPosLevels = 0;
    private int scrollMaxLevels = 0;

    public KeycardReaderScreen(KeycardReaderMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        this.inventoryLabelY = 10000;
        this.imageWidth = 256;
        this.imageHeight = 233;

        super.init();

        this.applyButton = addRenderableWidget(
                new HumbleImageButton(
                        this.leftPos + 229,
                        this.topPos + 206,
                        20,
                        20,
                        0,
                        0,
                        20,
                        CONFIRM_BUTTON_TEXTURE,
                        32, 96,
                        btn -> {
                            if (menu.blockEntity == null || selectedNetwork == null || selectedLevels.isEmpty()) return;
                            AxessPacketHandler.sendToServer(new CtSModifyKeycardReaderPacket(menu.blockEntity.getBlockPos(), selectedNetwork, selectedLevels, selectedCompareMode, selectedActivationMode, selectedPulseDurationTicks, selectedOverrideDisplay, selectedOverrideIcon, selectedOverrideColor));
                        })
        );
        this.applyButton.active = false;

        this.selectedCompareMode = this.menu.blockEntity.getCompareMode();
        this.compareModeButton = addRenderableWidget(
                new TexturedButton(
                        this.leftPos + 7,
                        this.topPos + 129,
                        125, 18,
                        COMPARE_MODE_PREFIX.copy().append(selectedCompareMode.getName()),
                        btn -> {
                            selectedCompareMode = switch (selectedCompareMode) {
                                case SPECIFIC -> AccessCompareMode.BIGGER_THAN_OR_EQUAL;
                                case BIGGER_THAN_OR_EQUAL -> AccessCompareMode.LESSER_THAN_OR_EQUAL;
                                case LESSER_THAN_OR_EQUAL -> AccessCompareMode.SPECIFIC;
                            };

                            selectedLevels.clear();

                            btn.setMessage(COMPARE_MODE_PREFIX.copy().append(selectedCompareMode.getName()));
                            btn.setTooltip(Tooltip.create(selectedCompareMode.getDescription()));
                        }
                )
        );
        this.compareModeButton.setTooltip(Tooltip.create(selectedCompareMode.getDescription()));

        this.selectedActivationMode = this.menu.blockEntity.getActivationMode();
        this.activationModeButton = addRenderableWidget(
                new TexturedButton(
                        this.leftPos + 7,
                        this.topPos + 152,
                        125, 18,
                        ACTIVATION_MODE_PREFIX.copy().append(selectedActivationMode.getName()),
                        btn -> {
                            selectedActivationMode = switch (selectedActivationMode) {
                                case TOGGLE -> AccessActivationMode.PULSE;
                                case PULSE -> AccessActivationMode.TOGGLE;
                            };

                            this.pulseDurationTicksSlider.visible = selectedActivationMode == AccessActivationMode.PULSE;

                            btn.setMessage(ACTIVATION_MODE_PREFIX.copy().append(selectedActivationMode.getName()));
                            btn.setTooltip(Tooltip.create(selectedActivationMode.getDescription()));
                        }
                )
        );
        this.activationModeButton.setTooltip(Tooltip.create(selectedActivationMode.getDescription()));

        this.selectedPulseDurationTicks = this.menu.blockEntity.getPulseDurationTicks();
        this.pulseDurationTicksSlider = addRenderableWidget(
                new TexturedSlider(
                        this.leftPos + 7,
                        this.topPos + 175,
                        125, 18,
                        PULSE_DURATION_LABEL_PREFIX,
                        PULSE_DURATION_LABEL_SUFFIX,
                        2, 100,
                        this.selectedPulseDurationTicks, 1,
                        0, true
                )
        );
        this.pulseDurationTicksSlider.setTooltip(Tooltip.create(PULSE_DURATION_LABEL));
        this.pulseDurationTicksSlider.visible = selectedActivationMode == AccessActivationMode.PULSE;

        //this.syncIcon = addRenderableWidget(new TexturedButton(this.leftPos + 140, this.topPos + 170, 11, 11, Component.empty(), btn -> {}));
        this.syncIcon = addRenderableWidget(new ImageButton(
                this.leftPos + 140, this.topPos + 167,
                11, 11,
                0, 233,
                0,
                TEXTURE,
                btn -> {}
        ));
        this.syncIcon.setTooltip(Tooltip.create(SYNCED_LABEL));
        this.syncIcon.active = false;

        this.selectedOverrideDisplay = this.menu.blockEntity.isOverrideDisplay();
        this.overrideDisplayCheckbox = addRenderableWidget(new TexturedCheckbox(this.leftPos + 67, this.topPos + 197, 20, 20, Component.empty(), this.selectedOverrideDisplay,
                (enabled) -> {
                    this.selectedOverrideDisplay = enabled;
                    this.iconSelectorButton.active = enabled;
                    this.colorSelectorButton.active = enabled;
                }));

        this.selectedOverrideIcon = this.menu.blockEntity.getOverrideIcon();
        this.iconSelectorButton = addRenderableWidget(new IconSelectorButton(this.leftPos + 91, this.topPos + 197, 20, 20,
                this.selectedOverrideIcon, icon -> this.selectedOverrideIcon = icon, btn -> {}));
        this.iconSelectorButton.active = this.selectedOverrideDisplay;

        this.selectedOverrideColor = this.menu.blockEntity.getOverrideColor();
        this.colorSelectorButton = addRenderableWidget(new ColorSelectorButton(this.leftPos + 112, this.topPos + 197, 20, 20,
                this.selectedOverrideColor, color -> this.selectedOverrideColor = color, btn -> {}));
        this.colorSelectorButton.active = this.selectedOverrideDisplay;

        updateEntries();
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.selectedPulseDurationTicks = pulseDurationTicksSlider.getValueInt();

        renderBackground(pGuiGraphics);

        scrollPosLevels   = MathUtil.clampInt(scrollPosLevels,   0, scrollMaxLevels  );
        scrollPosNetworks = MathUtil.clampInt(scrollPosNetworks, 0, scrollMaxNetworks);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.drawString(this.font, TITLE_LABEL, this.leftPos+8, this.topPos+8, AxessColors.MAIN.getRGB(), false);

        // NETWORK ENTRIES
        pGuiGraphics.enableScissor(leftPos + NETWORKS_X, topPos + NETWORKS_Y, leftPos + NETWORKS_X + NETWORKS_WIDTH, topPos + NETWORKS_Y + NETWORKS_HEIGHT);
        for (SelectableNetworkEntry entry:
             networkEntries) {
            entry.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        }
        pGuiGraphics.disableScissor();

        int netScrollerHeight = MathUtil.calcScrollHeight(NETWORKS_HEIGHT, scrollMaxNetworks);
        int netScrollerPos = MathUtil.calcScrollPos(NETWORKS_HEIGHT, netScrollerHeight, scrollPosNetworks, scrollMaxNetworks);
        pGuiGraphics.fill(
                leftPos+NETWORKS_X+NETWORKS_WIDTH+1,
                topPos+NETWORKS_Y+netScrollerPos,
                leftPos+NETWORKS_X+NETWORKS_WIDTH+1+scrollerWidth,
                topPos+NETWORKS_Y+netScrollerPos+netScrollerHeight,
                AxessColors.MAIN.getRGB());

        // LEVEL ENTRIES
        pGuiGraphics.enableScissor(leftPos + LEVELS_X, topPos + LEVELS_Y, leftPos + LEVELS_X + LEVELS_WIDTH, topPos + LEVELS_Y + LEVELS_HEIGHT);
        for (SelectableLevelEntry entry:
                levelEntries) {
            entry.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        }
        pGuiGraphics.disableScissor();

        int lvlScrollerHeight = MathUtil.calcScrollHeight(LEVELS_HEIGHT, scrollMaxLevels);
        int lvlScrollerPos = MathUtil.calcScrollPos(LEVELS_HEIGHT, lvlScrollerHeight, scrollPosLevels, scrollMaxLevels);
        pGuiGraphics.fill(
                leftPos+LEVELS_X+LEVELS_WIDTH+1,
                topPos+LEVELS_Y+lvlScrollerPos,
                leftPos+LEVELS_X+LEVELS_WIDTH+1+scrollerWidth,
                topPos+NETWORKS_Y+lvlScrollerPos+lvlScrollerHeight,
                AxessColors.MAIN.getRGB());

        applyButton.active = false;

        // tip
        Component textComp = Component.empty();
        int color = AxessColors.MAIN.getRGB();
        if (selectedNetwork == null) {
            textComp = NO_NETWORK_LABEL;
        } else if (selectedLevels.isEmpty()) {
            textComp = NO_LEVEL_LABEL;
        } else {
            KeycardReaderBlockEntity entity = this.menu.blockEntity;
            boolean levelsDiff = true;

            if (entity.getAccessLevels().size() == selectedLevels.size()) {
                int cnt = 0;
                for (AccessLevel level :
                        selectedLevels) {
                    if (entity.getAccessLevels().contains(level)) {
                        cnt ++;
                    }
                }

                if (cnt == selectedLevels.size()) levelsDiff = false;
            }

            if (
                entity.getAccessNetwork() != selectedNetwork ||
                levelsDiff ||
                selectedCompareMode != menu.blockEntity.getCompareMode() ||
                selectedActivationMode != menu.blockEntity.getActivationMode() ||
                selectedPulseDurationTicks != menu.blockEntity.getPulseDurationTicks() ||
                selectedOverrideDisplay != menu.blockEntity.isOverrideDisplay() ||
                selectedOverrideIcon.ID != menu.blockEntity.getOverrideIcon().ID ||
                selectedOverrideColor.getRGB() != menu.blockEntity.getOverrideColor().getRGB()
            ) {
                textComp = APPLY_CHANGES_LABEL;
                applyButton.active = true;
            }
        }

        int tx = 226;
        int ty = 212;

        pGuiGraphics.drawString(this.font, OVERRIDE_DISPLAY_LABEL, this.leftPos+7, this.topPos+203, color, false);
        pGuiGraphics.drawString(this.font, textComp, this.leftPos+tx-Minecraft.getInstance().font.width(textComp), this.topPos+ty, color, false);

        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        return;
    }

    public void selectNetwork(AccessNetwork network) {
        selectedNetwork = network;
        selectedLevels.clear();
        updateLevelEntries();
    }

    public void toggleLevel(AccessLevel level) {
        switch (selectedCompareMode) {
            case SPECIFIC -> {
                if (selectedLevels.contains(level)) {
                    selectedLevels.remove(level);
                } else {
                    selectedLevels.add(level);
                }

                break;
            }

            case BIGGER_THAN_OR_EQUAL, LESSER_THAN_OR_EQUAL -> {
                selectedLevels.clear();
                selectedLevels.add(level);

                break;
            }
        }



    }

    public void updateLevelEntries() {
        clearLevelEntries();
        if (selectedNetwork == null) return;

        ArrayList<AccessLevel> levels = selectedNetwork.getAccessLevels();
        for (int index = 0; index < levels.size(); index++) { // backwards!!!
            SelectableLevelEntry entry = new SelectableLevelEntry(levels.get(levels.size() - index - 1), index);
            levelEntries.add(entry);
            addWidget(entry);
        }

        int totalHeight = levels.size() * (ENTRY_HEIGHT + ENTRY_PADDING) - ENTRY_PADDING;
        scrollMaxLevels = totalHeight - LEVELS_HEIGHT;
    }

    public void updateNetworkEntries() {
        clearNetworkEntries();

        List<AccessNetwork> networks = AccessNetworkDataClient.getNetworks();
        for (int index = 0; index < networks.size(); index++) {
            SelectableNetworkEntry entry = new SelectableNetworkEntry(networks.get(index), index);
            networkEntries.add(entry);
            addWidget(entry);
        }

        int totalHeight = networks.size() * (ENTRY_HEIGHT + ENTRY_PADDING) - ENTRY_PADDING;
        scrollMaxNetworks = totalHeight - NETWORKS_HEIGHT;
    }

    public void updateEntries() {
        selectedNetwork = menu.blockEntity.getAccessNetwork();
        selectedLevels = menu.blockEntity.getAccessLevels();

        if (selectedNetwork == null || AccessUtils.canPlayerEditNetwork(Minecraft.getInstance().player, selectedNetwork)) {
            updateNetworkEntries();
            updateLevelEntries();
        } else {
            clearEntries();
        }
    }

    public void clearNetworkEntries() {
        for (SelectableNetworkEntry entry : networkEntries) {
            removeWidget(entry);
        }
        networkEntries.clear();

        //scrollPosNetworks = 0;
        scrollMaxNetworks = 0;
    }

    public void clearLevelEntries() {
        for (SelectableLevelEntry entry : levelEntries) {
            removeWidget(entry);
        }
        levelEntries.clear();

        //scrollPosLevels = 0;
        scrollMaxLevels = 0;
    }

    public void clearEntries() {
        // clear networks and levels
        clearLevelEntries();
        clearNetworkEntries();
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if ((int)pMouseX <= leftPos + NETWORKS_X + NETWORKS_WIDTH + 4)
            scrollPosNetworks = Math.max(Math.min(scrollPosNetworks + (int)(pDelta * -7), scrollMaxNetworks), 0);
        else
            scrollPosLevels = Math.max(Math.min(scrollPosLevels + (int)(pDelta * -7), scrollMaxLevels), 0);
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    // CONSTANTS

    private static int ENTRY_HEIGHT = 18;
    private static int ENTRY_PADDING = 1;

    private static int NETWORKS_X = 9;
    private static int NETWORKS_Y = 27;
    private static int NETWORKS_WIDTH = 101;
    private static int NETWORKS_HEIGHT = 93;

    private static int LEVELS_X = 120;
    private static int LEVELS_Y = 27;
    private static int LEVELS_WIDTH = 124;
    private static int LEVELS_HEIGHT = 93;

    private class SelectableNetworkEntry extends TexturedButton {
        private AccessNetwork network;
        private int index;

        public SelectableNetworkEntry(AccessNetwork network, int index) {
                super(leftPos + NETWORKS_X, topPos + NETWORKS_Y, NETWORKS_WIDTH, ENTRY_HEIGHT, Component.literal(network.getName()), btn -> {
                    selectNetwork(network);
                }
            );

            this.network = network;
            this.index = index;

            setBounds(leftPos+NETWORKS_X, topPos+NETWORKS_Y, leftPos+NETWORKS_X+NETWORKS_WIDTH, topPos+NETWORKS_Y+NETWORKS_HEIGHT);
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            this.setY(topPos + NETWORKS_Y + index * (ENTRY_HEIGHT + ENTRY_PADDING) - scrollPosNetworks);
            super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public ButtonColor getColor() {
            if (selectedNetwork == this.network) return ButtonColor.GREEN;
            return super.getColor();
        }
    }

    private class SelectableLevelEntry extends TexturedButton {
        private AccessLevel level;
        private int index;

        public SelectableLevelEntry(AccessLevel level, int index) {
            super(leftPos + LEVELS_X, topPos + LEVELS_Y, LEVELS_WIDTH, ENTRY_HEIGHT, Component.literal(level.getName()),
                    btn -> {toggleLevel(level);}
            );

            this.level = level;
            this.index = index;
            this.textPaddingLeft = 21;

            setBounds(leftPos+LEVELS_X, topPos+LEVELS_Y, leftPos+LEVELS_X+LEVELS_WIDTH, topPos+LEVELS_Y+LEVELS_HEIGHT);
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            int y = topPos + LEVELS_Y + index * (ENTRY_HEIGHT + ENTRY_PADDING) - scrollPosLevels;
            this.setY(y);

            super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

            pGuiGraphics.setColor(level.getColor().getRed()/255f, level.getColor().getGreen()/255f, level.getColor().getBlue()/255f, 1f);
            pGuiGraphics.blit(level.getIcon().TEXTURE, this.getX() + 2, y, 0, 0, 18, 18, 18, 18);
            pGuiGraphics.setColor(1f,1f,1f,1f);
        }

        @Override
        public ButtonColor getColor() {
            if (selectedLevels.contains(this.level)) return ButtonColor.GREEN;

            switch (selectedCompareMode) {
                case BIGGER_THAN_OR_EQUAL -> {
                    if (selectedLevels.size() == 0) {
                        break;
                    }

                    if (level.getPriority() >= selectedLevels.get(0).getPriority()) return ButtonColor.GREEN_LESS;
                }
                case LESSER_THAN_OR_EQUAL -> {
                    if (selectedLevels.size() == 0) {
                        break;
                    }

                    if (level.getPriority() <= selectedLevels.get(0).getPriority()) return ButtonColor.GREEN_LESS;
                }
            }

            return super.getColor();
        }
    }
}
