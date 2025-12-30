package net.teekay.axess.block.readers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.teekay.axess.access.AccessActivationMode;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.item.AccessWrenchItem;
import net.teekay.axess.item.keycard.AbstractKeycardItem;
import net.teekay.axess.registry.AxessSoundRegistry;
import net.teekay.axess.utilities.AccessUtils;
import net.teekay.axess.utilities.VoxelShapeUtilities;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class AbstractKeycardReaderBlock extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public final VoxelShape VOXEL_SHAPE_1 = Block.box(3, 1, 15, 13, 15, 16);
    public final VoxelShape VOXEL_SHAPE_2 = Block.box(3, 5, 14, 13, 13, 15);

    public final VoxelShape VOXEL_SHAPE = Shapes.join(VOXEL_SHAPE_1, VOXEL_SHAPE_2, BooleanOp.OR);

    public final VoxelShape VOXEL_SHAPE_SOUTH = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE, Direction.NORTH, Direction.SOUTH);
    public final VoxelShape VOXEL_SHAPE_WEST = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE, Direction.NORTH, Direction.WEST);
    public final VoxelShape VOXEL_SHAPE_EAST = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE, Direction.NORTH, Direction.EAST);

    public final VoxelShape VOXEL_SHAPE_FLOOR_X = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE, Direction.NORTH, Direction.UP);
    public final VoxelShape VOXEL_SHAPE_FLOOR_Z = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE_FLOOR_X, Direction.NORTH, Direction.WEST);

    public final VoxelShape VOXEL_SHAPE_CEILING_X = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE, Direction.NORTH, Direction.DOWN);
    public final VoxelShape VOXEL_SHAPE_CEILING_Z = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE_CEILING_X, Direction.NORTH, Direction.WEST);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;


    public AbstractKeycardReaderBlock(BlockBehaviour.Properties properties) {
        super(properties
                .lightLevel((bs) -> {
            return bs.getValue(POWERED) ? 6 : 6  ;
        }));
        this.registerDefaultState(
                this.stateDefinition.any().setValue(WATERLOGGED, false)
        );
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction facing = pState.getValue(FACING);
        AttachFace face = pState.getValue(FACE);
        return switch (face) {
            case WALL -> switch (facing) {
                case SOUTH -> VOXEL_SHAPE_SOUTH;
                case WEST -> VOXEL_SHAPE_WEST;
                case EAST -> VOXEL_SHAPE_EAST;
                default -> VOXEL_SHAPE;
            };
            case CEILING -> switch (facing) {
                case SOUTH, NORTH -> VOXEL_SHAPE_CEILING_X;
                case WEST, EAST -> VOXEL_SHAPE_CEILING_Z;
                default -> VOXEL_SHAPE;
            };
            case FLOOR -> switch (facing) {
                case SOUTH, NORTH -> VOXEL_SHAPE_FLOOR_X;
                case WEST, EAST -> VOXEL_SHAPE_FLOOR_Z;
                default -> VOXEL_SHAPE;
            };
            default -> VOXEL_SHAPE;
        };
    }

    public InteractionResult deny() {


        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof KeycardReaderBlockEntity reader) {
            ItemStack item = pPlayer.getItemInHand(pHand);
            if (item.getItem() instanceof AccessWrenchItem && AccessUtils.canPlayerEditNetwork(pPlayer, reader.getAccessNetwork())) {
                NetworkHooks.openScreen((ServerPlayer) pPlayer, reader, pPos);
                return InteractionResult.SUCCESS;
            } else if (item.getItem() instanceof AbstractKeycardItem keycardItem) {
                AccessNetwork keycardNet = keycardItem.getAccessNetwork(item, pLevel);
                AccessNetwork readerNet = reader.getAccessNetwork();

                if (keycardNet == null || readerNet == null) return InteractionResult.PASS;
                if (keycardNet != readerNet) return InteractionResult.PASS;

                AccessLevel keycardAL = keycardItem.getAccessLevel(item, pLevel);
                ArrayList<AccessLevel> readerALs = reader.getAccessLevels();

                if (keycardAL == null || readerALs == null || readerALs.size() == 0) return onFail(pLevel, pState, pPos);

                if (switch (reader.getCompareMode()) {
                    case SPECIFIC -> readerALs.contains(keycardAL);
                    case BIGGER_THAN_OR_EQUAL -> keycardAL.getPriority() >= readerALs.get(0).getPriority();
                    case LESSER_THAN_OR_EQUAL -> keycardAL.getPriority() <= readerALs.get(0).getPriority();
                }) {
                    reader.interact();
                    return onSuccess(pLevel, pState, pPos);
                } else {
                    return onFail(pLevel, pState, pPos);
                }
            }
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    public InteractionResult onFail(Level pLevel, BlockState pState, BlockPos pPos) {
        pLevel.playSeededSound(null, pPos.getX(), pPos.getY(), pPos.getZ(),
                AxessSoundRegistry.KEYCARD_READER_DECLINE.get(), SoundSource.BLOCKS, 1f, 1f, 0);
        return InteractionResult.SUCCESS;
    }

    public InteractionResult onSuccess(Level pLevel, BlockState pState, BlockPos pPos) {
        if (!pState.getValue(POWERED))
            pLevel.playSeededSound(null, pPos.getX() + 0.5f, pPos.getY() + 0.5f, pPos.getZ() + 0.5f,
                    AxessSoundRegistry.KEYCARD_READER_SUCCESS.get(), SoundSource.BLOCKS, 1f, 1f, 0);
        else
            pLevel.playSeededSound(null, pPos.getX() + 0.5f, pPos.getY() + 0.5f, pPos.getZ() + 0.5f,
                    AxessSoundRegistry.KEYCARD_READER_OFF.get(), SoundSource.BLOCKS, 1f, 1f, 0);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof KeycardReaderBlockEntity reader) {
            if (reader.getActivationMode() == AccessActivationMode.PULSE) {
                reader.deactivate();
            }
        }

        super.tick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(FACE);
        builder.add(POWERED);
        builder.add(WATERLOGGED);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluid = pContext.getLevel().getFluidState(pContext.getClickedPos());
        for(Direction direction : pContext.getNearestLookingDirections()) {
            BlockState blockstate;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockstate = this.defaultBlockState()
                        .setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR)
                        .setValue(FACING, direction == Direction.UP ? pContext.getHorizontalDirection() : pContext.getHorizontalDirection().getOpposite())
                        .setValue(POWERED, false)
                        .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
            } else {
                blockstate = this.defaultBlockState()
                        .setValue(FACE, AttachFace.WALL)
                        .setValue(FACING, direction.getOpposite())
                        .setValue(POWERED, false)
                        .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
            }

            if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
                return blockstate;
            }
        }

        return null;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }


    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return pBlockState.getValue(POWERED) && getConnectedDirection(pBlockState) == pSide ? 15 : 0;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pIsMoving && !pState.is(pNewState.getBlock())) {
            if (pState.getValue(POWERED)) {
                this.updateNeighbours(pState, pLevel, pPos);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    private void updateNeighbours(BlockState pState, Level pLevel, BlockPos pPos) {
        pLevel.updateNeighborsAt(pPos, this);
        pLevel.updateNeighborsAt(pPos.relative(getConnectedDirection(pState).getOpposite()), this);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

}
