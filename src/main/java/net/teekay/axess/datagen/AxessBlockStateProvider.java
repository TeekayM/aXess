package net.teekay.axess.datagen;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.BlockPos;
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
        keycardReader(AxessBlockRegistry.MINI_KEYCARD_READER_LEFT.get(), "mini_keycard_reader_left");
        keycardReader(AxessBlockRegistry.MINI_KEYCARD_READER_RIGHT.get(), "mini_keycard_reader_right");

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

        // LOCKDOWN RECEIVER
        String lockdownReceiverID = "lockdown_receiver";
        ModelFile lockdownReceiverModel = getBlockModel(lockdownReceiverID);
        horizontalBlock(AxessBlockRegistry.LOCKDOWN_RECEIVER.get(), lockdownReceiverModel);
        itemModels().getBuilder(lockdownReceiverID).parent(lockdownReceiverModel);

        // RECEIVER
        Block receiver = AxessBlockRegistry.RECEIVER.get();

        receiverBlock(receiver, "receiver");
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

    private void receiverBlock(Block receiver, String id) {
        ModelFile.ExistingModelFile on = models().getExistingFile(modLoc("block/"+id));
        ModelFile.ExistingModelFile off = models().getExistingFile(modLoc("block/"+id+"_off"));
        getVariantBuilder(receiver).forAllStates(blockState -> {
            Direction facing = blockState.getValue(ReceiverBlock.FACING);
            boolean powered = blockState.getValue(ReceiverBlock.POWERED);

            int rotX = 0;
            int rotY = 0;

            switch (facing) {
                case DOWN -> rotX = 180;
                case UP -> rotX = 0;
                case NORTH -> rotX = 90;
                case SOUTH -> {
                    rotX = 90;
                    rotY = 180;
                }
                case WEST -> {
                    rotX = 90;
                    rotY = 270;
                }
                case EAST -> {
                    rotX = 90;
                    rotY = 90;
                }
            }

            ModelFile.ExistingModelFile model = powered ? on : off;

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(rotX)
                    .rotationY(rotY)
                    .build();
        });

        itemModels().getBuilder(id).parent(on);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
