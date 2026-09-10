package net.teekay.axess.registry;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.teekay.axess.Axess;
import net.teekay.axess.item.AccessWrenchItem;
import net.teekay.axess.item.LinkerItem;
import net.teekay.axess.item.keycard.KeycardItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AxessItemRegistry {

    public static final DeferredRegister<Item> DEFERRED_REGISTER = DeferredRegister.create(Registries.ITEM, Axess.MODID);

    public static ArrayList<DeferredHolder<Item, Item>> keycards = new ArrayList<>();

    // REGISTRY
    public static DeferredHolder<Item, Item> KEYCARD = registerItem("keycard", KeycardItem::new);
    public static DeferredHolder<Item, Item> ACCESS_WRENCH = registerItem("access_wrench", AccessWrenchItem::new);
    public static DeferredHolder<Item, Item> READER_LINKER = registerItem("linker", LinkerItem::new);



    public static DeferredHolder<Item, Item> registerItem(String id, Supplier<Item> supplier) {
        DeferredHolder<Item, Item> item = DEFERRED_REGISTER.register(id, supplier);
        if (id.contains("keycard")) {
            keycards.add(item);
        }
        return item;
    }

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTER.register(eventBus);
    }

    public static Collection<DeferredHolder<Item,? extends Item>> getEntries() {
        return DEFERRED_REGISTER.getEntries();
    }

    public static ArrayList<Item> getKeycards() {
        ArrayList<Item> iKeycards = new ArrayList<>();
        for (DeferredHolder<Item, Item> k:
                keycards){
            iKeycards.add(k.get());
        }
        return iKeycards;
    }
}
