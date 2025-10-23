package net.teekay.axess.block.receiver;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.teekay.axess.Axess;
import net.teekay.axess.block.IPairableBlockEntity;
import net.teekay.axess.block.readers.AbstractKeycardReaderBlock;
import net.teekay.axess.block.readers.KeycardReaderBlock;
import net.teekay.axess.block.readers.KeycardReaderBlockEntity;
import net.teekay.axess.registry.AxessBlockEntityRegistry;

import java.util.UUID;

public class ReceiverBlockEntity extends BlockEntity implements IPairableBlockEntity {


    private BlockPos reader_pairPos = null;
    private UUID reader_pairID = null;

    public static final String READER_PAIR_POS_KEY = "ReaderPairPos";
    public static final String READER_PAIR_ID_KEY = "ReaderPairID";

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
    }

    public void deactivate() {
        level.setBlock(getBlockPos(), getBlockState().setValue(AbstractKeycardReaderBlock.POWERED, false), 3);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
    }

    public KeycardReaderBlockEntity getReaderPair() {
        if (level == null) return null;
        if (reader_pairPos == null) return null;
        if (reader_pairID == null) return null;

        if (level.getBlockEntity(reader_pairPos) instanceof KeycardReaderBlockEntity e) {
            if (reader_pairID.equals(e.getReceiverPairID())) return e;

            reader_pairPos = null;
            reader_pairID = null;
            setChanged();
        }
        return null;
    }

    @Override
    public boolean canPairWith(BlockEntity be) {
        return be instanceof KeycardReaderBlockEntity;
    }

    @Override
    public boolean canBePairedBy(Player player) {
        KeycardReaderBlockEntity keycardReader = getReaderPair();
        if (keycardReader != null) {
            return keycardReader.canBePairedBy(player);
        }
        return true;
    }

    @Override
    public void handlePairing(BlockEntity be) {
        if (be instanceof KeycardReaderBlockEntity reader) {
            reader.handlePairing(this); // pass to other
        }
    }

    @Override
    public void clearPairings() {
        reader_pairID = null;
        reader_pairPos = null;
        setChanged();
    }

    @Override
    public void setChanged() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        super.setChanged();
    }

    public UUID getReaderPairID() {
        return reader_pairID;
    }

    public void setReaderPairPos(BlockPos pairPos) {
        this.reader_pairPos = pairPos;
    }
    public void setReaderPairID(UUID pairID) {
        this.reader_pairID = pairID;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        CompoundTag modTag = new CompoundTag();

        if (reader_pairPos != null) modTag.putLong(READER_PAIR_POS_KEY, reader_pairPos.asLong());
        if (reader_pairID != null) modTag.putUUID(READER_PAIR_ID_KEY, reader_pairID);

        pTag.put(Axess.MODID, modTag);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        CompoundTag modTag = pTag.getCompound(Axess.MODID);

        reader_pairPos = null;
        reader_pairID = null;

        long longReaderPairPos = modTag.getLong(READER_PAIR_POS_KEY);
        if (longReaderPairPos != 0L)
            reader_pairPos = BlockPos.of(longReaderPairPos);
        if (modTag.contains(READER_PAIR_ID_KEY))
            reader_pairID = modTag.getUUID(READER_PAIR_ID_KEY);

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
