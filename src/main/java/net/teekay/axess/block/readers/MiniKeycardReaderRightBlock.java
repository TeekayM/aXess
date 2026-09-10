package net.teekay.axess.block.readers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.teekay.axess.registry.AxessBlockEntityRegistry;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MiniKeycardReaderRightBlock extends AbstractKeycardReaderBlock {
    public static final com.mojang.serialization.MapCodec<MiniKeycardReaderRightBlock> CODEC = simpleCodec(MiniKeycardReaderRightBlock::new);

    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }


    public MiniKeycardReaderRightBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }



    public MiniKeycardReaderRightBlock() {
        super(
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .noOcclusion().strength(4f, 6f)
                        .requiresCorrectToolForDrops()
        );
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return AxessBlockEntityRegistry.MINI_KEYCARD_READER_RIGHT.get().create(pPos, pState);
    }

    @Override
    public VoxelShape getVoxelShape() {
        return Block.box(2, 4, 15, 2+6, 4+8, 15+1);
    }

}
