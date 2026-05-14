package net.teekay.axess;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Axess.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AxessConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue MAX_READER_POWERED_TICKS = BUILDER
            .comment("The maximum amount a reader can stay on for with the PULSE option selected.")
            .defineInRange("max_reader_powered_ticks", 200, 1, 20*60);

    private static final ForgeConfigSpec.IntValue MAX_NETWORKS_PER_PLAYER = BUILDER
            .comment("The maximum amount of networks a player can own.")
            .defineInRange("max_networks_per_player", 5, 1, 20);
    private static final ForgeConfigSpec.IntValue MAX_LEVELS_PER_NETWORK = BUILDER
            .comment("The maximum amount of access levels a network can have.")
            .defineInRange("max_levels_per_network", 20, 1, 100);

    private static final ForgeConfigSpec.IntValue OP_MAX_NETWORKS_PER_PLAYER = BUILDER
            .comment("The maximum amount of networks c operators can own.")
            .defineInRange("op_max_networks_per_player", 10, 1, 20);
    private static final ForgeConfigSpec.IntValue OP_MAX_LEVELS_PER_NETWORK = BUILDER
            .comment("The maximum amount of access levels a network can have (for server operators).")
            .defineInRange("op_max_levels_per_network", 40, 1, 100);

    private static final ForgeConfigSpec.IntValue MAX_LINK_DIST = BUILDER
            .comment("The maximum distance there can be between two linked devices.")
            .defineInRange("max_link_distance", 32, 1, 100);

    private static final ForgeConfigSpec.IntValue MAX_LINKS_READER = BUILDER
            .comment("The maximum amount of links a reader can have.")
            .defineInRange("max_links_reader", 5, 0, 20);

    private static final ForgeConfigSpec.BooleanValue EXPERIMENTAL_LET_EVERYONE_EDIT_EVERYTHING = BUILDER
            .comment("[EXPERIMENTAL] ENABLE THIS ONLY IF YOU WANT EVERYONE TO BE ABLE TO EDIT EVERY NETWORK (DANGEROUS)")
            .define("experimental_let_everyone_edit_everything", false);


    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int maxReaderPoweredTicks;
    private static int maxNetworksPerPlayer;
    private static int maxLevelsPerNetwork;
    private static int opMaxNetworksPerPlayer;
    private static int opMaxLevelsPerNetwork;
    public static int maxLinkDistance;
    public static int maxLinksReader;
    public static boolean experimentalLetEveryoneEditEverything;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        maxReaderPoweredTicks = MAX_READER_POWERED_TICKS.get();
        maxNetworksPerPlayer = MAX_NETWORKS_PER_PLAYER.get();
        maxLevelsPerNetwork = MAX_LEVELS_PER_NETWORK.get();
        opMaxNetworksPerPlayer = OP_MAX_NETWORKS_PER_PLAYER.get();
        opMaxLevelsPerNetwork = OP_MAX_LEVELS_PER_NETWORK.get();
        maxLinkDistance = MAX_LINK_DIST.get();
        maxLinksReader = MAX_LINKS_READER.get();
        experimentalLetEveryoneEditEverything = EXPERIMENTAL_LET_EVERYONE_EDIT_EVERYTHING.get();
    }

    static void registerConfig(ModLoadingContext ctx) {
        ctx.registerConfig(ModConfig.Type.SERVER, SPEC);
    }

    public static int getPlayerMaxNetworks(Player player) {
        if (player.hasPermissions(4)) return opMaxNetworksPerPlayer; else return maxNetworksPerPlayer;
    }

    public static int getPlayerMaxLevelsPerNetwork(Player player) {
        if (player.hasPermissions(4)) return opMaxLevelsPerNetwork; else return maxLevelsPerNetwork;
    }
}
