package net.teekay.axess.client.render;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.teekay.axess.Axess;
import net.teekay.axess.item.keycard.AbstractKeycardItem;
import net.teekay.axess.item.keycard.KeycardItemRenderer;
import net.teekay.axess.registry.AxessItemRegistry;

import java.util.HashMap;
import java.util.logging.Logger;
public class AxessRendererHandler {
    public static HashMap<ResourceLocation, KeycardBakedModel> keycardBakedModelHashMap = new HashMap<>();

    public static void onModelBake(ModelEvent.ModifyBakingResult event) {
        event.getModels().forEach((r, m) -> {
            if (r.toString().contains("keycard#inventory") && r.toString().contains(Axess.MODID)) {
                KeycardBakedModel newModel = new KeycardBakedModel(m);
                event.getModels().put(r, newModel);
                keycardBakedModelHashMap.put(ResourceLocation.fromNamespaceAndPath(Axess.MODID, r.id().getPath()), newModel);
            }
        });

    }
}
