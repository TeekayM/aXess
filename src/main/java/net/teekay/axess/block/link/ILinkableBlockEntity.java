package net.teekay.axess.block.link;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.teekay.axess.block.link.payload.AbstractLinkPayload;

import java.awt.*;
import java.util.ArrayList;

public interface ILinkableBlockEntity {
    BlockEntity getBlockEntity();

    Color getLinkingColor();

    ArrayList<BlockLink> getLinks();
    boolean canLink();
    boolean canLinkWith(BlockEntity be);
    boolean canBeLinkedBy(Player player);
    void onLinkWith(BlockEntity be, boolean first);
    void onClearLinks();

    void acceptPayload(AbstractLinkPayload payload);
}