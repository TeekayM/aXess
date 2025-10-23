package net.teekay.axess.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessNetworkDataServer;
import net.teekay.axess.network.AxessPacketHandler;
import net.teekay.axess.network.packets.client.StCSyncAllNetworks;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Axess.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AxessServerEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide) {
            Player player = event.getEntity();

            AccessNetworkDataServer data = AccessNetworkDataServer.get(Objects.requireNonNull(player.getServer()));
            CompoundTag tag = new CompoundTag();
            data.save(tag);

            AxessPacketHandler.sendToPlayer(new StCSyncAllNetworks(data), (ServerPlayer) player);
        }
    }

    @SubscribeEvent
    public static void onWorldSave(LevelEvent.Save event) {
        if (event.getLevel().isClientSide()) return;

        AccessNetworkDataServer data = AccessNetworkDataServer.get(event.getLevel().getServer());
        data.setDirty();
    }

}
