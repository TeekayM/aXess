package net.teekay.axess.registry;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.teekay.axess.Axess;
import net.teekay.axess.screen.KeycardEditorMenu;
import net.teekay.axess.screen.KeycardReaderMenu;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AxessMenuRegistry {
    public static final DeferredRegister<MenuType<?>> DEFERRED_REGISTER =
            DeferredRegister.create(Registries.MENU, Axess.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<KeycardEditorMenu>> KEYCARD_EDITOR_MENU = registerMenu("keycard_editor", KeycardEditorMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<KeycardReaderMenu>> KEYCARD_READER_MENU = registerMenu("keycard_reader", KeycardReaderMenu::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenu(String name, IContainerFactory<T> factory) {
        return DEFERRED_REGISTER.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTER.register(eventBus);
    }
}
