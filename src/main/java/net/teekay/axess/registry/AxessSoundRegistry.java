package net.teekay.axess.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.teekay.axess.Axess;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AxessSoundRegistry {

    public static final DeferredRegister<SoundEvent> DEFERRED_REGISTER =
            DeferredRegister.create(Registries.SOUND_EVENT, Axess.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> KEYCARD_READER_SUCCESS = registerSoundEvents("keycard_reader_success");
    public static final DeferredHolder<SoundEvent, SoundEvent> KEYCARD_READER_OFF = registerSoundEvents("keycard_reader_off");
    public static final DeferredHolder<SoundEvent, SoundEvent> KEYCARD_READER_DECLINE = registerSoundEvents("keycard_reader_decline");

    public static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvents(String name) {
        return DEFERRED_REGISTER.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Axess.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTER.register(eventBus);
    }
}
