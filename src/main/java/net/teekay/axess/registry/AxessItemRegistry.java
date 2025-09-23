package net.teekay.axess.registry;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.teekay.axess.Axess;
import net.teekay.axess.item.keycard.KeycardItem;
import java.util.Collection;
import java.util.function.Supplier;

public class AxessItemRegistry {

    public static final DeferredRegister<Item> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Axess.MODID);


    // REGISTRY
    public static RegistryObject<Item> KEYCARD = registerItem("keycard", KeycardItem::new);



    public static RegistryObject<Item> registerItem(String id, Supplier<Item> supplier) {
        return DEFERRED_REGISTER.register(id, supplier);
    }

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTER.register(eventBus);
    }

    public static Collection<RegistryObject<Item>> getEntries() {
        return DEFERRED_REGISTER.getEntries();
    }
}
