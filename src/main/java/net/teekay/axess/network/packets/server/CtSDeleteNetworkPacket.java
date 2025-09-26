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

public class CtSDeleteNetworkPacket implements IAxessPacket {
    public UUID deletedNetwork;

    public CtSDeleteNetworkPacket(UUID networkDeleted) {
        this.deletedNetwork = networkDeleted;
    }

    public CtSDeleteNetworkPacket(FriendlyByteBuf buffer) {
        this.deletedNetwork = buffer.readUUID();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(deletedNetwork);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.setPacketHandled(false);
            return;
        }

        try {
            AccessNetworkDataServer serverData = AccessNetworkDataServer.get(Objects.requireNonNull(context.getSender()).server);

            context.setPacketHandled(serverData.playerDeleteNetwork(context.getSender(), deletedNetwork));
        } catch (Exception e) {
            context.setPacketHandled(false);
            return;
        }

        context.setPacketHandled(true);
    }
}
