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
import net.teekay.axess.Axess;
import net.teekay.axess.AxessConfig;
import net.teekay.axess.block.readers.KeycardReaderBlockEntity;

import java.util.UUID;

public class ReaderLinkerItem extends Item {
    public ReaderLinkerItem() {
        super(new Properties().stacksTo(1));
    }

    public Component READER_NOT_PRESENT = Component.translatable("item."+ Axess.MODID+".reader_linker.reader_not_present");
    public Component SELECTED_FIRST = Component.translatable("item."+ Axess.MODID+".reader_linker.selected_first");
    public Component LINKED_OK = Component.translatable("item."+ Axess.MODID+".reader_linker.linked_ok");
    public Component CANCEL = Component.translatable("item."+ Axess.MODID+".reader_linker.cancel");
    public Component TOO_FAR = Component.translatable("item."+ Axess.MODID+".reader_linker.too_far");


    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (!pContext.getHand().equals(InteractionHand.MAIN_HAND)) return InteractionResult.PASS;
        if (pContext.getPlayer() == null) return InteractionResult.PASS;

        ItemStack itemStack = pContext.getItemInHand();
        Player player = pContext.getPlayer();
        Level level = pContext.getLevel();

        if (!level.isClientSide()) {
            if (itemStack.getItem() instanceof ReaderLinkerItem) {

                BlockPos clickedPos = pContext.getClickedPos();
                if (level.getBlockEntity(clickedPos) instanceof KeycardReaderBlockEntity clickedReader) {
                    if (itemStack.getOrCreateTag().contains("selPos")) { // clicked second
                        BlockPos firstPos = BlockPos.of(itemStack.getTag().getLong("selPos"));

                        if (clickedPos.equals(firstPos)) return InteractionResult.PASS;

                        if (clickedPos.distSqr(firstPos) >= AxessConfig.maxReaderPairDistance) {
                            player.displayClientMessage(TOO_FAR, true);
                            return InteractionResult.PASS;
                        }

                        if (level.getBlockEntity(firstPos) instanceof KeycardReaderBlockEntity firstReader) {
                            firstReader.setPairPos(clickedPos);
                            clickedReader.setPairPos(firstPos);
                            UUID pairID = UUID.randomUUID();
                            firstReader.setPairID(pairID);
                            clickedReader.setPairID(pairID);

                            firstReader.setChanged();
                            clickedReader.setChanged();

                            player.displayClientMessage(LINKED_OK, true);
                            itemStack.getOrCreateTag().remove("selPos");

                            return InteractionResult.SUCCESS;
                        } else {
                            player.displayClientMessage(READER_NOT_PRESENT, true);
                            itemStack.getOrCreateTag().remove("selPos");
                            return InteractionResult.CONSUME;
                        }
                    } else { // clicked first
                        player.displayClientMessage(SELECTED_FIRST, true);
                        itemStack.getOrCreateTag().putLong("selPos", clickedPos.asLong());
                        return InteractionResult.SUCCESS;
                    }
                } else if (player.isShiftKeyDown() && itemStack.getOrCreateTag().contains("selPos")) {
                    itemStack.getOrCreateTag().remove("selPos");
                    player.displayClientMessage(CANCEL, true);
                    return InteractionResult.CONSUME;
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
