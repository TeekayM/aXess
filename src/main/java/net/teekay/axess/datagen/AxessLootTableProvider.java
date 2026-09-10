package net.teekay.axess.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.teekay.axess.datagen.loot.AxessBlockLootTables;

import java.util.List;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;



public class AxessLootTableProvider {
    public static LootTableProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        return new LootTableProvider(output, Set.of(

        ), List.of(
                new LootTableProvider.SubProviderEntry(AxessBlockLootTables::new, LootContextParamSets.BLOCK)
        ), lookupProvider);
    }
}
