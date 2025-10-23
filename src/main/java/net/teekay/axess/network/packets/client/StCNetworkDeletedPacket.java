package net.teekay.axess.network.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.access.AccessNetworkDataServer;
import net.teekay.axess.network.IAxessPacket;

import java.util.UUID;
import java.util.function.Supplier;

public class StCNetworkDeletedPacket implements IAxessPacket {
    public UUID deletedNetwork;

    public StCNetworkDeletedPacket(UUID networkDeleted) {
        this.deletedNetwork = networkDeleted;
    }

    public StCNetworkDeletedPacket(FriendlyByteBuf buffer) {
        this.deletedNetwork = buffer.readUUID();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(deletedNetwork);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isServer()) {
            context.setPacketHandled(false);
            return;
        }

        if (AccessNetworkDataClient.getNetwork(deletedNetwork) == null) {
            context.setPacketHandled(false);
            return;
        }

        AccessNetworkDataClient.removeNetwork(deletedNetwork);
    }
}
