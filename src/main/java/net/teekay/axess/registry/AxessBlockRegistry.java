package net.teekay.axess.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.teekay.axess.Axess;
import net.teekay.axess.block.keycardeditor.KeycardEditorBlock;
import net.teekay.axess.block.networkmanager.NetworkManagerBlock;
import net.teekay.axess.block.readers.KeycardReaderBlock;
import net.teekay.axess.block.receiver.ReceiverBlock;

import java.util.*;
import java.util.function.Supplier;


public class AxessBlockRegistry {

    public static final DeferredRegister<Block> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Axess.MODID);



    // REGISTRY
    public static final RegistryObject<Block> KEYCARD_READER = registerBlock("keycard_reader", KeycardReaderBlock::new);
    public static final RegistryObject<Block> NETWORK_MANAGER = registerBlock("network_manager", NetworkManagerBlock::new);
    public static final RegistryObject<Block> KEYCARD_EDITOR = registerBlock("keycard_editor", KeycardEditorBlock::new);
    public static final RegistryObject<Block> RECEIVER = registerBlock("receiver", ReceiverBlock::new);

    private static RegistryObject<Block> registerBlock(String id, Supplier<Block> block) { return registerBlock(id, block, true); }
    private static RegistryObject<Block> registerBlock(String id, Supplier<Block> blockSupplier, boolean withItem) {
        RegistryObject<Block> block = DEFERRED_REGISTER.register(id, blockSupplier);

        if (withItem) {
            AxessItemRegistry.DEFERRED_REGISTER.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
        }

        return block;
    }

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTER.register(eventBus);
    }

    public static Collection<RegistryObject<Block>> getEntries() {
        return DEFERRED_REGISTER.getEntries();
    }

}
