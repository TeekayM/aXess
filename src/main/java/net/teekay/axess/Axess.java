package net.teekay.axess;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.teekay.axess.client.AxessClientHandler;
import net.teekay.axess.client.render.AxessRendererHandler;
import net.teekay.axess.datagen.AxessDataGenerators;
import net.teekay.axess.network.AxessPacketHandler;
import net.teekay.axess.registry.*;
import org.slf4j.Logger;
import org.slf4j.MDC;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.fml.ModContainer;
import net.teekay.axess.network.packets.server.ModNetwork;
import net.teekay.axess.block.keycardeditor.KeycardEditorBlockEntity;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Axess.MODID)
public class Axess {

    public static final String MODID = "axess";

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AXESS_CREATIVE_TAB = CREATIVE_MODE_TABS.register("axess", () -> CreativeModeTab.builder().displayItems((parameters, output) -> {
        for (DeferredHolder<Item, ? extends Item> item : AxessItemRegistry.getEntries()) {
            output.accept(item.get());
        }
        for (DeferredHolder<Block, ? extends Block> block : AxessBlockRegistry.getEntries()) {
            try {
                output.accept(block.get().asItem());
            } catch (Exception e) {
            }
        }
    }).title(Component.translatable("gui." + MODID + ".creative_tab")).icon(() -> new ItemStack(AxessItemRegistry.KEYCARD.get(), 1)).build());

    public Axess(IEventBus modEventBus, ModContainer modContainer) {
        AxessBlockRegistry.register(modEventBus);
        AxessItemRegistry.register(modEventBus);
        AxessBlockEntityRegistry.register(modEventBus);
        modEventBus.addListener(KeycardEditorBlockEntity::registerCapabilities);
        AxessMenuRegistry.register(modEventBus);
        AxessSoundRegistry.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(AxessDataGenerators::gatherData);
        if (FMLLoader.getDist() == Dist.CLIENT) {
            modEventBus.addListener(AxessRendererHandler::onModelBake);
            modEventBus.addListener(AxessClientHandler::registerItemColors);
            modEventBus.addListener(AxessClientHandler::registerRenderers);
            modEventBus.addListener(AxessClientHandler::registerMenus);
        }
        modEventBus.addListener(AxessConfig::onLoad);
        modEventBus.addListener(ModNetwork::register);
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::commonSetup);
        modContainer.registerConfig(ModConfig.Type.COMMON, AxessConfig.SPEC);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[aXess] I'm alive!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        AxessPacketHandler.register();
    }
}
