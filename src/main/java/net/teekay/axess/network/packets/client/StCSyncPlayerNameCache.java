package net.teekay.axess.network.packets.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.teekay.axess.network.IAxessPacket;
import net.teekay.axess.utilities.name_cache.ClientPlayerNameCache;
import net.teekay.axess.utilities.name_cache.ServerPlayerNameCache;

import java.util.Objects;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.HolderLookup;



public class StCSyncPlayerNameCache implements IAxessPacket, CustomPacketPayload {
    public static final CustomPacketPayload.Type<StCSyncPlayerNameCache> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(net.teekay.axess.Axess.MODID, "st_csync_player_name_cache"));

    public static final StreamCodec<FriendlyByteBuf, StCSyncPlayerNameCache> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), StCSyncPlayerNameCache::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public ServerPlayerNameCache serverDataModel;

    public StCSyncPlayerNameCache(ServerPlayerNameCache serverDataModel) {
        this.serverDataModel = serverDataModel;
    }

    public StCSyncPlayerNameCache(FriendlyByteBuf buffer) {
        this.serverDataModel = ServerPlayerNameCache.load(Objects.requireNonNull(buffer.readNbt()));
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

        ClientPlayerNameCache.loadAllFromServer(serverDataModel);
    }
}
