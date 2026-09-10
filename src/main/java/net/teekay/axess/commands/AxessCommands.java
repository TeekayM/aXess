package net.teekay.axess.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sun.jdi.connect.Connector;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextColor;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataServer;
import net.teekay.axess.utilities.AxessUtilities;
import net.teekay.axess.utilities.name_cache.ServerPlayerNameCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class AxessCommands {

    private static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final int NETWORKS_PER_PAGE = 5;

    private static final Component ERROR_DUMP = Component.translatable("commands.axess.error.dump");
    private static final Component ERROR_NETWORK_NOT_FOUND = Component.translatable("commands.axess.error.network_not_found");
    private static final Component ERROR_INVALID_PAGE = Component.translatable("commands.axess.error.invalid_page");
    private static final Component ERROR_NO_NETWORKS = Component.translatable("commands.axess.error.no_networks");

    private static final String VERSION_LANGKEY =  "commands.axess.version";
    private static final Component DUMP_SUCCESS = Component.translatable("commands.axess.dump_success");
    private static final Component NETWORK_LIST_CREATED_BY = Component.translatable("commands.axess.created_by");
    private static final String NETWORK_PAGES_LANGKEY = "commands.axess.network_pages";
    private static final String SUCCESSFUL_DELETE_LANGKEY = "commands.axess.success_delete";
    private static final String VIEW_LANGKEY = "commands.axess.view_network";

    private static final Component MOD_PREFIX =
            Component.literal("[").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal("aXess").withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal("]").withStyle(ChatFormatting.GOLD)).append(Component.literal(" ").withStyle(ChatFormatting.RESET));

    private static Component applyPrefix(Component component) {
        return Component.empty().append(MOD_PREFIX).append(component.copy());
    }

    public static void dumpNetwork(Path path, AccessNetwork net) throws IOException {
        Path file = path.resolve(net.getUUID().toString() + ".json");
        String prettyNet = GSON.toJson(AxessUtilities.nbtToJson(net.toNBT()));
        Files.writeString(file, prettyNet);
    }

    public static AccessNetworkDataServer getServerNetworkData(CommandContext<CommandSourceStack> context) {
        return AccessNetworkDataServer.get(context.getSource().getServer());
    }

    public static Component listNetwork(CommandContext<CommandSourceStack> context, AccessNetwork net) {
        return Component.empty()
                .append(Component.literal("[\uD83D\uDDD1]").withStyle(style ->
                    style.withClickEvent(new ClickEvent(
                            ClickEvent.Action.SUGGEST_COMMAND,
                            "/axess network delete " + net.getUUID().toString()
                    ))
                            .withColor(TextColor.fromLegacyFormat(ChatFormatting.RED))
                ))

                .append(Component.literal(" "))

                .append(Component.literal("[\uD83D\uDC41]").withStyle(style ->
                        style.withClickEvent(new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/axess network view " + net.getUUID().toString()
                        ))
                                .withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA))
                ))

                .append(Component.literal(" "))

                .append(Component.literal(net.getName())
                        .withStyle(style -> style.withHoverEvent(
                                new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        Component.literal(net.getUUID().toString())
                                )
                            )
                                .withUnderlined(true)
                        )
                )


                .append(Component.literal(" ").withStyle(style -> style.withHoverEvent(null)))

                .append(NETWORK_LIST_CREATED_BY)

                .append(Component.literal(" "))

                .append(Component.literal(ServerPlayerNameCache.get(context.getSource().getServer()).getName(net.getOwnerUUID()))
                        .withStyle(style -> style.withHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.literal(net.getOwnerUUID().toString())
                        )).withUnderlined(true)
                ));
    }

    public static int listNetworks(CommandContext<CommandSourceStack> context, int page) {
        var networks = getServerNetworkData(context).getNetworks();
        var networks_amount = networks.size();

        var max_page = networks_amount/NETWORKS_PER_PAGE;
        if (networks_amount%NETWORKS_PER_PAGE != 0) max_page++;

        if (page > max_page || page <= 0) {
            if (page == 1) {
                context.getSource().sendFailure(
                        applyPrefix(ERROR_NO_NETWORKS)
                );
            } else {
                context.getSource().sendFailure(
                        applyPrefix(ERROR_INVALID_PAGE)
                );
            }
            return 1;
        }

        int finalMax_page = max_page;
        context.getSource().sendSuccess(
                () -> applyPrefix(Component.translatable(NETWORK_PAGES_LANGKEY, page, finalMax_page)),
                true
        );
        for (int i = (page-1)*NETWORKS_PER_PAGE; i < page*NETWORKS_PER_PAGE; i++) {
            if (i >= networks.size()) break;
            int finalI = i;
            context.getSource().sendSuccess(
                    () -> Component.literal(finalI+1 + " - ").append(listNetwork(context, networks.get(finalI))),
                    true
            );
        }

        return 1;
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("axess")

                        .requires(source -> source.hasPermission(2))

                        .then(Commands.literal("version")
                                .executes(context -> {
                                    String version = ModList.get()
                                            .getModContainerById(Axess.MODID)
                                            .orElseThrow()
                                            .getModInfo()
                                            .getVersion()
                                            .toString();

                                    context.getSource().sendSuccess(
                                            () -> applyPrefix(Component.translatable(VERSION_LANGKEY, version)),
                                            false
                                    );

                                    return 1;
                                })
                        )

                        .then(Commands.literal("dump")
                                .executes(context -> {
                                    Path path = context.getSource().getServer().getServerDirectory().toAbsolutePath();

                                    try {
                                        Path dir = Files.createDirectories(path.resolve("axess_dump"));
                                        for (AccessNetwork net : getServerNetworkData(context).getNetworks()) {
                                                try {
                                                    dumpNetwork(dir, net);
                                                } catch (IOException ignored) {
                                                }
                                        }
                                        context.getSource().sendSuccess(
                                                () -> applyPrefix(DUMP_SUCCESS),
                                                true
                                        );
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        context.getSource().sendFailure(
                                                applyPrefix(ERROR_DUMP)
                                        );
                                    }
                                    return 1;
                                })
                        )

                        .then(Commands.literal("network")
                                .then(Commands.literal("delete")
                                        .then(Commands.argument("networkID", StringArgumentType.string())
                                                .suggests((context, builder) -> {
                                                    for (var net : getServerNetworkData(context).getNetworks()) {
                                                        builder.suggest(net.getUUID().toString());
                                                    }

                                                    return builder.buildFuture();
                                                })
                                                .executes(context -> {
                                                    try {
                                                        var uuid = UUID.fromString(StringArgumentType.getString(context, "networkID"));

                                                        if (!getServerNetworkData(context).getNetworkRegistry().containsKey(uuid)) {
                                                            context.getSource().sendFailure(
                                                                    applyPrefix(ERROR_NETWORK_NOT_FOUND)
                                                            );

                                                            return 1;
                                                        }

                                                        var player = context.getSource().getPlayer();
                                                        if (player == null) return 0;

                                                        getServerNetworkData(context).playerDeleteNetwork(player, uuid, true);

                                                        context.getSource().sendSuccess(
                                                                        () -> applyPrefix(Component.translatable(SUCCESSFUL_DELETE_LANGKEY, uuid.toString())),
                                                                true
                                                        );

                                                        return 1;
                                                    } catch (Exception e) {
                                                        context.getSource().sendFailure(
                                                                ERROR_NETWORK_NOT_FOUND
                                                        );

                                                        return 1;
                                                    }
                                                })
                                        )
                                )
                                .then(Commands.literal("view")
                                        .then(Commands.argument("networkID", StringArgumentType.string())
                                                .suggests((context, builder) -> {
                                                    for (var net : getServerNetworkData(context).getNetworks()) {
                                                        builder.suggest(net.getUUID().toString());
                                                    }

                                                    return builder.buildFuture();
                                                })
                                                .executes(context -> {
                                                    try {
                                                        var uuid = UUID.fromString(StringArgumentType.getString(context, "networkID"));

                                                        if (!getServerNetworkData(context).getNetworkRegistry().containsKey(uuid)) {
                                                            context.getSource().sendFailure(
                                                                    applyPrefix(ERROR_NETWORK_NOT_FOUND)
                                                            );

                                                            return 1;
                                                        }

                                                        var player = context.getSource().getPlayer();
                                                        if (player == null) return 0;

                                                        context.getSource().sendSuccess(
                                                                () -> applyPrefix(Component.translatable(VIEW_LANGKEY, uuid.toString())),
                                                                true
                                                        );

                                                        context.getSource().sendSuccess(
                                                                () -> NbtUtils.toPrettyComponent(getServerNetworkData(context).getNetwork(uuid).toNBT()),
                                                                true
                                                        );

                                                        return 1;
                                                    } catch (Exception e) {
                                                        context.getSource().sendFailure(
                                                                applyPrefix(ERROR_NETWORK_NOT_FOUND)
                                                        );

                                                        return 1;
                                                    }
                                                })
                                        )
                                )
                                .then(Commands.literal("list")
                                        .then(Commands.argument("page", IntegerArgumentType.integer())
                                                .suggests((context, builder) -> {
                                                    int networks = getServerNetworkData(context).getNetworks().size();
                                                    int pages = networks / NETWORKS_PER_PAGE;
                                                    if (networks % NETWORKS_PER_PAGE != 0) {
                                                        pages++;
                                                    }

                                                    for (int i = 1; i <= pages; i++) builder.suggest(i);

                                                    return builder.buildFuture();
                                                })
                                                .executes(context -> {
                                                    return listNetworks(context, IntegerArgumentType.getInteger(context, "page"));
                                                })
                                        )
                                        .executes(context -> {
                                            return listNetworks(context, 1);
                                        })
                                )
                        )
        );


    }

}
