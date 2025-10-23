package net.teekay.axess.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import net.teekay.axess.block.keycardeditor.KeycardEditorBlockEntity;
import net.teekay.axess.block.readers.KeycardReaderBlockEntity;
import net.teekay.axess.registry.AxessBlockRegistry;
import net.teekay.axess.registry.AxessMenuRegistry;

public class KeycardReaderMenu extends AbstractContainerMenu {
    public final KeycardReaderBlockEntity blockEntity;
    private final Level level;

    public KeycardReaderMenu(int pContainerId, Inventory inv, FriendlyByteBuf buf) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public KeycardReaderMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(AxessMenuRegistry.KEYCARD_READER_MENU.get(), pContainerId);

        blockEntity = ((KeycardReaderBlockEntity) entity);
        this.level = inv.player.level();
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return ContainerLevelAccess.create(level, blockEntity.getBlockPos()).evaluate((level, bp) -> {
            return level.getBlockEntity(bp) instanceof KeycardReaderBlockEntity && pPlayer.distanceToSqr((double) bp.getX() + 0.5D, (double) bp.getY() + 0.5D, (double) bp.getZ() + 0.5D) <= 64.0D;
        }, true);
    }
}
