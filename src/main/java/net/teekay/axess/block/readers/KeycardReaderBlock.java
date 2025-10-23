package net.teekay.axess.block.readers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.teekay.axess.registry.AxessBlockEntityRegistry;
import org.jetbrains.annotations.Nullable;

public class KeycardReaderBlock extends AbstractKeycardReaderBlock {


    public KeycardReaderBlock() {
        super(
                Properties.copy(Blocks.IRON_BLOCK)
                        .noOcclusion().strength(4f, 6f)
                        .requiresCorrectToolForDrops()
        );
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return AxessBlockEntityRegistry.KEYCARD_READER.get().create(pPos, pState);
    }


}
