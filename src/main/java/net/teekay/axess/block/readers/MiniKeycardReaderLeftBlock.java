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

public class MiniKeycardReaderLeftBlock extends AbstractKeycardReaderBlock {
    public static final com.mojang.serialization.MapCodec<MiniKeycardReaderLeftBlock> CODEC = simpleCodec(MiniKeycardReaderLeftBlock::new);

    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }


    public MiniKeycardReaderLeftBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }


    public final VoxelShape VOXEL_SHAPE_1 = Block.box(3, 1, 15, 13, 15, 16);
    public final VoxelShape VOXEL_SHAPE_2 = Block.box(3, 5, 14, 13, 13, 15);


    public MiniKeycardReaderLeftBlock() {
        super(
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .noOcclusion().strength(4f, 6f)
                        .requiresCorrectToolForDrops()
        );
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return AxessBlockEntityRegistry.MINI_KEYCARD_READER_LEFT.get().create(pPos, pState);
    }

    @Override
    public VoxelShape getVoxelShape() {
        return Block.box(8, 4, 15, 8+6, 4+8, 15+1);
    }
}
