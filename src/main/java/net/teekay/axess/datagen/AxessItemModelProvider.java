package net.teekay.axess.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.teekay.axess.Axess;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.registry.AxessItemRegistry;

import java.util.Objects;

public class AxessItemModelProvider extends ItemModelProvider {
    public AxessItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Axess.MODID, existingFileHelper);
    }

    public ItemModelBuilder rodItem(ResourceLocation item)
    {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
    }

    public ItemModelBuilder rodItem(Item item)
    {
        return rodItem(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
    }

    @Override
    protected void registerModels() {
        rodItem(AxessItemRegistry.ACCESS_WRENCH.get());
        rodItem(AxessItemRegistry.READER_LINKER.get());
    }
}
