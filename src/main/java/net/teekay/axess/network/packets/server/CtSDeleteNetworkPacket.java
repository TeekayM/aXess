package net.teekay.axess.network.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.teekay.axess.access.AccessNetworkDataServer;
import net.teekay.axess.network.IAxessPacket;

import java.util.Objects;
import java.util.UUID;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.HolderLookup;



public class CtSDeleteNetworkPacket implements IAxessPacket, CustomPacketPayload {
    public static final CustomPacketPayload.Type<CtSDeleteNetworkPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(net.teekay.axess.Axess.MODID, "ct_sdelete_network"));

    public static final StreamCodec<FriendlyByteBuf, CtSDeleteNetworkPacket> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), CtSDeleteNetworkPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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
    public void handle(IPayloadContext context) {
        if (context.flow().getReceptionSide().isClient()) {
            return;
        }

        try {
            AccessNetworkDataServer serverData = AccessNetworkDataServer.get(Objects.requireNonNull(context.player().getServer()));
            serverData.playerDeleteNetwork((ServerPlayer) context.player(), deletedNetwork);
        } catch (Exception ignored) {}

    }
}
