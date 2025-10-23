package net.teekay.axess.registry;

import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.teekay.axess.Axess;
import net.teekay.axess.block.keycardeditor.KeycardEditorBlockEntity;
import net.teekay.axess.block.readers.AbstractKeycardReaderBlock;
import net.teekay.axess.block.readers.KeycardReaderBlockEntity;
import net.teekay.axess.block.receiver.ReceiverBlockEntity;

import java.util.Collection;

public class AxessBlockEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Axess.MODID);



    // REGISTRY
    public static final RegistryObject<BlockEntityType<KeycardReaderBlockEntity>> KEYCARD_READER = registerKeycardReader(
            "keycard_reader", AxessBlockRegistry.KEYCARD_READER);

    public static final RegistryObject<BlockEntityType<KeycardEditorBlockEntity>> KEYCARD_EDITOR = DEFERRED_REGISTER.register("keycard_editor",
            () -> BlockEntityType.Builder.of(
                    KeycardEditorBlockEntity::new,
                    AxessBlockRegistry.KEYCARD_EDITOR.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<ReceiverBlockEntity>> RECEIVER = DEFERRED_REGISTER.register("receiver",
            () -> BlockEntityType.Builder.of(
                    ReceiverBlockEntity::new,
                    AxessBlockRegistry.RECEIVER.get()
            ).build(null));



    private static RegistryObject<BlockEntityType<KeycardReaderBlockEntity>> registerKeycardReader(String id, RegistryObject<Block> block) {
        return DEFERRED_REGISTER.register(id,
                () -> BlockEntityType.Builder.of(
                        (pos, state) -> new KeycardReaderBlockEntity(
                                DEFERRED_REGISTER.getEntries()
                                        .stream()
                                        .filter(e -> e.getId().getPath().equals(id))
                                        .findFirst()
                                        .get()
                                        .get(),
                                pos, state
                        ),
                        block.get()
                ).build(null));
    }

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTER.register(eventBus);
    }

    public static Collection<RegistryObject<BlockEntityType<?>>> getEntries() {
        return DEFERRED_REGISTER.getEntries();
    }

}
