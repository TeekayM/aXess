package net.teekay.axess.access;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.teekay.axess.AxessConfig;
import net.teekay.axess.network.AxessPacketHandler;
import net.teekay.axess.network.packets.client.StCNetworkDeletedPacket;
import net.teekay.axess.network.packets.client.StCNetworkModifiedPacket;
import net.teekay.axess.utilities.AxessUtilities;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.HolderLookup;

public class AccessNetworkDataServer extends SavedData {

    private final HashMap<UUID, AccessNetwork> networkRegistry = new HashMap<>();

    public static AccessNetworkDataServer get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(AccessNetworkDataServer::new, (tag, registries) -> load(tag)), "axess_networks"
        );
    }

    public AccessNetworkDataServer() {}

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        for (HashMap.Entry<UUID, AccessNetwork> networkEntry :
                networkRegistry.entrySet()) {

            tag.put(networkEntry.getKey().toString(), networkEntry.getValue().toNBT());
        }

        return tag;
    }

    public CompoundTag toNbt(CompoundTag tag) {
        for (HashMap.Entry<UUID, AccessNetwork> networkEntry :
                networkRegistry.entrySet()) {

            tag.put(networkEntry.getKey().toString(), networkEntry.getValue().toNBT());
        }

        return tag;
    }


    public static AccessNetworkDataServer load(CompoundTag tag) {
        AccessNetworkDataServer data = new AccessNetworkDataServer();

        for (String key : tag.getAllKeys()) {
            data.networkRegistry.put(UUID.fromString(key), AccessNetwork.fromNBT(tag.getCompound(key)));
        }

        return data;
    }

    public HashMap<UUID, AccessNetwork> getNetworkRegistry() {
        return networkRegistry;
    }

    public List<AccessNetwork> getNetworks() { return networkRegistry.values().stream().toList(); }

    @Nullable
    public AccessNetwork getNetwork(UUID uuid) {
        return networkRegistry.get(uuid);
    }

    public void setNetwork(AccessNetwork network) {
        networkRegistry.put(network.getUUID(), network);
        AxessPacketHandler.sendToAllClients(new StCNetworkModifiedPacket(network));
    }

    public void removeNetwork(UUID uuid) {
        networkRegistry.remove(uuid);
        AxessPacketHandler.sendToAllClients(new StCNetworkDeletedPacket(uuid));
    }

    public boolean validateNetwork(AccessNetwork network, ServerPlayer player) {
        // check level count
        if (network.getAccessLevels().size() > AxessConfig.getPlayerMaxLevelsPerNetwork(player)) return false;

        return true;
    }

    public boolean playerModifyNetwork(ServerPlayer player, AccessNetwork network) {
        AccessNetwork networkToChange = getNetwork(network.getUUID());

        if (!validateNetwork(network, player)) return false;


        if (networkToChange != null) {  // NETWORK EXISTS
            if (networkToChange.hasPermission(player, AccessPermission.ADMIN)) {
                setNetwork(network);
                return true;
            }

            boolean can_add = true;

            // ACCESS LEVELS
            if (AxessUtilities.getDiff(
                    networkToChange.getAccessLevels(),
                    network.getAccessLevels(),
                    AccessLevel::strictEquals
            )) {
                can_add = networkToChange.hasPermission(player, AccessPermission.AL_EDIT);
            }

            network.setAllPermissions(networkToChange.getPermissions());

            if (can_add) setNetwork(network);

            return can_add;

        } else {  // NETWORK BEING CREATED
            int networksCreatedByPlayer = networkRegistry.values().stream().filter( (net) -> net.isOwnedBy(player) ).toList().size();
            if (networksCreatedByPlayer >= AxessConfig.getPlayerMaxNetworks(player)) return false;

            if (!network.isOwnedBy(player)) return false;

            setNetwork(network);
            return true;
        }
    }

    public boolean playerDeleteNetwork(ServerPlayer player, UUID network, boolean force) {
        AccessNetwork networkToDelete = getNetwork(network);

        if (!networkToDelete.isOwnedBy(player) && !force) return false;

        removeNetwork(network);
        return true;
    }

    public boolean playerDeleteNetwork(ServerPlayer player, UUID network) {
        return playerDeleteNetwork(player, network, false);
    }

}
