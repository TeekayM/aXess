package net.teekay.axess.registry;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeIntrinsicHolderTagAppender;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.teekay.axess.Axess;
import net.teekay.axess.screen.KeycardEditorMenu;
import net.teekay.axess.screen.KeycardReaderMenu;

public class AxessMenuRegistry {
    public static final DeferredRegister<MenuType<?>> DEFERRED_REGISTER =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Axess.MODID);

    public static final RegistryObject<MenuType<KeycardEditorMenu>> KEYCARD_EDITOR_MENU = registerMenu("keycard_editor", KeycardEditorMenu::new);
    public static final RegistryObject<MenuType<KeycardReaderMenu>> KEYCARD_READER_MENU = registerMenu("keycard_reader", KeycardReaderMenu::new);

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenu(String name, IContainerFactory<T> factory) {
        return DEFERRED_REGISTER.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTER.register(eventBus);
    }
}
