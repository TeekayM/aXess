package net.teekay.axess.access;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.teekay.axess.Axess;
import net.teekay.axess.AxessConfig;
import net.teekay.axess.network.AxessPacketHandler;
import net.teekay.axess.network.packets.client.StCNetworkDeletedPacket;
import net.teekay.axess.network.packets.client.StCNetworkModifiedPacket;
import net.teekay.axess.screen.component.NetworkEntry;
import net.teekay.axess.utilities.AccessUtils;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.Nullable;
import org.joml.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Axess.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AccessNetworkDataServer extends SavedData {

    private final HashMap<UUID, AccessNetwork> networkRegistry = new HashMap<>();

    public static AccessNetworkDataServer get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                AccessNetworkDataServer::load, AccessNetworkDataServer::new, "axess_networks"
        );
    }

    public AccessNetworkDataServer() {}

    @Override
    public CompoundTag save(CompoundTag tag) {
        System.out.println("[!] Saving access networks...");

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
            System.out.println("Sloaded " + key);
        }

        return data;
    }

    public HashMap<UUID, AccessNetwork> getNetworkRegistry() {
        return networkRegistry;
    }

    public ArrayList<AccessNetwork> getNetworks() { return (ArrayList<AccessNetwork>) networkRegistry.values().stream().toList(); }

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

    public boolean canPlayerCreateNetwork(ServerPlayer player, AccessNetwork network) {
        // get count
        int networksCreatedByPlayer = networkRegistry.values().stream().filter( (net) -> net.isOwnedBy(player) ).toList().size();

        return (networksCreatedByPlayer < AxessConfig.maxNetworksPerPlayer) && AccessUtils.canPlayerEditNetwork(player, network);
    }

    public boolean validateNetwork(AccessNetwork network) {
        // check level count
        if (network.getAccessLevels().size() > AxessConfig.maxLevelsPerNetwork) return false;

        return true;
    }

    public boolean playerModifyNetwork(ServerPlayer player, AccessNetwork network) {
        AccessNetwork networkToChange = getNetwork(network.getUUID());

        if (!validateNetwork(network)) return false;

        if (networkToChange == null && canPlayerCreateNetwork(player, network)) { // NETWORK BEING CREATED
            setNetwork(network);
            return true;
        } else if (networkToChange != null && AccessUtils.canPlayerEditNetwork(player, networkToChange) && AccessUtils.canPlayerEditNetwork(player, network)) { // NETWORK EDITED
            setNetwork(network);
            return true;
        }

        return false;
    }

    public boolean playerDeleteNetwork(ServerPlayer player, UUID network) {
        AccessNetwork networkToDelete = getNetwork(network);

        if (!AccessUtils.canPlayerEditNetwork(player, networkToDelete)) {
            System.out.println(networkToDelete.getOwnerUUID() + " is not equal to " + player.getUUID());
            return false;
        }

        removeNetwork(network);

        return true;
    }

}
