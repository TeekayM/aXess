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

import java.util.Objects;
import java.util.function.Supplier;

public class StCNetworkModifiedPacket implements IAxessPacket {
    public AccessNetwork network;

    public StCNetworkModifiedPacket(AccessNetwork network) {
        this.network = network;
    }

    public StCNetworkModifiedPacket(FriendlyByteBuf buffer) {
        this.network = AccessNetwork.fromNBT(Objects.requireNonNull(buffer.readNbt()));
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(network.toNBT());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();


        if (context.getDirection().getReceptionSide().isServer()) {
            context.setPacketHandled(false);
            return;
        }

        AccessNetworkDataClient.setNetwork(network);
    }
}
