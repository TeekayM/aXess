package net.teekay.axess.network.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataServer;
import net.teekay.axess.network.IAxessPacket;

import java.util.Objects;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.HolderLookup;



public class CtSModifyNetworkPacket implements IAxessPacket, CustomPacketPayload {
    public static final CustomPacketPayload.Type<CtSModifyNetworkPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(net.teekay.axess.Axess.MODID, "ct_smodify_network"));

    public static final StreamCodec<FriendlyByteBuf, CtSModifyNetworkPacket> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), CtSModifyNetworkPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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
    public void handle(IPayloadContext context) {
        if (context.flow().getReceptionSide().isClient()) {
            return;
        }

        try {
            AccessNetworkDataServer serverNetworkData = AccessNetworkDataServer.get(Objects.requireNonNull(Objects.requireNonNull(context.player()).getServer()));
            ServerPlayer player = (ServerPlayer) context.player();
            serverNetworkData.playerModifyNetwork(player, network);
        } catch (Exception ignored) {}
    }
}
