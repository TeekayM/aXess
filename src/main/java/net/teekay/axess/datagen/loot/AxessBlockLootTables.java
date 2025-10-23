package net.teekay.axess.datagen.loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.teekay.axess.registry.AxessBlockRegistry;

import java.util.Set;

public class AxessBlockLootTables extends BlockLootSubProvider {
    public AxessBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(AxessBlockRegistry.KEYCARD_READER.get());
        this.dropSelf(AxessBlockRegistry.NETWORK_MANAGER.get());
        this.dropSelf(AxessBlockRegistry.KEYCARD_EDITOR.get());
        this.dropSelf(AxessBlockRegistry.RECEIVER.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return AxessBlockRegistry.DEFERRED_REGISTER.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
