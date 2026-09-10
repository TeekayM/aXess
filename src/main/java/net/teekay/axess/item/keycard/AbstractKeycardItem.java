package net.teekay.axess.item.keycard;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.access.AccessNetworkDataServer;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.utilities.port.DistExecution;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractKeycardItem extends Item {

    private static final String ACCESS_LEVEL_KEY = "AccessLevel";

    private static final String ACCESS_NETWORK_KEY = "AccessNetwork";

    private static final Component NETWORK_LABEL = Component.translatable("tooltip." + Axess.MODID + ".keycard.access_network");

    private static final Component ACCESS_LEVEL_LABEL = Component.translatable("tooltip." + Axess.MODID + ".keycard.access_level");

    private static final Component NO_LEVEL_LABEL = Component.translatable("tooltip." + Axess.MODID + ".keycard.no_level");

    private static final Component UNCONFIGURED_LABEL = Component.translatable("tooltip." + Axess.MODID + ".keycard.unconfigured");

    private static final String MORE_INFO_LABEL_KEY = "tooltip." + Axess.MODID + ".more_info";

    private static final Component LSHIFT_LABEL = Component.translatable("tooltip." + Axess.MODID + ".lshift");

    private static final Component INFO_LABEL = Component.translatable("tooltip." + Axess.MODID + ".item.keycard");

    public AbstractKeycardItem(Item.Properties properties) {
        super(properties);
    }

    public String getID() {
        return "keycard";
    }

    @Nullable
    public AccessNetwork getAccessNetwork(ItemStack stack, @Nullable Level level) {
        CompoundTag tag = (stack).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(ACCESS_NETWORK_KEY)) {
            return null;
        }
        UUID id = tag.getUUID(ACCESS_NETWORK_KEY);
        // Server side
        if (level != null && !level.isClientSide()) {
            return AccessNetworkDataServer.get(level.getServer()).getNetwork(id);
        }
        // Client side — class-safe
        return DistExecution.runForDist(() -> () -> AccessNetworkDataClient.getNetwork(id), () -> () -> null);
    }

    @Nullable
    public AccessLevel getAccessLevel(ItemStack stack, @Nullable Level level) {
        CompoundTag tag = (stack).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        AccessNetwork net = getAccessNetwork(stack, level);
        if (!tag.contains(ACCESS_LEVEL_KEY) || net == null) {
            return null;
        }
        return net.getAccessLevel((stack).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getUUID(ACCESS_LEVEL_KEY));
    }

    public void setAccessNetwork(ItemStack stack, AccessNetwork network) {
        CompoundTag tag = (stack).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putUUID(ACCESS_NETWORK_KEY, network.getUUID());
        if ((tag).isEmpty()) {
            (stack).remove(DataComponents.CUSTOM_DATA);
        } else {
            (stack).set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    public void setAccessLevel(ItemStack stack, AccessLevel level) {
        CompoundTag tag = (stack).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putUUID(ACCESS_LEVEL_KEY, level.getUUID());
        if ((tag).isEmpty()) {
            (stack).remove(DataComponents.CUSTOM_DATA);
        } else {
            (stack).set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    public ResourceLocation getIconTex(ItemStack stack) {
        AccessLevel level = getAccessLevel(stack, null);
        if (level != null) {
            return level.getIcon().TEXTURE;
        } else {
            return AxessIconRegistry.NONE.TEXTURE;
        }
    }

    public Color getRenderColor(ItemStack stack) {
        AccessNetwork network = getAccessNetwork(stack, null);
        AccessLevel level = getAccessLevel(stack, null);
        if (network != null && level != null) {
            return level.getColor();
        } else {
            return Color.WHITE;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pTooltipContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        Level pLevel = pTooltipContext.level();
        if (pLevel == null) {
            super.appendHoverText(pStack, pTooltipContext, pTooltipComponents, pIsAdvanced);
            return;
        }
        AccessNetwork net = getAccessNetwork(pStack, pLevel);
        AccessLevel level = getAccessLevel(pStack, pLevel);
        if (net != null) {
            pTooltipComponents.add(NETWORK_LABEL.copy().append(": ").append(net.getName()).withStyle(ChatFormatting.GRAY));
            pTooltipComponents.add(ACCESS_LEVEL_LABEL.copy().append(": ").append(level == null ? NO_LEVEL_LABEL : Component.empty()).append(level != null ? level.getName() : "").withStyle(ChatFormatting.GRAY));
        } else {
            pTooltipComponents.add(UNCONFIGURED_LABEL.copy().withStyle(ChatFormatting.GRAY));
            // Tooltip
            if (Minecraft.getInstance().player != null)
                if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    pTooltipComponents.add(INFO_LABEL.copy().withStyle(ChatFormatting.GRAY));
                } else {
                    pTooltipComponents.add(Component.translatable(MORE_INFO_LABEL_KEY, LSHIFT_LABEL.copy().withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));
                }
        }
        super.appendHoverText(pStack, pTooltipContext, pTooltipComponents, pIsAdvanced);
    }

    @SuppressWarnings("removal")
    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {

            KeycardItemRenderer renderer = new KeycardItemRenderer(getID());

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    public boolean isSet(ItemStack pStack, @Nullable Level level) {
        return getAccessLevel(pStack, level) != null && getAccessNetwork(pStack, level) != null;
    }

    @Override
    public Component getName(ItemStack pStack) {
        if (isSet(pStack, null))
            return Component.literal(getAccessLevel(pStack, null).getName()).append(" ").append(Component.translatable("item." + Axess.MODID + ".keycard"));
        else
            return super.getName(pStack);
    }
}
