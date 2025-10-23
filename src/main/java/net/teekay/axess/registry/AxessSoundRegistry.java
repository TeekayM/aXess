package net.teekay.axess.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.teekay.axess.Axess;

public class AxessSoundRegistry {

    public static final DeferredRegister<SoundEvent> DEFERRED_REGISTER =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Axess.MODID);

    public static final RegistryObject<SoundEvent> KEYCARD_READER_SUCCESS = registerSoundEvents("keycard_reader_success");
    public static final RegistryObject<SoundEvent> KEYCARD_READER_OFF = registerSoundEvents("keycard_reader_off");
    public static final RegistryObject<SoundEvent> KEYCARD_READER_DECLINE = registerSoundEvents("keycard_reader_decline");

    public static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return DEFERRED_REGISTER.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Axess.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTER.register(eventBus);
    }
}
