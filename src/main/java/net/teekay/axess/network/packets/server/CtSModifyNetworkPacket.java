package net.teekay.axess.network.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.access.AccessNetworkDataServer;
import net.teekay.axess.network.IAxessPacket;
import net.teekay.axess.utilities.AccessUtils;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class CtSModifyNetworkPacket implements IAxessPacket {
    public AccessNetwork network;

    public CtSModifyNetworkPacket(AccessNetwork network) {
        this.network = network;
    }

    public CtSModifyNetworkPacket(FriendlyByteBuf buffer) {
        this.network = AccessNetwork.fromNBT(Objects.requireNonNull(buffer.readNbt()));
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(network.toNBT());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.setPacketHandled(false);
            return;
        }

        try {
            AccessNetworkDataServer serverNetworkData = AccessNetworkDataServer.get(Objects.requireNonNull(context.getSender()).server);
            ServerPlayer player = context.getSender();

            context.setPacketHandled(serverNetworkData.playerModifyNetwork(player, network));
        } catch (Exception e) {
            context.setPacketHandled(false);
            return;
        }
    }
}
