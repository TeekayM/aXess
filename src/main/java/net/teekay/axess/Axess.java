package net.teekay.axess;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.teekay.axess.registry.*;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Axess.MODID)
public class Axess {

    public static final String MODID = "axess";

    private static final Logger LOGGER = LogUtils.getLogger();


    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> AXESS_CREATIVE_TAB = CREATIVE_MODE_TABS.register("axess", () -> CreativeModeTab.builder()
            .displayItems((parameters, output) -> {
                for (RegistryObject<Item> item :
                        AxessItemRegistry.getEntries()) {
                    output.accept(item.get());
                }
                for (RegistryObject<Block> block :
                        AxessBlockRegistry.getEntries()) {
                    try {
                        output.accept(block.get().asItem());
                    } catch (Exception e) {}
                }
            }).build());

    public Axess(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        AxessConfig.registerConfig(context);

        AxessBlockRegistry.register(modEventBus);
        AxessItemRegistry.register(modEventBus);
        AxessBlockEntityRegistry.register(modEventBus);
        AxessMenuRegistry.register(modEventBus);
        AxessSoundRegistry.register(modEventBus);

        CREATIVE_MODE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        context.registerConfig(ModConfig.Type.COMMON, AxessConfig.SPEC);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[aXess] I'm alive!");
    }

}
