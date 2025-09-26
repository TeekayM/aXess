package net.teekay.axess;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = Axess.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AxessConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue MAX_NETWORKS_PER_PLAYER = BUILDER
            .comment("The maximum amount of networks a player can own.")
            .defineInRange("max_networks_per_player", 5, 1, 20);
    private static final ForgeConfigSpec.IntValue MAX_LEVELS_PER_NETWORK = BUILDER
            .comment("The maximum amount of access levels a network can have.")
            .defineInRange("max_levels_per_network", 20, 1, 100);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int maxNetworksPerPlayer;
    public static int maxLevelsPerNetwork;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        maxNetworksPerPlayer = MAX_NETWORKS_PER_PLAYER.get();
        maxLevelsPerNetwork = MAX_LEVELS_PER_NETWORK.get();
    }

    static void registerConfig(ModLoadingContext ctx) {
        ctx.registerConfig(ModConfig.Type.SERVER, SPEC);
    }
}
