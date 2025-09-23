package net.teekay.axess.screen.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.screen.NetworkManagerScreen;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.MathUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NetworkList {
    private final List<NetworkEntry> buttons = new ArrayList<>();

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/network_manager.png");

    private int scrollPos = 0;
    private int maxScrollPos = 0;

    private int width, height;
    private int leftPos, topPos;
    private int elemHeight = 20;
    private int padding = 1;

    private int scrollerWidth = 4;

    public NetworkList(int leftPos, int topPos, int width, int height) {
        this.width = width;
        this.height = height;
        this.leftPos = leftPos;
        this.topPos = topPos;
    }

    private void updateMaxScroll() {
        int totalHeight = buttons.size() * (elemHeight + padding) - padding;
        this.maxScrollPos = totalHeight - height;
    }

    public NetworkEntry addElement(AccessNetwork network) {
        NetworkEntry newButton = new NetworkEntry(network, leftPos, topPos, width, elemHeight);
        buttons.add(newButton);
        updateMaxScroll();
        return newButton;
    }


    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.enableScissor(leftPos, topPos, leftPos+width+scrollerWidth+1, topPos+height);

        int scrollerHeight = MathUtil.calcScrollHeight(height, maxScrollPos);
        int scrollerPos = MathUtil.calcScrollPos(height, scrollerHeight, scrollPos, maxScrollPos);

        graphics.fill(leftPos+width+1, topPos+scrollerPos, leftPos+width+1+scrollerWidth, topPos+scrollerPos+scrollerHeight, AxessColors.MAIN.getRGB());

        for (int index = 0; index < buttons.size(); index++) {
            NetworkEntry networkButton = buttons.get(index);

            int yPos = topPos + index * elemHeight - scrollPos + index * padding;

            networkButton.button.setY(yPos);
            networkButton.trashButton.setY(yPos);

            networkButton.button.render(graphics, mouseX, mouseY, partialTick);
            networkButton.trashButton.render(graphics, mouseX, mouseY, partialTick);

            //graphics.drawString(Minecraft.getInstance().font, network.getName(), leftPos + 4, yPos + (elemHeight - 7) / 2, 0xFFFFFF);
        }

        graphics.disableScissor();


    }

    public void scroll(int delta) {
        this.scrollPos = Math.max(Math.min(scrollPos + delta, maxScrollPos), 0);
    }

    public int getSize() {
        return this.buttons.size();
    }
}