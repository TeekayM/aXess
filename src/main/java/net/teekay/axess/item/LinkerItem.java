package net.teekay.axess.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.teekay.axess.Axess;
import net.teekay.axess.AxessConfig;
import net.teekay.axess.block.IPairableBlockEntity;
import net.teekay.axess.block.readers.KeycardReaderBlockEntity;
import net.teekay.axess.block.receiver.ReceiverBlockEntity;
import net.teekay.axess.utilities.AccessUtils;

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

                if (player.isShiftKeyDown() && itemStack.getOrCreateTag().contains("selPos")) {
                    itemStack.getOrCreateTag().remove("selPos");
                    player.displayClientMessage(CANCEL, true);
                    return InteractionResult.CONSUME;
                } else if (clickedBE instanceof IPairableBlockEntity clickedBEPairable) {
                    if (!clickedBEPairable.canBePairedBy(player))
                        return InteractionResult.PASS;

                    if (player.isShiftKeyDown()) {
                        clickedBEPairable.clearPairings();
                        player.displayClientMessage(CLEARED_LINKS, true);
                        return InteractionResult.SUCCESS;
                    }

                    if (itemStack.getOrCreateTag().contains("selPos")) { // clicked second
                        BlockPos firstPos = BlockPos.of(itemStack.getTag().getLong("selPos"));

                        if (clickedPos.equals(firstPos)) return InteractionResult.PASS;

                        if (Math.sqrt(clickedPos.distSqr(firstPos)) >= AxessConfig.maxPairDistance) {
                            player.displayClientMessage(TOO_FAR, true);
                            return InteractionResult.PASS;
                        }

                        BlockEntity firstBE = level.getBlockEntity(firstPos);
                        if (level.getBlockEntity(firstPos) instanceof IPairableBlockEntity firstBEPairable) {
                            if (!firstBEPairable.canBePairedBy(player))
                                return InteractionResult.PASS;

                            if (!firstBEPairable.canPairWith(clickedBE) || !clickedBEPairable.canPairWith(firstBE))
                                return InteractionResult.PASS;

                            firstBEPairable.handlePairing(clickedBE);

                            player.displayClientMessage(LINKED_OK, true);
                            itemStack.getOrCreateTag().remove("selPos");

                            return InteractionResult.SUCCESS;
                        } else {
                            player.displayClientMessage(LINKABLE_NOT_PRESENT, true);
                            itemStack.getOrCreateTag().remove("selPos");
                            return InteractionResult.CONSUME;
                        }
                    } else { // clicked first
                        player.displayClientMessage(SELECTED_FIRST, true);
                        itemStack.getOrCreateTag().putLong("selPos", clickedPos.asLong());
                        return InteractionResult.SUCCESS;
                    }
                }

            }
        }

        return super.useOn(pContext);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return super.isFoil(pStack) || pStack.getOrCreateTag().contains("selPos");
    }
}
