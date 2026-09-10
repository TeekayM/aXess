package net.teekay.axess.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.teekay.axess.Axess;

import java.util.concurrent.CompletableFuture;
public class AxessDataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new AxessRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), AxessLootTableProvider.create(packOutput, lookupProvider));

        generator.addProvider(event.includeClient(), new AxessBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new AxessItemModelProvider(packOutput, existingFileHelper));

        AxessBlockTagProvider blockTagProvider = generator.addProvider(event.includeServer(), new AxessBlockTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new AxessItemTagsProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), existingFileHelper));
    }

}
