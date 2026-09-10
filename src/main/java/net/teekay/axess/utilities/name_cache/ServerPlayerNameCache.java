package net.teekay.axess.utilities.name_cache;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.teekay.axess.Axess;
import net.teekay.axess.network.AxessPacketHandler;
import net.teekay.axess.network.packets.client.StCPlayerCacheModifiedPacket;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import net.minecraft.core.HolderLookup;

public class ServerPlayerNameCache extends SavedData {

    private final HashMap<UUID, String> nameRegistry = new HashMap<>();

    public static ServerPlayerNameCache get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(ServerPlayerNameCache::new,  (tag, registries) -> load(tag)), "axess_name_cache"
        );
    }

    public ServerPlayerNameCache() {}

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        for (HashMap.Entry<UUID, String> nameEntry :
                nameRegistry.entrySet()) {

            tag.putString(nameEntry.getKey().toString(), nameEntry.getValue());
        }

        return tag;
    }

    public CompoundTag toNbt(CompoundTag tag) {
        for (HashMap.Entry<UUID, String> nameEntry :
                nameRegistry.entrySet()) {

            tag.putString(nameEntry.getKey().toString(), nameEntry.getValue());
        }

        return tag;
    }

    public static ServerPlayerNameCache load(CompoundTag tag) {
        ServerPlayerNameCache data = new ServerPlayerNameCache();

        for (String key : tag.getAllKeys()) {
            data.nameRegistry.put(UUID.fromString(key), tag.getString(key));
        }

        return data;
    }

    public HashMap<UUID, String> getNameRegistry() {
        return nameRegistry;
    }

    @Nullable
    public String getName(UUID uuid) {
        return nameRegistry.get(uuid);
    }

    public void setName(UUID uuid, String name) {
        nameRegistry.put(uuid, name);
        AxessPacketHandler.sendToAllClients(new StCPlayerCacheModifiedPacket(uuid, name));
    }

    public void removeName(UUID uuid) {
        nameRegistry.remove(uuid);
    }

}
