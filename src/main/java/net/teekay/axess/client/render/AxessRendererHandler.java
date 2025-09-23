package net.teekay.axess.client.render;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import net.teekay.axess.Axess;
import net.teekay.axess.item.keycard.AbstractKeycardItem;
import net.teekay.axess.item.keycard.KeycardItemRenderer;
import net.teekay.axess.registry.AxessItemRegistry;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = Axess.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AxessRendererHandler {
    public static HashMap<ResourceLocation, KeycardBakedModel> keycardBakedModelHashMap = new HashMap<>();

    @SubscribeEvent
    public static void onModelBake(ModelEvent.ModifyBakingResult event) {
        event.getModels().forEach((r, m) -> {
            if (r.toString().contains("keycard#inventory")) {
                KeycardBakedModel newModel = new KeycardBakedModel(m);
                event.getModels().put(r, newModel);
                keycardBakedModelHashMap.put(ResourceLocation.fromNamespaceAndPath(Axess.MODID, r.getPath()), newModel);
            }
        });

    }
}
