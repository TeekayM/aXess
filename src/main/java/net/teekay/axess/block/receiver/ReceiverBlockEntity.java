package net.teekay.axess.block.readers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.teekay.axess.Axess;
import net.teekay.axess.access.*;
import net.teekay.axess.screen.KeycardReaderMenu;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public class KeycardReaderBlockEntity extends BlockEntity implements MenuProvider {

    private UUID networkID = null;
    private ArrayList<UUID> accessLevelIDs = new ArrayList<>();
    private AccessCompareMode compareMode = AccessCompareMode.BIGGER_THAN_OR_EQUAL;
    private AccessActivationMode activationMode = AccessActivationMode.TOGGLE;
    private int pulseDurationTicks = 30;

    private BlockPos reader_pairPos = null;
    private UUID reader_pairID = null;

    private BlockPos receiver_pairPos = null;
    private UUID receiver_pairID = null;

    private BlockPos display_pairPos = null;
    private UUID display_pairID = null;

    public static final String ACCESS_LEVELS_KEY = "AccessLevels";
    public static final String ACCESS_NETWORK_KEY  = "AccessNetwork";
    public static final String COMPARE_MODE_KEY  = "CompareMode";
    public static final String ACTIVATION_MODE_KEY  = "ActivationMode";
    public static final String PULSE_DURATION_TICKS_KEY  = "PulseDurationTicks";

    public static final String READER_PAIR_POS_KEY = "ReaderPairPos";
    public static final String READER_PAIR_ID_KEY = "ReaderPairID";

    public static final String RECEIVER_PAIR_POS_KEY = "ReceiverPairPos";
    public static final String RECEIVER_PAIR_ID_KEY = "ReceiverPairID";

    public static final String DISPLAY_PAIR_POS_KEY = "DisplayPairPos";
    public static final String DISPLAY_PAIR_ID_KEY = "DisplayPairID";

    public KeycardReaderBlockEntity(BlockEntityType<?> type, BlockPos pPos, BlockState pBlockState) {
        super(type, pPos, pBlockState);
    }

    public boolean getPowered() {
        return getBlockState().getValue(AbstractKeycardReaderBlock.POWER_STATE);
    }

    public BlockState setPowered(boolean p) {
        return getBlockState().setValue(AbstractKeycardReaderBlock.POWER_STATE, p);
    }

    public Direction getConnectedDirection() {
        switch (getBlockState().getValue(FaceAttachedHorizontalDirectionalBlock.FACE)) {
            case CEILING:
                return Direction.DOWN;
            case FLOOR:
                return Direction.UP;
            default:
                return getBlockState().getValue(FaceAttachedHorizontalDirectionalBlock.FACING);
        }
    }

    public void interact() {
        switch (activationMode) {
            case PULSE -> {
                if (!getPowered()) {
                    activate();
                    execOnPair(KeycardReaderBlockEntity::activate);
                }
            }

            case TOGGLE -> {
                if (!getPowered()) {
                    activate();
                    execOnPair(KeycardReaderBlockEntity::activate);
                } else {
                    deactivate();
                    execOnPair(KeycardReaderBlockEntity::deactivate);
                }
            }
        }
    }

    public void activate() {
        level.setBlock(getBlockPos(), setPowered(true), 3);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        level.updateNeighborsAt(getBlockPos().relative(getConnectedDirection().getOpposite()), getBlockState().getBlock());

        if (activationMode == AccessActivationMode.PULSE)
            level.scheduleTick(getBlockPos(), getBlockState().getBlock(), pulseDurationTicks);
    }

    public void deactivate() {
        level.setBlock(getBlockPos(), getBlockState().setValue(AbstractKeycardReaderBlock.POWER_STATE, false), 3);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        level.updateNeighborsAt(getBlockPos().relative(getConnectedDirection().getOpposite()), getBlockState().getBlock());
    }

    public void execOnPair(Consumer<KeycardReaderBlockEntity> exec) {
        KeycardReaderBlockEntity pair = getReaderPair();

        if (pair != null) exec.accept(pair);
    }

    public void pairReceiver(KeycardReaderBlockEntity secondReceiver) {
        setReaderPairPos(secondReceiver.getBlockPos());
        secondReceiver.setReaderPairPos(getBlockPos());
        UUID pairID = UUID.randomUUID();
        setReaderPairID(pairID);
        secondReceiver.setReaderPairID(pairID);

        secondReceiver.setActivationMode(activationMode);

        setChanged();
        secondReceiver.setChanged();
    }

    @Override
    public void setChanged() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        super.setChanged();
    }

    @Nullable
    public AccessNetwork getAccessNetwork() {
        if (this.level == null || this.level.isClientSide) {
            return AccessNetworkDataClient.getNetwork(networkID);
        } else {
            return AccessNetworkDataServer.get(this.level.getServer()).getNetwork(networkID);
        }
    }

    @Nullable
    public ArrayList<AccessLevel> getAccessLevels() {
        AccessNetwork network = getAccessNetwork();

        ArrayList<AccessLevel> levels = new ArrayList<>();

        if (network == null) return levels;

        for (UUID uuid : accessLevelIDs) {
            if (network.hasAccessLevel(uuid))
                levels.add(network.getAccessLevel(uuid));
        }

        return levels;
    }

    public AccessCompareMode getCompareMode() {
        return compareMode;
    }
    public AccessActivationMode getActivationMode() {
        return activationMode;
    }
    public int getPulseDurationTicks() {
        return pulseDurationTicks;
    }

    public KeycardReaderBlockEntity getReaderPair() {
        if (level == null) return null;
        if (reader_pairPos == null) return null;
        if (reader_pairID == null) return null;

        if (level.getBlockEntity(reader_pairPos) instanceof KeycardReaderBlockEntity e) {
            if (reader_pairID.equals(e.getReaderPairID())) return e;

            reader_pairPos = null;
            reader_pairID = null;
            setChanged();
        }
        return null;
    }

    public UUID getReaderPairID() {
        return reader_pairID;
    }
    public UUID getDisplayPairID() {
        return display_pairID;
    }
    public UUID getReceiverPairID() {
        return receiver_pairID;
    }

    public void setAccessNetwork(AccessNetwork network) {
        if (network == null) {this.networkID = null; return;}

        this.networkID = network.getUUID();
    }

    public void setAccessLevels(ArrayList<AccessLevel> levels) {
        if (levels == null || levels.size() == 0) { accessLevelIDs.clear(); return; }

        accessLevelIDs.clear();

        for (AccessLevel level :
                levels) {
            accessLevelIDs.add(level.getUUID());
        }
    }

    public void setCompareMode(AccessCompareMode compareMode) {
        this.compareMode = compareMode;
    }
    public void setActivationMode(AccessActivationMode activationMode) {
        this.activationMode = activationMode;
    }
    public void setPulseDurationTicks(int pulseDurationTicks) {
        this.pulseDurationTicks = pulseDurationTicks;
    }

    public void setReaderPairPos(BlockPos pairPos) {
        this.reader_pairPos = pairPos;
    }
    public void setReaderPairID(UUID pairID) {
        this.reader_pairID = pairID;
    }

    public void setDisplayPairPos(BlockPos pairPos) {
        this.display_pairPos = pairPos;
    }
    public void setDisplayPairID(UUID pairID) {
        this.display_pairID = pairID;
    }

    public void setReceiverPairPos(BlockPos pairPos) {
        this.receiver_pairPos = pairPos;
    }
    public void setReceiverPairID(UUID pairID) {
        this.receiver_pairID = pairID;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        CompoundTag modTag = new CompoundTag();

        if (networkID != null) {
            modTag.putUUID(ACCESS_NETWORK_KEY, networkID);

            ListTag accessLevelsTag = new ListTag();

            for (UUID levelID :
                    accessLevelIDs) {
                CompoundTag x = new CompoundTag();
                x.putUUID("UUID", levelID);
                accessLevelsTag.add(x);
            }

            modTag.put(ACCESS_LEVELS_KEY, accessLevelsTag);
        }

        modTag.putString(COMPARE_MODE_KEY, compareMode.toString());
        modTag.putString(ACTIVATION_MODE_KEY, activationMode.toString());
        modTag.putInt(PULSE_DURATION_TICKS_KEY, pulseDurationTicks);

        if (reader_pairPos != null) modTag.putLong(READER_PAIR_POS_KEY, reader_pairPos.asLong());
        if (reader_pairID != null) modTag.putUUID(READER_PAIR_ID_KEY, reader_pairID);

        if (receiver_pairPos != null) modTag.putLong(RECEIVER_PAIR_POS_KEY, receiver_pairPos.asLong());
        if (receiver_pairID != null) modTag.putUUID(RECEIVER_PAIR_ID_KEY, receiver_pairID);

        if (display_pairPos != null) modTag.putLong(DISPLAY_PAIR_POS_KEY, display_pairPos.asLong());
        if (display_pairID != null) modTag.putUUID(DISPLAY_PAIR_ID_KEY, display_pairID);

        pTag.put(Axess.MODID, modTag);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        CompoundTag modTag = pTag.getCompound(Axess.MODID);

        if (modTag.contains(ACCESS_NETWORK_KEY))
            networkID = modTag.getUUID(ACCESS_NETWORK_KEY);

        if (networkID != null && modTag.contains(ACCESS_LEVELS_KEY) && modTag.get(ACCESS_LEVELS_KEY) != null) {
            ListTag accessLevelsTag = (ListTag) modTag.get(ACCESS_LEVELS_KEY);
            accessLevelIDs.clear();

            for (int i = 0; i < accessLevelsTag.size(); i++) {
                accessLevelIDs.add(((CompoundTag)accessLevelsTag.get(i)).getUUID("UUID"));
            }
        }

        compareMode = AccessCompareMode.valueOf(modTag.getString(COMPARE_MODE_KEY));
        activationMode = AccessActivationMode.valueOf(modTag.getString(ACTIVATION_MODE_KEY));
        pulseDurationTicks = modTag.getInt(PULSE_DURATION_TICKS_KEY);

        long longReaderPairPos = modTag.getLong(READER_PAIR_POS_KEY);
        if (longReaderPairPos != 0L)
            reader_pairPos = BlockPos.of(longReaderPairPos);
        if (modTag.contains(READER_PAIR_ID_KEY))
            reader_pairID = modTag.getUUID(READER_PAIR_ID_KEY);

        long longReceiverPairPos = modTag.getLong(RECEIVER_PAIR_POS_KEY);
        if (longReceiverPairPos != 0L)
            receiver_pairPos = BlockPos.of(longReceiverPairPos);
        if (modTag.contains(RECEIVER_PAIR_ID_KEY))
            receiver_pairID = modTag.getUUID(RECEIVER_PAIR_ID_KEY);

        long longDisplayPairPos = modTag.getLong(DISPLAY_PAIR_POS_KEY);
        if (longDisplayPairPos != 0L)
            display_pairPos = BlockPos.of(longDisplayPairPos);
        if (modTag.contains(DISPLAY_PAIR_ID_KEY))
            display_pairID = modTag.getUUID(DISPLAY_PAIR_ID_KEY);

        super.load(pTag);
        setChanged();
    }

    private static final Component TITLE = Component.translatable("gui." + Axess.MODID + ".keycard_reader");

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new KeycardReaderMenu(pContainerId, pPlayerInventory, this);
    }

    @Override
    public Component getDisplayName() {
        return TITLE;
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
