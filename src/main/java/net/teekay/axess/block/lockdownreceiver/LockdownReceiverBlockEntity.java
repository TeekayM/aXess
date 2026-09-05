package net.teekay.axess.block.lockdownreceiver;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.teekay.axess.Axess;
import net.teekay.axess.block.link.BlockLink;
import net.teekay.axess.block.link.ILinkableBlockEntity;
import net.teekay.axess.block.link.LinkingSystem;
import net.teekay.axess.block.link.payload.AbstractLinkPayload;
import net.teekay.axess.block.link.payload.LockdownUpdateLinkPayload;
import net.teekay.axess.block.link.payload.ReaderUpdateLinkPayload;
import net.teekay.axess.block.readers.KeycardReaderBlockEntity;
import net.teekay.axess.registry.AxessBlockEntityRegistry;
import net.teekay.axess.utilities.AxessColors;

import java.awt.*;
import java.util.ArrayList;

public class LockdownReceiverBlockEntity extends BlockEntity implements ILinkableBlockEntity {

    private ArrayList<BlockLink> blockLinks = new ArrayList<>();

    public LockdownReceiverBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(AxessBlockEntityRegistry.LOCKDOWN_RECEIVER.get(), pPos, pBlockState);
    }

    public boolean getPowered() {
        return getBlockState().getValue(LockdownReceiverBlock.POWERED);
    }

    public BlockState setPowered(boolean p) {
        return getBlockState().setValue(LockdownReceiverBlock.POWERED, p);
    }

    public void activate() {
        LinkingSystem.emitPayloadToConnections(this, new LockdownUpdateLinkPayload(this, true));
        level.setBlock(getBlockPos(), setPowered(true), 3);
    }

    public void deactivate() {
        LinkingSystem.emitPayloadToConnections(this, new LockdownUpdateLinkPayload(this, false));
        level.setBlock(getBlockPos(), setPowered(false), 3);
    }

    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    @Override
    public Color getLinkingColor() {
        return AxessColors.RED;
    }

    @Override
    public ArrayList<BlockLink> getLinks() {
        return blockLinks;
    }

    @Override
    public boolean canLink() {
        return true;
    }

    @Override
    public boolean canLinkWith(BlockEntity be) {
        return be instanceof KeycardReaderBlockEntity;
    }

    @Override
    public boolean canBeLinkedBy(Player player) {
        boolean can = true;
        for (BlockLink link : blockLinks) {
            ILinkableBlockEntity lbe = LinkingSystem.getLinkableAtBlockPos(level, link.getOther(getBlockPos()));
            if (lbe == null) continue;
            can = can && lbe.canBeLinkedBy(player);
        }
        return can;
    }

    @Override
    public void onLinkWith(BlockEntity be, boolean first) {
        if (be instanceof ILinkableBlockEntity link) {
            LockdownUpdateLinkPayload payload = new LockdownUpdateLinkPayload(this, getPowered());
            link.acceptPayload(payload);
        }

    }

    @Override
    public void onClearLinks() {}

    @Override
    public void acceptPayload(AbstractLinkPayload payload) {}

    @Override
    public void setChanged() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        super.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        CompoundTag modTag = new CompoundTag();

        ListTag links = new ListTag();
        for (BlockLink link :
                blockLinks) {
            links.add(link.toNBT());
        }

        modTag.put("BlockLinks", links);

        pTag.put(Axess.MODID, modTag);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        CompoundTag modTag = pTag.getCompound(Axess.MODID);

        if (modTag.contains("BlockLinks")) {
            blockLinks.clear();
            ListTag blList = (ListTag) modTag.get("BlockLinks");
            if (blList != null) for (int i = 0; i < blList.size(); i++) {
                blockLinks.add(BlockLink.fromNBT(blList.getCompound(i)));
            }
        }

        super.load(pTag);
        setChanged();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }



}
