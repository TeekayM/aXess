package net.teekay.axess.block.keycardeditor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.teekay.axess.Axess;
import net.teekay.axess.item.keycard.AbstractKeycardItem;
import net.teekay.axess.registry.AxessBlockEntityRegistry;
import net.teekay.axess.screen.KeycardEditorMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KeycardEditorBlockEntity extends BlockEntity implements MenuProvider {
    public static class KeycardItemStackHandler extends ItemStackHandler {
        public KeycardItemStackHandler(int i) {
            super(i);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return super.isItemValid(slot, stack) && (stack.getItem() instanceof AbstractKeycardItem);
        }
    }


    private static final Component TITLE = Component.translatable("gui." + Axess.MODID + ".keycard_editor");

    private final ItemStackHandler itemStackHandler = new KeycardItemStackHandler(1);
    public static final int KEYCARD_SLOT = 0;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();


    public KeycardEditorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(AxessBlockEntityRegistry.KEYCARD_EDITOR.get(), pPos, pBlockState);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemStackHandler.getSlots());

        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            inv.setItem(i, itemStackHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new KeycardEditorMenu(pContainerId, pPlayerInventory, this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemStackHandler);
    }

    @Override
    public void load(CompoundTag pTag) {
        System.out.println(pTag);
        itemStackHandler.deserializeNBT(pTag.getCompound("inventory"));

        super.load(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemStackHandler.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return TITLE;
    }


}
