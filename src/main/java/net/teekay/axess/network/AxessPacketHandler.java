package net.teekay.axess.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.teekay.axess.Axess;
import net.teekay.axess.network.packets.client.StCNetworkDeletedPacket;
import net.teekay.axess.network.packets.client.StCNetworkModifiedPacket;
import net.teekay.axess.network.packets.client.StCSyncAllNetworks;
import net.teekay.axess.network.packets.server.CtSDeleteNetworkPacket;
import net.teekay.axess.network.packets.server.CtSModifyKeycardPacket;
import net.teekay.axess.network.packets.server.CtSModifyNetworkPacket;

import java.util.List;

public class AxessPacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(Axess.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int lastID = 0;

    private static int id() {
        lastID ++;
        return lastID;
    }

    public static void register() {

        // CLIENT TO SERVER
        INSTANCE.registerMessage(id(),
                CtSDeleteNetworkPacket.class,
                CtSDeleteNetworkPacket::encode,
                CtSDeleteNetworkPacket::new,
                CtSDeleteNetworkPacket::handle);
        INSTANCE.registerMessage(id(),
                CtSModifyNetworkPacket.class,
                CtSModifyNetworkPacket::encode,
                CtSModifyNetworkPacket::new,
                CtSModifyNetworkPacket::handle);
        INSTANCE.registerMessage(id(),
                CtSModifyKeycardPacket.class,
                CtSModifyKeycardPacket::encode,
                CtSModifyKeycardPacket::new,
                CtSModifyKeycardPacket::handle);

        // SERVER TO CLIENT
        INSTANCE.registerMessage(id(),
                StCNetworkDeletedPacket.class,
                StCNetworkDeletedPacket::encode,
                StCNetworkDeletedPacket::new,
                StCNetworkDeletedPacket::handle);
        INSTANCE.registerMessage(id(),
                StCNetworkModifiedPacket.class,
                StCNetworkModifiedPacket::encode,
                StCNetworkModifiedPacket::new,
                StCNetworkModifiedPacket::handle);
        INSTANCE.registerMessage(id(),
                StCSyncAllNetworks.class,
                StCSyncAllNetworks::encode,
                StCSyncAllNetworks::new,
                StCSyncAllNetworks::handle);

    }

    public static void sendToServer(Object msg) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
    }

    public static void sendToPlayer(Object msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static void sendToAllClients(Object msg) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }
}
