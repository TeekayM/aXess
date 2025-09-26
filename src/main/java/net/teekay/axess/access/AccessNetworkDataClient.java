package net.teekay.axess.access;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.teekay.axess.utilities.AccessUtils;

import java.util.*;

public class AccessNetworkDataClient {

    private static final HashMap<UUID, AccessNetwork> networkRegistry = new HashMap<>();

    public static HashMap<UUID, AccessNetwork> getNetworkRegistry() {
        return networkRegistry;
    }

    public static List<AccessNetwork> getNetworks() {
        if (Minecraft.getInstance().player != null) {
            return networkRegistry.values().stream().filter( (network) -> AccessUtils.canPlayerEditNetwork(Minecraft.getInstance().player, network)).toList();
        }
        return networkRegistry.values().stream().toList();
    }

    public static void loadAllFromServer(AccessNetworkDataServer serverData) {
        networkRegistry.clear();
        networkRegistry.putAll(serverData.getNetworkRegistry());
    }

    public static AccessNetwork getNetwork(UUID uuid) {
        return networkRegistry.get(uuid);
    }

    public static void setNetwork(AccessNetwork network) {
        networkRegistry.put(network.getUUID(), network);
    }

    public static void removeNetwork(UUID uuid) {
        networkRegistry.remove(uuid);
    }
}
