package net.teekay.axess.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.teekay.axess.Axess;
import net.teekay.axess.network.packets.client.*;
import net.teekay.axess.network.packets.server.CtSDeleteNetworkPacket;
import net.teekay.axess.network.packets.server.CtSModifyKeycardPacket;
import net.teekay.axess.network.packets.server.CtSModifyKeycardReaderPacket;
import net.teekay.axess.network.packets.server.CtSModifyNetworkPacket;
import java.util.List;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class AxessPacketHandler {

    public static void registerPackets() {
    }

    public static void init() {
        registerPackets();
    }

    public static void register() {
        registerPackets();
    }

    public static <T extends CustomPacketPayload> void sendToServer(T msg) {
        PacketDistributor.sendToServer(msg);
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(T msg, ServerPlayer player) {

        if (NetworkRegistry.hasChannel(player.connection, msg.type().id())) {
            PacketDistributor.sendToPlayer(player, msg);
        }
    }

    public static <T extends CustomPacketPayload> void sendToAllClients(T msg) {
        PacketDistributor.sendToAllPlayers(msg);
    }

}
