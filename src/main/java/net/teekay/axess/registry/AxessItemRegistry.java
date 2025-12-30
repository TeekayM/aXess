package net.teekay.axess.registry;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.teekay.axess.Axess;
import net.teekay.axess.item.AccessWrenchItem;
import net.teekay.axess.item.LinkerItem;
import net.teekay.axess.item.keycard.KeycardItem;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class AxessItemRegistry {

    public static final DeferredRegister<Item> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Axess.MODID);

    public static ArrayList<RegistryObject<Item>> keycards = new ArrayList<>();

    // REGISTRY
    public static RegistryObject<Item> KEYCARD = registerItem("keycard", KeycardItem::new);
    public static RegistryObject<Item> ACCESS_WRENCH = registerItem("access_wrench", AccessWrenchItem::new);
    public static RegistryObject<Item> READER_LINKER = registerItem("linker", LinkerItem::new);



    public static RegistryObject<Item> registerItem(String id, Supplier<Item> supplier) {
        RegistryObject<Item> item = DEFERRED_REGISTER.register(id, supplier);
        if (id.contains("keycard")) {
            keycards.add(item);
        }
        return item;
    }

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTER.register(eventBus);
    }

    public static Collection<RegistryObject<Item>> getEntries() {
        return DEFERRED_REGISTER.getEntries();
    }

    public static ArrayList<Item> getKeycards() {
        ArrayList<Item> iKeycards = new ArrayList<>();
        for (RegistryObject<Item> k:
                keycards){
            iKeycards.add(k.get());
        }
        return iKeycards;
    }
}
