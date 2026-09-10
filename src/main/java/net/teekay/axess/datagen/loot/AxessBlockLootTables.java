package net.teekay.axess.datagen.loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.teekay.axess.registry.AxessBlockRegistry;

import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.registries.DeferredHolder;



public class AxessBlockLootTables extends BlockLootSubProvider {
    public AxessBlockLootTables(HolderLookup.Provider registries) {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
	}

    @Override
    protected void generate() {
        this.dropSelf(AxessBlockRegistry.KEYCARD_READER.get());
        this.dropSelf(AxessBlockRegistry.MINI_KEYCARD_READER_LEFT.get());
        this.dropSelf(AxessBlockRegistry.MINI_KEYCARD_READER_RIGHT.get());

        this.dropSelf(AxessBlockRegistry.NETWORK_MANAGER.get());
        this.dropSelf(AxessBlockRegistry.KEYCARD_EDITOR.get());
        this.dropSelf(AxessBlockRegistry.RECEIVER.get());
        this.dropSelf(AxessBlockRegistry.LOCKDOWN_RECEIVER.get());
    }

//    @Override
//    protected Iterable<Block> getKnownBlocks() {
//        return AxessBlockRegistry.DEFERRED_REGISTER.getEntries().stream().map(DeferredHolder::get)::iterator;
//    }
}
