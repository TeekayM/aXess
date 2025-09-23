package net.teekay.axess.access;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.teekay.axess.Axess;
import net.teekay.axess.network.AxessPacketHandler;
import net.teekay.axess.network.packets.client.StCNetworkDeletedPacket;
import net.teekay.axess.network.packets.client.StCNetworkModifiedPacket;
import net.teekay.axess.screen.component.NetworkEntry;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.Nullable;
import org.joml.Random;

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


}
