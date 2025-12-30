package net.teekay.axess.item.keycard;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.DistExecutor;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.access.AccessNetworkDataServer;
import net.teekay.axess.registry.AxessIconRegistry;
import org.checkerframework.checker.index.qual.PolyUpperBound;
import org.checkerframework.checker.units.qual.K;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractKeycardItem extends Item {
    private static final String ACCESS_LEVEL_KEY = "AccessLevel";
    private static final String ACCESS_NETWORK_KEY = "AccessNetwork";

    private static final Component NETWORK_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".keycard.access_network");
    private static final Component ACCESS_LEVEL_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".keycard.access_level");
    private static final Component NO_LEVEL_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".keycard.no_level");
    private static final Component UNCONFIGURED_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".keycard.unconfigured");

    public AbstractKeycardItem(Item.Properties properties) {
        super(properties);
    }

    public String getID() {
        return "keycard";
    }

    @Nullable
    public AccessNetwork getAccessNetwork(ItemStack stack, @Nullable Level level) {
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains(ACCESS_NETWORK_KEY)) {
            return null;
        }

        UUID id = tag.getUUID(ACCESS_NETWORK_KEY);

        // Server side
        if (level != null && !level.isClientSide()) {
            return AccessNetworkDataServer
                    .get(level.getServer())
                    .getNetwork(id);
        }

        // Client side — class-safe
        return DistExecutor.unsafeRunForDist(
                () -> () -> AccessNetworkDataClient.getNetwork(id),
                () -> () -> null
        );
    }


    @Nullable
    public AccessLevel getAccessLevel(ItemStack stack, @Nullable Level level) {
        CompoundTag tag = stack.getOrCreateTag();
        AccessNetwork net = getAccessNetwork(stack, level);

        if (!tag.contains(ACCESS_LEVEL_KEY) || net == null) {
            return null;
        }

        return net.getAccessLevel(stack.getOrCreateTag().getUUID(ACCESS_LEVEL_KEY));
    }

    public void setAccessNetwork(ItemStack stack, AccessNetwork network) {
        CompoundTag tag = stack.getOrCreateTag();

        tag.putUUID(ACCESS_NETWORK_KEY, network.getUUID());
    }

    public void setAccessLevel(ItemStack stack, AccessLevel level) {
        CompoundTag tag = stack.getOrCreateTag();

        tag.putUUID(ACCESS_LEVEL_KEY, level.getUUID());
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

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (pLevel == null) {super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced); return;}
        if (!pLevel.isClientSide()) return;

        AccessNetwork net = getAccessNetwork(pStack, pLevel);
        AccessLevel level = getAccessLevel(pStack, pLevel);

        if (net != null) {
            pTooltipComponents.add(
                    NETWORK_LABEL.copy()
                            .append(": ")
                            .append(net.getName())
                            .withStyle(ChatFormatting.GRAY)
            );

            pTooltipComponents.add(
                    ACCESS_LEVEL_LABEL.copy()
                            .append(": ")
                            .append(level == null ? NO_LEVEL_LABEL : Component.empty())
                            .append(level != null ? level.getName() : "")
                            .withStyle(ChatFormatting.GRAY)
            );
        } else {
            pTooltipComponents.add(
                    UNCONFIGURED_LABEL.copy()
                        .withStyle(ChatFormatting.GRAY)
            );
        }

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

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
        else return super.getName(pStack);
    }
}
