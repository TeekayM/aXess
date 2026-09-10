package net.teekay.axess.network.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.access.AccessNetworkDataServer;
import net.teekay.axess.network.IAxessPacket;
import org.checkerframework.checker.units.qual.C;

import java.util.Objects;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.HolderLookup;



public class StCSyncAllNetworks implements IAxessPacket, CustomPacketPayload {
    public static final CustomPacketPayload.Type<StCSyncAllNetworks> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(net.teekay.axess.Axess.MODID, "st_csync_all_networks"));

    public static final StreamCodec<FriendlyByteBuf, StCSyncAllNetworks> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), StCSyncAllNetworks::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public AccessNetworkDataServer serverDataModel;

    public StCSyncAllNetworks(AccessNetworkDataServer serverDataModel) {
        this.serverDataModel = serverDataModel;
    }

    public StCSyncAllNetworks(FriendlyByteBuf buffer) {
        this.serverDataModel = AccessNetworkDataServer.load(Objects.requireNonNull(buffer.readNbt()));
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(serverDataModel.toNbt(new CompoundTag()));
    }

    @Override
    public void handle(IPayloadContext context) {
        if (context.flow().getReceptionSide().isServer()) {
            return;
        }

        AccessNetworkDataClient.loadAllFromServer(serverDataModel);
    }
}
