package net.teekay.axess.datagen;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.teekay.axess.Axess;
import net.teekay.axess.block.readers.AbstractKeycardReaderBlock;
import net.teekay.axess.block.receiver.ReceiverBlock;
import net.teekay.axess.registry.AxessBlockRegistry;

public class AxessBlockStateProvider extends BlockStateProvider {
    public AxessBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Axess.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // KEYCARD READERS
        keycardReader(AxessBlockRegistry.KEYCARD_READER.get(), "keycard_reader");

        // NETWORK MANAGER
        String networkManagerID = "network_manager";
        ModelFile networkManagerModel = getBlockModel(networkManagerID);
        horizontalBlock(AxessBlockRegistry.NETWORK_MANAGER.get(), networkManagerModel);
        itemModels().getBuilder(networkManagerID).parent(networkManagerModel);

        // KEYCARD EDITOR
        String keycardEditorID = "keycard_editor";
        ModelFile keycardEditorModel = getBlockModel(keycardEditorID);
        horizontalBlock(AxessBlockRegistry.KEYCARD_EDITOR.get(), keycardEditorModel);
        itemModels().getBuilder(keycardEditorID).parent(keycardEditorModel);

        // RECEIVER
        Block receiver = AxessBlockRegistry.RECEIVER.get();

        ModelFile offModel = getBlockModel("receiver_off");
        ModelFile onModel = getBlockModel("receiver");

        getVariantBuilder(receiver)
                .partialState().with(ReceiverBlock.POWERED, false)
                .modelForState().modelFile(offModel).addModel()
                .partialState().with(ReceiverBlock.POWERED, true)
                .modelForState().modelFile(onModel).addModel();

        itemModels().getBuilder("receiver").parent(onModel);
    }

    private ModelFile getBlockModel(String id) {
        ModelFile m = models().getExistingFile(modLoc("block/" + id));
        return m;
    }

    private void keycardReader(Block reader, String id) {
        ModelFile readerModel = models().getExistingFile(modLoc("block/" + id));
        horizontalFaceBlock(reader,readerModel);
        itemModels().getBuilder(id).parent(readerModel);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
