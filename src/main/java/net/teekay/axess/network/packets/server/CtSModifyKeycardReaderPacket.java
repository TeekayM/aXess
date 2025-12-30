package net.teekay.axess.network.packets.server;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.teekay.axess.access.*;
import net.teekay.axess.block.readers.KeycardReaderBlockEntity;
import net.teekay.axess.network.IAxessPacket;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.screen.KeycardReaderMenu;
import net.teekay.axess.utilities.AccessUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

public class CtSModifyKeycardReaderPacket implements IAxessPacket {
    public BlockPos blockEntityPos;
    public UUID networkUUID;
    public ArrayList<UUID> accessLevelsUUIDs;
    public AccessCompareMode compareMode;
    public AccessActivationMode activationMode;
    public int pulseDurationTicks;
    public boolean overrideDisplay;
    public AxessIconRegistry.AxessIcon overrideIcon;
    public Color overrideColor;

    public CtSModifyKeycardReaderPacket(BlockPos blockEntityPos, AccessNetwork network, ArrayList<AccessLevel> levels, AccessCompareMode compareMode, AccessActivationMode activationMode, int pulseDurationTicks, boolean overrideDisplay, AxessIconRegistry.AxessIcon overrideIcon, Color overrideColor) {
        this.blockEntityPos = blockEntityPos;
        this.networkUUID = network.getUUID();
        this.accessLevelsUUIDs = new ArrayList<>();

        this.compareMode = compareMode;
        this.activationMode = activationMode;
        this.pulseDurationTicks = pulseDurationTicks;

        for (AccessLevel level :
                levels) {
            this.accessLevelsUUIDs.add(level.getUUID());
        }

        this.overrideDisplay = overrideDisplay;
        this.overrideIcon = overrideIcon;
        this.overrideColor = overrideColor;
    }

    public CtSModifyKeycardReaderPacket(FriendlyByteBuf buffer) {
        this.blockEntityPos = buffer.readBlockPos();
        this.networkUUID = buffer.readUUID();

        this.accessLevelsUUIDs = new ArrayList<>();


        ListTag list = (ListTag) buffer.readNbt().get("Data");

        for (int i = 0; i < list.size(); i++) {
            UUID uuid = ((CompoundTag)list.get(i)).getUUID("UUID");
            this.accessLevelsUUIDs.add(uuid);
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

        CompoundTag dataTag = new CompoundTag();
        dataTag.put("Data", listTag);
        buffer.writeNbt(dataTag);

        buffer.writeEnum(compareMode);
        buffer.writeEnum(activationMode);
        buffer.writeInt(pulseDurationTicks);

        buffer.writeBoolean(overrideDisplay);
        buffer.writeUtf(overrideIcon.ID);
        buffer.writeInt(overrideColor.getRGB());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.setPacketHandled(false);
            return;
        }

        context.enqueueWork(() -> {
            try {
                ServerPlayer player = context.getSender();
                if (player == null) return;

                KeycardReaderBlockEntity keycardEditor;
                if (player.containerMenu instanceof KeycardReaderMenu menu) {
                    keycardEditor = menu.blockEntity;
                } else if (player.level().getBlockEntity(blockEntityPos) instanceof KeycardReaderBlockEntity blockEntity) {
                    keycardEditor = blockEntity;
                } else return;

                AccessNetwork prevNetwork = keycardEditor.getAccessNetwork();
                if (prevNetwork != null) if (!AccessUtils.canPlayerEditNetwork(player, prevNetwork)) return;

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

                if (compareMode != null)
                    keycardEditor.setCompareMode(compareMode);

                if (activationMode != null) {
                    keycardEditor.setActivationMode(activationMode);
                    keycardEditor.execOnReaderPair(p -> p.setActivationMode(activationMode));
                }

                if (pulseDurationTicks != 0) {
                    keycardEditor.setPulseDurationTicks(pulseDurationTicks);
                    keycardEditor.execOnReaderPair(p -> p.setPulseDurationTicks(pulseDurationTicks));
                }

                keycardEditor.setOverrideDisplay(overrideDisplay);
                keycardEditor.setOverrideIcon(overrideIcon);
                keycardEditor.setOverrideColor(overrideColor);

                keycardEditor.setAccessNetwork(network);
                keycardEditor.setAccessLevels(accessLevels);

                keycardEditor.setChanged();
                keycardEditor.execOnReaderPair(KeycardReaderBlockEntity::setChanged);
                keycardEditor.deactivate();
                keycardEditor.execOnReaderPair(KeycardReaderBlockEntity::deactivate);
            } catch (Exception e) {e.printStackTrace();}
        });
    }
}
