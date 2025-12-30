package net.teekay.axess.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.block.keycardeditor.KeycardEditorBlockEntityRenderer;
import net.teekay.axess.block.readers.KeycardReaderBlockEntityRenderer;
import net.teekay.axess.block.receiver.ReceiverBlockEntityRenderer;
import net.teekay.axess.item.keycard.AbstractKeycardItem;
import net.teekay.axess.item.keycard.KeycardItem;
import net.teekay.axess.registry.AxessBlockEntityRegistry;
import net.teekay.axess.registry.AxessBlockRegistry;
import net.teekay.axess.registry.AxessItemRegistry;
import net.teekay.axess.registry.AxessMenuRegistry;
import net.teekay.axess.screen.KeycardEditorScreen;
import net.teekay.axess.screen.KeycardReaderScreen;

@Mod.EventBusSubscriber(modid = Axess.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AxessClientHandler {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(AxessMenuRegistry.KEYCARD_EDITOR_MENU.get(), KeycardEditorScreen::new);
        MenuScreens.register(AxessMenuRegistry.KEYCARD_READER_MENU.get(), KeycardReaderScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AxessBlockEntityRegistry.KEYCARD_READER.get(), KeycardReaderBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(AxessBlockEntityRegistry.KEYCARD_EDITOR.get(), KeycardEditorBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(AxessBlockEntityRegistry.RECEIVER.get(), ReceiverBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
                (stack, tintIndex) -> {
                    if (tintIndex == 3) {
                        AccessNetwork n = ((AbstractKeycardItem)stack.getItem()).getAccessNetwork(stack, null);
                        AccessLevel l = ((AbstractKeycardItem)stack.getItem()).getAccessLevel(stack, null);

                        if (n != null && l != null) {
                            return l.getColor().getRGB();
                        }

                        return 0; // default red
                    }
                    return 0xFFFFFF; // no tint (white)
                },
                AxessItemRegistry.getKeycards().toArray(new ItemLike[0])
        );
    }

}
