package net.teekay.axess.item;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.teekay.axess.Axess;
import net.teekay.axess.AxessConfig;
import net.teekay.axess.block.link.ILinkableBlockEntity;
import net.teekay.axess.block.link.LinkingSystem;
import org.joml.Random;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.List;

public class LinkerItem extends Item {
    public LinkerItem() {
        super(new Properties().stacksTo(1));
    }

    public Component LINKABLE_NOT_PRESENT = Component.translatable("item."+ Axess.MODID+".linker.linkable_not_present");
    public Component SELECTED_FIRST = Component.translatable("item."+ Axess.MODID+".linker.selected_first");
    public Component LINKED_OK = Component.translatable("item."+ Axess.MODID+".linker.linked_ok");
    public Component CANCEL = Component.translatable("item."+ Axess.MODID+".linker.cancel");
    public Component TOO_FAR = Component.translatable("item."+ Axess.MODID+".linker.too_far");
    public Component NO_PERMISSION = Component.translatable("item."+ Axess.MODID+".linker.no_permission");
    public Component CLEARED_LINKS = Component.translatable("item."+Axess.MODID+".linker.clear_links");

    public void playSuccessSound(UseOnContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Vec3 actualPos = pContext.getLevel().getBlockState(pos).getShape(pContext.getLevel(), pos).bounds().getCenter();
        pContext.getLevel().playSeededSound(null,
                pos.getX() + actualPos.x, pos.getY() + actualPos.y,  pos.getZ() + actualPos.z,
                SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.BLOCKS, 1, 1, Random.newSeed());
    }

    public void playClearSound(UseOnContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Vec3 actualPos = pContext.getLevel().getBlockState(pos).getShape(pContext.getLevel(), pos).bounds().getCenter();
        pContext.getLevel().playSeededSound(null,
                pos.getX() + actualPos.x, pos.getY() + actualPos.y,  pos.getZ() + actualPos.z,
                SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1, 0.2f, Random.newSeed());
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (!pContext.getHand().equals(InteractionHand.MAIN_HAND)) return InteractionResult.PASS;
        if (pContext.getPlayer() == null) return InteractionResult.PASS;

        ItemStack itemStack = pContext.getItemInHand();
        Player player = pContext.getPlayer();
        Level level = pContext.getLevel();

        if (!level.isClientSide()) {
            if (itemStack.getItem() instanceof LinkerItem) {

                BlockPos clickedPos = pContext.getClickedPos();
                BlockEntity clickedBE = level.getBlockEntity(clickedPos);

                if (player.isShiftKeyDown() && (itemStack).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains("selPos")) {
                    CustomData.update(DataComponents.CUSTOM_DATA, itemStack, modPorterCustomDataTag -> modPorterCustomDataTag.remove("selPos"));
                    player.displayClientMessage(CANCEL, true);
                    playClearSound(pContext);
                    return InteractionResult.CONSUME;
                } else if (clickedBE instanceof ILinkableBlockEntity clickedBELinkable) {
                    LinkingSystem.validateLinksOnBlock(clickedBELinkable);

                    if (!clickedBELinkable.canBeLinkedBy(player))
                        return InteractionResult.PASS;

                    if (player.isShiftKeyDown()) {
                        LinkingSystem.clearLinks(clickedBELinkable);
                        player.displayClientMessage(CLEARED_LINKS, true);
                        playSuccessSound(pContext);
                        return InteractionResult.SUCCESS;
                    }

                    if (!clickedBELinkable.canLink())
                        return InteractionResult.PASS;

                    if ((itemStack).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains("selPos")) { // clicked second
                        BlockPos firstPos = BlockPos.of(itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getLong("selPos"));

                        if (clickedPos.equals(firstPos)) return InteractionResult.PASS;

                        if (Math.sqrt(clickedPos.distSqr(firstPos)) >= AxessConfig.maxLinkDistance) {
                            player.displayClientMessage(TOO_FAR, true);
                            return InteractionResult.PASS;
                        }

                        BlockEntity firstBE = level.getBlockEntity(firstPos);
                        if (firstBE instanceof ILinkableBlockEntity firstBELinkable) {
                            LinkingSystem.validateLinksOnBlock(firstBELinkable);

                            if (!firstBELinkable.canLink())
                                return InteractionResult.PASS;
                            if (!firstBELinkable.canBeLinkedBy(player))
                                return InteractionResult.PASS;
                            if (!firstBELinkable.canLinkWith(clickedBE) || !clickedBELinkable.canLinkWith(firstBE))
                                return InteractionResult.PASS;

                            LinkingSystem.linkBlocks(firstBELinkable, clickedBELinkable);

                            player.displayClientMessage(LINKED_OK, true);
                            CustomData.update(DataComponents.CUSTOM_DATA, itemStack,  modPorterCustomDataTag -> modPorterCustomDataTag.remove("selPos"));

                            playSuccessSound(pContext);
                            return InteractionResult.SUCCESS;
                        } else {
                            player.displayClientMessage(LINKABLE_NOT_PRESENT, true);
                            CustomData.update(DataComponents.CUSTOM_DATA, itemStack,  modPorterCustomDataTag -> modPorterCustomDataTag.remove("selPos"));

                            return InteractionResult.CONSUME;
                        }
                    } else { // clicked first
                        player.displayClientMessage(SELECTED_FIRST, true);
                        CustomData.update(DataComponents.CUSTOM_DATA, itemStack,  modPorterCustomDataTag -> modPorterCustomDataTag.putLong("selPos", clickedPos.asLong()));

                        playSuccessSound(pContext);
                        return InteractionResult.SUCCESS;
                    }
                }

            }
        }

        return super.useOn(pContext);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return super.isFoil(pStack) || (pStack).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains("selPos");
    }

    private static final String MORE_INFO_LABEL_KEY = "tooltip."+ Axess.MODID + ".more_info";
    private static final Component LSHIFT_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".lshift");

    private static final Component INFO_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".item.linker");

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pTooltipContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (pTooltipContext == null) {super.appendHoverText(pStack, pTooltipContext, pTooltipComponents, pIsAdvanced); return;}

        // Tooltip
        if (Minecraft.getInstance().player != null)
            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                pTooltipComponents.add(
                        INFO_LABEL.copy().withStyle(ChatFormatting.GRAY)
                );
            } else {
                pTooltipComponents.add(
                        Component.translatable(MORE_INFO_LABEL_KEY, LSHIFT_LABEL.copy().withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY)
                );
            }

        super.appendHoverText(pStack, pTooltipContext, pTooltipComponents, pIsAdvanced);
    }
}
