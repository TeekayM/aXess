package net.teekay.axess.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.teekay.axess.Axess;
import net.teekay.axess.block.keycardeditor.KeycardEditorBlock;
import net.teekay.axess.block.lockdownreceiver.LockdownReceiverBlock;
import net.teekay.axess.block.networkmanager.NetworkManagerBlock;
import net.teekay.axess.block.readers.KeycardReaderBlock;
import net.teekay.axess.block.readers.MiniKeycardReaderLeftBlock;
import net.teekay.axess.block.readers.MiniKeycardReaderRightBlock;
import net.teekay.axess.block.receiver.ReceiverBlock;

import java.util.*;
import java.util.function.Supplier;
import net.neoforged.neoforge.registries.DeferredHolder;


public class AxessBlockRegistry {

    public static final DeferredRegister<Block> DEFERRED_REGISTER = DeferredRegister.create(Registries.BLOCK, Axess.MODID);



    // REGISTRY
    public static final DeferredHolder<Block, Block> KEYCARD_READER = registerBlock("keycard_reader", KeycardReaderBlock::new);
    public static final DeferredHolder<Block, Block> MINI_KEYCARD_READER_LEFT = registerBlock("mini_keycard_reader_left", MiniKeycardReaderLeftBlock::new);
    public static final DeferredHolder<Block, Block> MINI_KEYCARD_READER_RIGHT = registerBlock("mini_keycard_reader_right", MiniKeycardReaderRightBlock::new);

    public static final DeferredHolder<Block, Block> NETWORK_MANAGER = registerBlock("network_manager", NetworkManagerBlock::new);
    public static final DeferredHolder<Block, Block> KEYCARD_EDITOR = registerBlock("keycard_editor", KeycardEditorBlock::new);
    public static final DeferredHolder<Block, Block> RECEIVER = registerBlock("receiver", ReceiverBlock::new);
    public static final DeferredHolder<Block, Block> LOCKDOWN_RECEIVER = registerBlock("lockdown_receiver", LockdownReceiverBlock::new);

    private static DeferredHolder<Block, Block> registerBlock(String id, Supplier<Block> block) { return registerBlock(id, block, true); }
    private static DeferredHolder<Block, Block> registerBlock(String id, Supplier<Block> blockSupplier, boolean withItem) {
        DeferredHolder<Block, Block> block = DEFERRED_REGISTER.register(id, blockSupplier);

        if (withItem) {
            AxessItemRegistry.DEFERRED_REGISTER.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
        }

        return block;
    }

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTER.register(eventBus);
    }

    public static Collection<DeferredHolder<Block,? extends Block>> getEntries() {
        return DEFERRED_REGISTER.getEntries();
    }

}
