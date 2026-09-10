package net.teekay.axess.block.receiver;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.teekay.axess.Axess;
import net.teekay.axess.block.link.BlockLink;
import net.teekay.axess.block.link.ILinkableBlockEntity;
import net.teekay.axess.block.link.LinkingSystem;
import net.teekay.axess.block.link.payload.AbstractLinkPayload;
import net.teekay.axess.block.link.payload.ReaderUpdateLinkPayload;
import net.teekay.axess.block.readers.AbstractKeycardReaderBlock;
import net.teekay.axess.block.readers.KeycardReaderBlockEntity;
import net.teekay.axess.registry.AxessBlockEntityRegistry;
import net.teekay.axess.utilities.AxessColors;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.core.HolderLookup;


public class ReceiverBlockEntity extends BlockEntity implements ILinkableBlockEntity {

    private ArrayList<BlockLink> blockLinks = new ArrayList<>();

    public ReceiverBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(AxessBlockEntityRegistry.RECEIVER.get(), pPos, pBlockState);
    }

    public boolean getPowered() {
        return getBlockState().getValue(ReceiverBlock.POWERED);
    }

    public BlockState setPowered(boolean p) {
        return getBlockState().setValue(ReceiverBlock.POWERED, p);
    }

    public void activate() {
        level.setBlock(getBlockPos(), setPowered(true), 3);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        level.updateNeighborsAt(getBlockPos().relative(getBlockState().getValue(ReceiverBlock.FACING).getOpposite()), getBlockState().getBlock());
    }

    public void deactivate() {
        level.setBlock(getBlockPos(), setPowered(false), 3);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        level.updateNeighborsAt(getBlockPos().relative(getBlockState().getValue(ReceiverBlock.FACING).getOpposite()), getBlockState().getBlock());
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
        return blockLinks.size() < 1;
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
        if (be instanceof KeycardReaderBlockEntity kr) {
            if (kr.getPowered()) activate(); else deactivate();
        }
    }

    @Override
    public void onClearLinks() {
        deactivate();
    }

    @Override
    public void acceptPayload(AbstractLinkPayload payload) {
        if (payload instanceof ReaderUpdateLinkPayload readerUpdateLinkPayload) {
            if (readerUpdateLinkPayload.getNewState()) {
                activate();
            } else {
                deactivate();
            }
        }
    }

    @Override
    public void setChanged() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        super.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider registries) {
        CompoundTag modTag = new CompoundTag();

        ListTag links = new ListTag();
        for (BlockLink link :
                blockLinks) {
            links.add(link.toNBT());
        }

        modTag.put("BlockLinks", links);

        pTag.put(Axess.MODID, modTag);

        super.saveAdditional(pTag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider registries) {
        CompoundTag modTag = pTag.getCompound(Axess.MODID);

        if (modTag.contains("BlockLinks")) {
            blockLinks.clear();
            ListTag blList = (ListTag) modTag.get("BlockLinks");
            if (blList != null) for (int i = 0; i < blList.size(); i++) {
                blockLinks.add(BlockLink.fromNBT(blList.getCompound(i)));
            }
        }

        super.loadAdditional(pTag, registries);
        setChanged();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }



}
