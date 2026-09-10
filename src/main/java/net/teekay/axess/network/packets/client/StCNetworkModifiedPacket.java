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

import java.util.Objects;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.HolderLookup;



public class StCNetworkModifiedPacket implements IAxessPacket, CustomPacketPayload {
    public static final CustomPacketPayload.Type<StCNetworkModifiedPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(net.teekay.axess.Axess.MODID, "st_cnetwork_modified"));

    public static final StreamCodec<FriendlyByteBuf, StCNetworkModifiedPacket> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), StCNetworkModifiedPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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
    public void handle(IPayloadContext context) {
        if (context.flow().getReceptionSide().isServer()) {
            return;
        }

        AccessNetworkDataClient.setNetwork(network);
    }
}
