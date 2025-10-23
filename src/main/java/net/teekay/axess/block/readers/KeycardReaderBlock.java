package net.teekay.axess.block.readers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.teekay.axess.block.readers.AbstractKeycardReaderBlock;
import net.teekay.axess.registry.AxessBlockEntityRegistry;
import org.jetbrains.annotations.Nullable;

public class IronKeycardReaderBlock extends AbstractKeycardReaderBlock {


    public IronKeycardReaderBlock() {
        super(
                Properties.copy(Blocks.IRON_BLOCK).noOcclusion()
        );
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return AxessBlockEntityRegistry.IRON_KEYCARD_READER.get().create(pPos, pState);
    }


}
