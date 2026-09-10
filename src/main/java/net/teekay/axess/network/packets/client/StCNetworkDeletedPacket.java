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

import java.util.UUID;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.HolderLookup;



public class StCNetworkDeletedPacket implements IAxessPacket, CustomPacketPayload {
    public static final CustomPacketPayload.Type<StCNetworkDeletedPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(net.teekay.axess.Axess.MODID, "st_cnetwork_deleted"));

    public static final StreamCodec<FriendlyByteBuf, StCNetworkDeletedPacket> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), StCNetworkDeletedPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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
    public void handle(IPayloadContext context) {
        if (context.flow().getReceptionSide().isServer()) {
            return;
        }

        if (AccessNetworkDataClient.getNetwork(deletedNetwork) == null) {
            return;
        }

        AccessNetworkDataClient.removeNetwork(deletedNetwork);
    }
}
