package net.teekay.axess.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.teekay.axess.Axess;
import net.teekay.axess.registry.AxessBlockRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AxessBlockTagProvider extends BlockTagsProvider {
    public AxessBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Axess.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(AxessBlockRegistry.KEYCARD_READER.get())
                .add(AxessBlockRegistry.NETWORK_MANAGER.get())
                .add(AxessBlockRegistry.KEYCARD_EDITOR.get())
                .add(AxessBlockRegistry.RECEIVER.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(AxessBlockRegistry.KEYCARD_READER.get())
                .add(AxessBlockRegistry.NETWORK_MANAGER.get())
                .add(AxessBlockRegistry.KEYCARD_EDITOR.get())
                .add(AxessBlockRegistry.RECEIVER.get());
    }
}
