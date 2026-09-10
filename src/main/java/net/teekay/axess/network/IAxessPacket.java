package net.teekay.axess.network;

import net.minecraft.network.FriendlyByteBuf;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.core.HolderLookup;


public interface IAxessPacket {
    void encode(FriendlyByteBuf buffer);
    void handle(IPayloadContext contextSupplier);
}
