package net.teekay.axess.network.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.teekay.axess.network.IAxessPacket;
import net.teekay.axess.utilities.name_cache.ClientPlayerNameCache;

import java.util.UUID;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.HolderLookup;



public class StCPlayerCacheModifiedPacket implements IAxessPacket, CustomPacketPayload {
    public static final CustomPacketPayload.Type<StCPlayerCacheModifiedPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(net.teekay.axess.Axess.MODID, "st_cplayer_cache_modified"));

    public static final StreamCodec<FriendlyByteBuf, StCPlayerCacheModifiedPacket> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), StCPlayerCacheModifiedPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public UUID uuid;
    public String name;

    public StCPlayerCacheModifiedPacket(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public StCPlayerCacheModifiedPacket(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.name = buffer.readUtf();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeUtf(name, 128);
    }

    @Override
    public void handle(IPayloadContext context) {
        if (context.flow().getReceptionSide().isServer()) {
            return;
        }

        ClientPlayerNameCache.setName(uuid, name);
    }
}
