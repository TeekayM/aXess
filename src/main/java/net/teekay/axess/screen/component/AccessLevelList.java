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

    private int width, height;
    private int leftPos, topPos;
    private int elemHeight = 20;
    private int padding = 1;

    private int scrollerHeight = 14;
    private int scrollerWidth = 4;
    private AccessNetwork network;

    private Consumer<AbstractWidget> childrenAdder;
    private Consumer<AbstractWidget> childrenRemover;

    private AccessLevelEntry dragged = null;
    private int lastPredictedDraggableIndex = -1;

    private boolean orderDirty = false;

    public AccessLevelList(Consumer<AbstractWidget> childrenAdder, Consumer<AbstractWidget> childrenRemover, AccessNetwork network, int leftPos, int topPos, int width, int height) {
        super(leftPos, topPos, width, height, Component.empty());

        this.width = width;
        this.height = height;
        this.leftPos = leftPos;
        this.topPos = topPos;
        this.network = network;
        this.childrenAdder = childrenAdder;
        this.childrenRemover = childrenRemover;
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
        AccessLevelEntry newButton = new AccessLevelEntry(childrenAdder, childrenRemover, level, leftPos, topPos, width, elemHeight,
            () -> removeElement(level), // TRASH
            (entry) -> { // start drag
                if (dragged == null) dragged = entry;
            },
            (entry) -> { // end drag
                
            },
                AxessClientMenus::openIconSelectionScreen,
                AxessClientMenus::openColorSelectionScreen //AxessClientMenus::openIconSelectionScreen
        );

        buttons.add(newButton);

        updateMaxScroll();
        updateOrder();

        return newButton;
    }

    public void removeElement(AccessLevel level) {
        buttons.removeIf((AccessLevelEntry entry) -> entry.accessLevel == level);
        this.network.removeAccessLevel(level);

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

        int scrollerImgPos = (int) ((float)(height-scrollerHeight+1) * ((float)scrollPos / (float)maxScrollPos));
        graphics.blit(TEXTURE, leftPos+width+1, topPos+scrollerImgPos, 0, 198, scrollerWidth, scrollerHeight);

        if (orderDirty) {
            updateOrder();
            orderDirty = false;
        }

        int predictedDraggableIndex = MathUtil.clampInt((mouseY - topPos + scrollPos) / (elemHeight + padding), 0, buttons.size() - 1);
        int initialDragIndex = dragging ? network.getAccessLevels().size() - 1 - dragged.accessLevel.getPriority() : 0;

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

        network.moveLevelToPriority(dragged.accessLevel, (network.getAccessLevels().size() - 1) - lastPredictedDraggableIndex);
        orderDirty = true;
        dragged = null;

        for (AccessLevelEntry e :
                buttons) {
            e.dragging = false;
        }
        
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
}