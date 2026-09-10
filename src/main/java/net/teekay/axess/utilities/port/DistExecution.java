package net.teekay.axess.utilities.port;

import java.util.function.Supplier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;

public final class DistExecution {
    private DistExecution() {
    }

    public static void runWhenOn(Dist dist, Supplier<Runnable> target) {
        if (FMLLoader.getDist() == dist) {
            target.get().run();
        }
    }

    public static <T> T callWhenOn(Dist dist, Supplier<Supplier<T>> target) {
        return FMLLoader.getDist() == dist ? target.get().get() : null;
    }

    public static <T> T runForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        return (FMLLoader.getDist() == Dist.CLIENT ? clientTarget : serverTarget).get().get();
    }
}
