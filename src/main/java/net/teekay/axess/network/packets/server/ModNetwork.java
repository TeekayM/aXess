package net.teekay.axess.network.packets.server;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.teekay.axess.network.packets.client.*;

public class ModNetwork {

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(net.teekay.axess.Axess.MODID).versioned("1");

        registrar.playBidirectional(
                CtSDeleteNetworkPacket.TYPE,
                CtSDeleteNetworkPacket.STREAM_CODEC,
                CtSDeleteNetworkPacket::handle
        );

        registrar.playBidirectional(
                CtSModifyNetworkPacket.TYPE,
                CtSModifyNetworkPacket.STREAM_CODEC,
                CtSModifyNetworkPacket::handle
        );

        registrar.playBidirectional(
                CtSModifyKeycardPacket.TYPE,
                CtSModifyKeycardPacket.STREAM_CODEC,
                CtSModifyKeycardPacket::handle
        );

        registrar.playBidirectional(
                CtSModifyKeycardReaderPacket.TYPE,
                CtSModifyKeycardReaderPacket.STREAM_CODEC,
                CtSModifyKeycardReaderPacket::handle
        );

        registrar.playBidirectional(
                StCNetworkDeletedPacket.TYPE,
                StCNetworkDeletedPacket.STREAM_CODEC,
                StCNetworkDeletedPacket::handle
        );

        registrar.playBidirectional(
                StCNetworkModifiedPacket.TYPE,
                StCNetworkModifiedPacket.STREAM_CODEC,
                StCNetworkModifiedPacket::handle
        );

        registrar.playBidirectional(
                StCSyncAllNetworks.TYPE,
                StCSyncAllNetworks.STREAM_CODEC,
                StCSyncAllNetworks::handle
        );

        registrar.playBidirectional(
                StCPlayerCacheModifiedPacket.TYPE,
                StCPlayerCacheModifiedPacket.STREAM_CODEC,
                StCPlayerCacheModifiedPacket::handle
        );

        registrar.playBidirectional(
                StCSyncPlayerNameCache.TYPE,
                StCSyncPlayerNameCache.STREAM_CODEC,
                StCSyncPlayerNameCache::handle
        );
    }
}
