package net.teekay.axess.block.receiver;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.teekay.axess.registry.AxessBlockEntityRegistry;
import org.jetbrains.annotations.Nullable;

public class ReceiverBlock extends Block implements EntityBlock {
    public final VoxelShape VOXEL_SHAPE = Block.box(4, 0, 4, 12, 8, 12);

    public ReceiverBlock() {
        super(
        BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                .noOcclusion()
                .strength(4f, 6f)
                .requiresCorrectToolForDrops()
                .isRedstoneConductor((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
                .lightLevel((bs) -> {
                    return bs.getValue(POWERED) ? 14 : 0 ;
                })
        );
    }

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return AxessBlockEntityRegistry.RECEIVER.get().create(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        return belowState.isSolidRender(level, belowPos);
    }

    @javax.annotation.Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        for(Direction direction : pContext.getNearestLookingDirections()) {
            BlockState blockstate;
            blockstate = this.defaultBlockState()
                    .setValue(POWERED, false);

            if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
                return blockstate;
            }
        }

        return null;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return VOXEL_SHAPE;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(POWERED)) {
            double d0 = (double)pPos.getX() + 0.5D + (pRandom.nextDouble() - 0.5D) * 0.2D;
            double d1 = (double)pPos.getY() + 0.4D + (pRandom.nextDouble() - 0.5D) * 0.2D;
            double d2 = (double)pPos.getZ() + 0.5D + (pRandom.nextDouble() - 0.5D) * 0.2D;
            pLevel.addParticle(DustParticleOptions.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }
}
