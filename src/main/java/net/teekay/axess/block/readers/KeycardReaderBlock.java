package net.teekay.axess.block.readers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.teekay.axess.registry.AxessBlockEntityRegistry;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class KeycardReaderBlock extends AbstractKeycardReaderBlock {
    public static final com.mojang.serialization.MapCodec<KeycardReaderBlock> CODEC = simpleCodec(KeycardReaderBlock::new);

    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }


    public KeycardReaderBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }



    public KeycardReaderBlock() {
        super(
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
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
