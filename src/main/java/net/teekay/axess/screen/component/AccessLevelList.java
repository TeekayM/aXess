package net.teekay.axess.screen.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.client.AxessClientMenus;
import net.teekay.axess.screen.NetworkEditorScreen;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.MathUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class AccessLevelList extends AbstractWidget {
    private final List<AccessLevelEntry> buttons = new ArrayList<>();

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/network_editor.png");

    private static final Component EDIT_TEXT = Component.translatable("gui."+Axess.MODID+".button.edit");
    private static final Component DELETE_TEXT = Component.translatable("gui."+Axess.MODID+".button.delete");

    public int scrollPos = 0;
    private int maxScrollPos = 0;

    public int width, height;
    public int leftPos, topPos;
    public int elemHeight = 20;
    public int padding = 1;

    private int scrollerHeight = 14;
    private int scrollerWidth = 4;

    private AccessLevelEntry dragged = null;
    private int lastPredictedDraggableIndex = -1;

    private boolean orderDirty = false;

    public NetworkEditorScreen screen;

    public void startDrag(AccessLevelEntry entry) {
        if (dragged == null) dragged = entry;
    }

    public void trash(AccessLevelEntry entry) {
        removeElement(entry.accessLevel);
    }

    public AccessLevelList(NetworkEditorScreen screen) {
        super(screen.leftPos + 14, screen.topPos + 51, 224, 116, Component.empty());

        this.width = 224;
        this.height = 116;
        this.leftPos = screen.leftPos + 14;
        this.topPos = screen.topPos + 51;
        this.screen = screen;
    }

    private void updateMaxScroll() {
        int totalHeight = buttons.size() * elemHeight + (buttons.size() - 1) * padding;
        this.maxScrollPos = totalHeight - height;
    }

    private void updateOrder() {
        buttons.sort(Comparator.comparingInt((AccessLevelEntry a) -> -a.accessLevel.getPriority()));
    }

    public AccessLevelEntry addElement(AccessLevel level) {
        // on edit icon
        AccessLevelEntry newButton = new AccessLevelEntry(this, level);

        buttons.add(newButton);

        updateMaxScroll();
        updateOrder();

        return newButton;
    }

    public void removeElement(AccessLevel level) {
        buttons.removeIf((AccessLevelEntry entry) -> entry.accessLevel == level);
        screen.network.removeAccessLevel(level);

        updateMaxScroll();
        updateOrder();
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.enableScissor(leftPos, topPos, leftPos+width+scrollerWidth+1, topPos+height);

        boolean dragging = dragged != null;

        if (dragging) {
            if (mouseY < topPos) {
                scroll(-Math.round((topPos - mouseY) / 3f * partialTick));
            } else if (mouseY > topPos + height) {
                scroll(Math.round((mouseY - topPos - height) / 3f * partialTick));
            }
        }

        int totalElementsHeight = maxScrollPos + height;

        int scrollerHeight;
        if (height >= totalElementsHeight) {
            scrollerHeight = height;
        } else {
            scrollerHeight = (int)Math.ceil((float)(height-1) * (float)height / (float)totalElementsHeight);
        }

        int scrollerPos = (int) ((float)(height-scrollerHeight+1) * ((float)scrollPos / (float)maxScrollPos));

        graphics.fill(leftPos+width+1, topPos+scrollerPos, leftPos+width+1+scrollerWidth, topPos+scrollerPos+scrollerHeight, AxessColors.MAIN.getRGB());

        if (orderDirty) {
            updateOrder();
            orderDirty = false;
        }

        int predictedDraggableIndex = MathUtil.clampInt((mouseY - topPos + scrollPos) / (elemHeight + padding), 0, buttons.size() - 1);
        int initialDragIndex = dragging ? screen.network.getAccessLevels().size() - 1 - dragged.accessLevel.getPriority() : 0;

        int index = 0;
        for (AccessLevelEntry accessLevelEntry : buttons) {
            int offset = 0;

            if (dragging && index > initialDragIndex) offset--;
            if (dragging && index > predictedDraggableIndex) offset++;
            if (dragging && predictedDraggableIndex < initialDragIndex && index == predictedDraggableIndex) offset++;

            int yPos = topPos + (index + offset) * (elemHeight + padding);

            if (!dragging || accessLevelEntry != dragged) {
                accessLevelEntry.updateYPos(yPos, partialTick, -scrollPos);
                accessLevelEntry.render(graphics, mouseX, mouseY, partialTick);
            }

            index++;

            //graphics.drawString(Minecraft.getInstance().font, index + " " + accessLevelEntry.accessLevel.getPriority() + " " + offset, leftPos + 4, yPos - scrollPos, 0xFFFFFF);
        }

        graphics.disableScissor();

        //graphics.drawString(Minecraft.getInstance().font, String.valueOf(predictedDraggableIndex), mouseX, mouseY, 0xFFFFFF);

        if (dragging) {
            dragged.forceUpdateYPos(mouseY - 10, partialTick);
            dragged.render(graphics, mouseX, mouseY, partialTick);
        }

        lastPredictedDraggableIndex = predictedDraggableIndex;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    public void scroll(int delta) {
        this.scrollPos = Math.max(Math.min(scrollPos + delta, maxScrollPos), 0);
    }

    public int getSize() {
        return this.buttons.size();
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {

        if (dragged == null) return super.mouseReleased(pMouseX, pMouseY, pButton);

        //System.out.println(dragged.accessLevel.getDisplayName());
        //System.out.println(lastPredictedDraggableIndex);
        //System.out.println((network.getAccessLevels().size() - 1) - lastPredictedDraggableIndex);

        screen.network.moveLevelToPriority(dragged.accessLevel, (screen.network.getAccessLevels().size() - 1) - lastPredictedDraggableIndex);
        orderDirty = true;
        dragged = null;

        for (AccessLevelEntry e :
                buttons) {
            e.dragging = false;
            e.dragButton.dragging = false;
        }
        
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
}