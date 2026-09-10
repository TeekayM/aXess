package net.teekay.axess.network.packets.server;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.teekay.axess.access.*;
import net.teekay.axess.block.readers.KeycardReaderBlockEntity;
import net.teekay.axess.network.IAxessPacket;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.screen.KeycardReaderMenu;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.HolderLookup;



public class CtSModifyKeycardReaderPacket implements IAxessPacket, CustomPacketPayload {
    public static final CustomPacketPayload.Type<CtSModifyKeycardReaderPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(net.teekay.axess.Axess.MODID, "ct_smodify_keycard_reader"));

    public static final StreamCodec<FriendlyByteBuf, CtSModifyKeycardReaderPacket> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), CtSModifyKeycardReaderPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public BlockPos blockEntityPos;
    public UUID networkUUID;
    public ArrayList<UUID> accessLevelsUUIDs;
    public ArrayList<Pair<UUID, UUID>> overrideAccessLevelsUUIDs;
    public AccessCompareMode compareMode;
    public AccessActivationMode activationMode;
    public int pulseDurationTicks;
    public boolean overrideDisplay;
    public AxessIconRegistry.AxessIcon overrideIcon;
    public Color overrideColor;

    public CtSModifyKeycardReaderPacket(BlockPos blockEntityPos, AccessNetwork network, ArrayList<AccessLevel> levels, ArrayList<Pair<AccessNetwork, AccessLevel>> overrideLevels, AccessCompareMode compareMode, AccessActivationMode activationMode, int pulseDurationTicks, boolean overrideDisplay, AxessIconRegistry.AxessIcon overrideIcon, Color overrideColor) {
        this.blockEntityPos = blockEntityPos;
        this.networkUUID = network.getUUID();
        this.accessLevelsUUIDs = new ArrayList<>();
        this.overrideAccessLevelsUUIDs = new ArrayList<>();

        this.compareMode = compareMode;
        this.activationMode = activationMode;
        this.pulseDurationTicks = pulseDurationTicks;

        for (AccessLevel level :
                levels) {
            this.accessLevelsUUIDs.add(level.getUUID());
        }

        for (Pair<AccessNetwork, AccessLevel> pair :
                overrideLevels) {
            this.overrideAccessLevelsUUIDs.add(Pair.of(pair.getFirst().getUUID(), pair.getSecond().getUUID()));
        }

        this.overrideDisplay = overrideDisplay;
        this.overrideIcon = overrideIcon;
        this.overrideColor = overrideColor;
    }

    public CtSModifyKeycardReaderPacket(FriendlyByteBuf buffer) {
        this.blockEntityPos = buffer.readBlockPos();
        this.networkUUID = buffer.readUUID();

        this.accessLevelsUUIDs = new ArrayList<>();
        this.overrideAccessLevelsUUIDs = new ArrayList<>();

        CompoundTag dataTag = buffer.readNbt();

        ListTag list = (ListTag) dataTag.get("Levels");

        for (int i = 0; i < list.size(); i++) {
            UUID uuid = ((CompoundTag)list.get(i)).getUUID("UUID");
            this.accessLevelsUUIDs.add(uuid);
        }

        ListTag olist = (ListTag)dataTag.get("OverrideLevels");

        for (int i = 0; i < olist.size(); i++) {
            UUID netUuid = ((CompoundTag)olist.get(i)).getUUID("NetworkUUID");
            UUID levelUuid = ((CompoundTag)olist.get(i)).getUUID("LevelUUID");
            this.overrideAccessLevelsUUIDs.add(Pair.of(netUuid, levelUuid));
        }

        this.compareMode = buffer.readEnum(AccessCompareMode.class);
        this.activationMode = buffer.readEnum(AccessActivationMode.class);
        this.pulseDurationTicks = buffer.readInt();

        this.overrideDisplay = buffer.readBoolean();
        this.overrideIcon = AxessIconRegistry.getIcon(buffer.readUtf());
        this.overrideColor = new Color(buffer.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockEntityPos);
        buffer.writeUUID(networkUUID);

        ListTag listTag = new ListTag();

        for (UUID uuid :
                accessLevelsUUIDs) {
            CompoundTag newTag = new CompoundTag();
            newTag.putUUID("UUID", uuid);
            listTag.add(newTag);
        }

        ListTag olistTag = new ListTag();

        for (Pair<UUID,UUID> pair :
                overrideAccessLevelsUUIDs) {
            CompoundTag newTag = new CompoundTag();
            newTag.putUUID("NetworkUUID", pair.getFirst());
            newTag.putUUID("LevelUUID", pair.getSecond());
            olistTag.add(newTag);
        }

        CompoundTag dataTag = new CompoundTag();
        dataTag.put("Levels", listTag);
        dataTag.put("OverrideLevels", olistTag);
        buffer.writeNbt(dataTag);

        buffer.writeEnum(compareMode);
        buffer.writeEnum(activationMode);
        buffer.writeInt(pulseDurationTicks);

        buffer.writeBoolean(overrideDisplay);
        buffer.writeUtf(overrideIcon.ID);
        buffer.writeInt(overrideColor.getRGB());
    }

    @Override
    public void handle(IPayloadContext context) {
        if (context.flow().getReceptionSide().isClient()) {
            return;
        }

        context.enqueueWork(() -> {
            try {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player == null) return;

                KeycardReaderBlockEntity reader;
                if (player.containerMenu instanceof KeycardReaderMenu menu) {
                    reader = menu.blockEntity;
                } else if (player.level().getBlockEntity(blockEntityPos) instanceof KeycardReaderBlockEntity blockEntity) {
                    reader = blockEntity;
                } else return;

                AccessNetwork prevNetwork = reader.getAccessNetwork();
                if (prevNetwork != null) if (!prevNetwork.hasPermission(player, AccessPermission.READER_EDIT)) return;

                AccessNetworkDataServer serverNetworkData = AccessNetworkDataServer.get(player.getServer());
                AccessNetwork network = serverNetworkData.getNetwork(networkUUID);
                if (network == null) return;

                ArrayList<AccessLevel> accessLevels = new ArrayList<>();
                for (UUID uuid :
                        accessLevelsUUIDs) {
                    AccessLevel accessLevel = network.getAccessLevel(uuid);
                    if (accessLevel == null) return;
                    accessLevels.add(accessLevel);
                }

                ArrayList<Pair<AccessNetwork, AccessLevel>> overrideAccessLevels = new ArrayList<>();
                if (network.hasPermission(player, AccessPermission.READER_OVERRIDES)) {
                    for (Pair<UUID, UUID> pair :
                            overrideAccessLevelsUUIDs) {
                        AccessNetwork net = serverNetworkData.getNetwork(pair.getFirst());
                        if (net == null) continue;
                        AccessLevel level = net.getAccessLevel(pair.getSecond());
                        if (level == null) continue;
                        overrideAccessLevels.add(Pair.of(net, level));
                    }
                }

                if (compareMode != null)
                    reader.setCompareMode(compareMode);

                if (activationMode != null) {
                    reader.setActivationMode(activationMode);
                }

                if (pulseDurationTicks != 0) {
                    reader.setPulseDurationTicks(pulseDurationTicks);
                }

                reader.setOverrideDisplay(overrideDisplay);
                reader.setOverrideIcon(overrideIcon);
                reader.setOverrideColor(overrideColor);

                reader.setAccessNetwork(network);
                reader.setAccessLevels(accessLevels);

                reader.setOverrideAccessLevels(overrideAccessLevels);

                reader.setChanged();
                reader.deactivate();

                reader.updateOthers();
            } catch (Exception e) {e.printStackTrace();}
        });
    }
}
