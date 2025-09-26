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
import org.checkerframework.checker.units.qual.C;

import java.util.Objects;
import java.util.function.Supplier;

public class StCSyncAllNetworks implements IAxessPacket {
    public AccessNetworkDataServer serverDataModel;

    public StCSyncAllNetworks(AccessNetworkDataServer serverDataModel) {
        this.serverDataModel = serverDataModel;
    }

    public StCSyncAllNetworks(FriendlyByteBuf buffer) {
        this.serverDataModel = AccessNetworkDataServer.load(Objects.requireNonNull(buffer.readNbt()));
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(serverDataModel.save(new CompoundTag()));
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isServer()) {
            context.setPacketHandled(false);
            return;
        }

        AccessNetworkDataClient.loadAllFromServer(serverDataModel);
    }
}
