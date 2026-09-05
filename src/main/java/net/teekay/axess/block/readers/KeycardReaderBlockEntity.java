package net.teekay.axess.block.readers;

import com.mojang.datafixers.util.Pair;
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
import net.teekay.axess.AxessConfig;
import net.teekay.axess.access.*;
import net.teekay.axess.block.link.BlockLink;
import net.teekay.axess.block.link.ILinkableBlockEntity;
import net.teekay.axess.block.link.LinkingSystem;
import net.teekay.axess.block.link.payload.AbstractLinkPayload;
import net.teekay.axess.block.link.payload.LockdownUpdateLinkPayload;
import net.teekay.axess.block.link.payload.ReaderPropertiesLinkPayload;
import net.teekay.axess.block.link.payload.ReaderUpdateLinkPayload;
import net.teekay.axess.block.lockdownreceiver.LockdownReceiverBlockEntity;
import net.teekay.axess.block.receiver.ReceiverBlockEntity;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.screen.KeycardReaderMenu;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.AxessUtilities;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public class KeycardReaderBlockEntity extends BlockEntity implements MenuProvider, ILinkableBlockEntity {

    private UUID networkID = null;
    private ArrayList<UUID> accessLevelIDs = new ArrayList<>();
    private ArrayList<Pair<UUID, UUID>> overrideAccessLevelsIDs = new ArrayList<>();
    private AccessCompareMode compareMode = AccessCompareMode.BIGGER_THAN_OR_EQUAL;
    private AccessActivationMode activationMode = AccessActivationMode.TOGGLE;
    private int pulseDurationTicks = 30;

    private ArrayList<BlockLink> blockLinks = new ArrayList<>();

    private boolean overrideDisplay = false;
    private AxessIconRegistry.AxessIcon overrideIcon = AxessIconRegistry.NONE;
    private Color overrideColor = Color.WHITE;

    public static final String ACCESS_LEVELS_KEY = "AccessLevels";
    public static final String OVERRIDE_ACCESS_LEVELS_KEY = "OverrideAccessLevels";
    public static final String ACCESS_NETWORK_KEY  = "AccessNetwork";
    public static final String COMPARE_MODE_KEY  = "CompareMode";
    public static final String ACTIVATION_MODE_KEY  = "ActivationMode";
    public static final String PULSE_DURATION_TICKS_KEY  = "PulseDurationTicks";

    public static final String OVERRIDE_DISPLAY_KEY = "OverrideDisplay";
    public static final String OVERRIDE_ICON_KEY = "OverrideIcon";
    public static final String OVERRIDE_COLOR_KEY = "OverrideColor";

    public static final String BLOCKLINK_KEY = "BlockLinks";

    public KeycardReaderBlockEntity(BlockEntityType<?> type, BlockPos pPos, BlockState pBlockState) {
        super(type, pPos, pBlockState);
    }

    public boolean getPowered() {
        return getBlockState().getValue(AbstractKeycardReaderBlock.POWERED);
    }

    public BlockState setPowered(boolean p) {
        return getBlockState().setValue(AbstractKeycardReaderBlock.POWERED, p);
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
                }
            }

            case TOGGLE -> {
                if (!getPowered()) {
                    activate();
                } else {
                    deactivate();
                }
            }
        }
    }

    public void updateOthers() {
        LinkingSystem.emitPayloadToConnections(this,
                new ReaderPropertiesLinkPayload(this, compareMode, activationMode, pulseDurationTicks)
        );
    }

    public void activate() {
        _activate();
        LinkingSystem.emitPayloadToConnections(
                this,
                new ReaderUpdateLinkPayload(this, true)
        );
    }

    public void deactivate() {
        _deactivate();
        LinkingSystem.emitPayloadToConnections(
                this,
                new ReaderUpdateLinkPayload(this, false)
        );
    }

    private void _activate() {
        level.setBlock(getBlockPos(), setPowered(true), 3);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        level.updateNeighborsAt(getBlockPos().relative(getConnectedDirection().getOpposite()), getBlockState().getBlock());

        if (activationMode == AccessActivationMode.PULSE)
            level.scheduleTick(getBlockPos(), getBlockState().getBlock(), pulseDurationTicks);
    }

    private void _deactivate() {
        level.setBlock(getBlockPos(), getBlockState().setValue(AbstractKeycardReaderBlock.POWERED, false), 3);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        level.updateNeighborsAt(getBlockPos().relative(getConnectedDirection().getOpposite()), getBlockState().getBlock());
    }

    private void _lock() {
        level.setBlock(getBlockPos(), getBlockState().setValue(AbstractKeycardReaderBlock.POWERED, false).setValue(AbstractKeycardReaderBlock.LOCKED, true), 3);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        level.updateNeighborsAt(getBlockPos().relative(getConnectedDirection().getOpposite()), getBlockState().getBlock());
    }

    private void _unlock() {
        level.setBlock(getBlockPos(), getBlockState().setValue(AbstractKeycardReaderBlock.LOCKED, false), 3);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        level.updateNeighborsAt(getBlockPos().relative(getConnectedDirection().getOpposite()), getBlockState().getBlock());
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
        return AxessUtilities.getAccessNetworkFromID(networkID, level);
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

    public ArrayList<Pair<AccessNetwork, AccessLevel>> getOverrideAccessLevels() {
        ArrayList<Pair<AccessNetwork, AccessLevel>> oLevels = new ArrayList<>();

        for (Pair<UUID, UUID> pair : overrideAccessLevelsIDs) {
            AccessNetwork net = AxessUtilities.getAccessNetworkFromID(pair.getFirst(), level);
            if (net == null) continue;

            AccessLevel level = net.getAccessLevel(pair.getSecond());
            if (level == null)  continue;

            oLevels.add(Pair.of(net, level));
        }

        return oLevels;
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

    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    @Override
    public Color getLinkingColor() {
        return AxessColors.MAIN;
    }

    @Override
    public ArrayList<BlockLink> getLinks() {
        return blockLinks;
    }

    @Override
    public boolean canLink() {
        return blockLinks.size() < AxessConfig.maxLinksReader;
    }

    @Override
    public boolean canLinkWith(BlockEntity be) {
        return be instanceof KeycardReaderBlockEntity
                || be instanceof ReceiverBlockEntity
                || (be instanceof LockdownReceiverBlockEntity && getLinks().stream().noneMatch(
                        (v) -> level.getBlockEntity(v.getOther(getBlockPos())) instanceof LockdownReceiverBlockEntity
        ));
    }

    @Override
    public boolean canBeLinkedBy(Player player) {
        if (getAccessNetwork() == null) return false;
        return getAccessNetwork().hasPermission(player, AccessPermission.READER_LINK);
    }

    @Override
    public void onLinkWith(BlockEntity be, boolean first) {
        if (!first) return;
        LinkingSystem.emitPayloadToConnections(
                this,
                new ReaderPropertiesLinkPayload(
                        this, compareMode, activationMode, pulseDurationTicks
                )
        );
    }

    @Override
    public void acceptPayload(AbstractLinkPayload payload) {
        if (payload instanceof ReaderPropertiesLinkPayload rpPayload) {
            setActivationMode(rpPayload.getActivationMode());
            setCompareMode(rpPayload.getCompareMode());
            setPulseDurationTicks(rpPayload.getPulseDurationTicks());
            setChanged();
        } else if (payload instanceof ReaderUpdateLinkPayload ruPayload) {
            if (ruPayload.getNewState()) {
                _activate();
            } else {
                _deactivate();
            }
        } else if (payload instanceof LockdownUpdateLinkPayload luPayload) {
            if (luPayload.getNewState()) {
                _lock();
            } else {
                _unlock();
            }
        }
    }

    @Override
    public void onClearLinks() {
        _deactivate();
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

    public void setOverrideAccessLevels(ArrayList<Pair<AccessNetwork, AccessLevel>> levels) {
        if (levels == null || levels.size() == 0) { overrideAccessLevelsIDs.clear(); return; }

        overrideAccessLevelsIDs.clear();

        for (Pair<AccessNetwork, AccessLevel> levelPair :
                levels) {
            overrideAccessLevelsIDs.add(Pair.of(levelPair.getFirst().getUUID(), levelPair.getSecond().getUUID()));
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

    public boolean isOverrideDisplay() {
        return overrideDisplay;
    }

    public void setOverrideDisplay(boolean overrideDisplay) {
        this.overrideDisplay = overrideDisplay;
    }

    public AxessIconRegistry.AxessIcon getOverrideIcon() {
        return overrideIcon;
    }

    public void setOverrideIcon(AxessIconRegistry.AxessIcon overrideIcon) {
        this.overrideIcon = overrideIcon;
    }

    public Color getOverrideColor() {
        return overrideColor;
    }

    public void setOverrideColor(Color overrideColor) {
        this.overrideColor = overrideColor;
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

        // OVERRIDE ACCESS LEVELS
        ListTag accessLevelsTag = new ListTag();

        for (Pair<UUID, UUID> p :
                overrideAccessLevelsIDs) {
            CompoundTag x = new CompoundTag();
            x.putUUID("NetworkUUID", p.getFirst());
            x.putUUID("LevelUUID", p.getSecond());
            accessLevelsTag.add(x);
        }

        modTag.put(OVERRIDE_ACCESS_LEVELS_KEY, accessLevelsTag);
        // END

        modTag.putString(COMPARE_MODE_KEY, compareMode.toString());
        modTag.putString(ACTIVATION_MODE_KEY, activationMode.toString());
        modTag.putInt(PULSE_DURATION_TICKS_KEY, pulseDurationTicks);

        ListTag blList = new ListTag();
        for (BlockLink link :
                blockLinks) {
            blList.add(link.toNBT());
        }

        modTag.put(BLOCKLINK_KEY, blList);

        modTag.putBoolean(OVERRIDE_DISPLAY_KEY, overrideDisplay);
        modTag.putString(OVERRIDE_ICON_KEY, overrideIcon.ID);
        modTag.putInt(OVERRIDE_COLOR_KEY, overrideColor.getRGB());

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

        if (modTag.contains(OVERRIDE_ACCESS_LEVELS_KEY) && modTag.get(OVERRIDE_ACCESS_LEVELS_KEY) != null) {
            ListTag overrideAccessLevelsTag = (ListTag) modTag.get(OVERRIDE_ACCESS_LEVELS_KEY);
            overrideAccessLevelsIDs.clear();

            for (int i = 0; i < overrideAccessLevelsTag.size(); i++) {
                CompoundTag tag = (CompoundTag)overrideAccessLevelsTag.get(i);
                overrideAccessLevelsIDs.add(Pair.of(tag.getUUID("NetworkUUID"), tag.getUUID("LevelUUID")));
            }
        }

        compareMode = AccessCompareMode.valueOf(modTag.getString(COMPARE_MODE_KEY));
        activationMode = AccessActivationMode.valueOf(modTag.getString(ACTIVATION_MODE_KEY));
        pulseDurationTicks = modTag.getInt(PULSE_DURATION_TICKS_KEY);

        if (modTag.contains(BLOCKLINK_KEY)) {
            ListTag blList = (ListTag) modTag.get(BLOCKLINK_KEY);
            blockLinks.clear();

            if (blList != null) for (int i = 0; i < blList.size(); i++) {
                blockLinks.add(BlockLink.fromNBT((CompoundTag) blList.getCompound(i)));
            }
        }


        if (modTag.contains(OVERRIDE_DISPLAY_KEY))
            overrideDisplay = modTag.getBoolean(OVERRIDE_DISPLAY_KEY);

        if (modTag.contains(OVERRIDE_ICON_KEY))
            overrideIcon = AxessIconRegistry.getIcon(modTag.getString(OVERRIDE_ICON_KEY));

        if (modTag.contains(OVERRIDE_COLOR_KEY))
            overrideColor = new Color(modTag.getInt(OVERRIDE_COLOR_KEY));

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
