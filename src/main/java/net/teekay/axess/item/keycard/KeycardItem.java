package net.teekay.axess.item.keycard;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.teekay.axess.Axess;

import java.util.function.Consumer;

public class KeycardItem extends AbstractKeycardItem {
    public KeycardItem() {
        super(new Properties().stacksTo(1));
    }
}
