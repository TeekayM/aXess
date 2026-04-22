package net.teekay.axess.block.readers;

import com.mojang.datafixers.util.Pair;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessActivationMode;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessPermission;
import net.teekay.axess.item.AccessWrenchItem;
import net.teekay.axess.item.keycard.AbstractKeycardItem;
import net.teekay.axess.registry.AxessSoundRegistry;
import net.teekay.axess.utilities.RenderingUtilities;
import net.teekay.axess.utilities.VoxelShapeUtilities;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractKeycardReaderBlock extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public final VoxelShape VOXEL_SHAPE_1 = Block.box(3, 1, 15, 13, 15, 16);
    public final VoxelShape VOXEL_SHAPE_2 = Block.box(3, 5, 14, 13, 13, 15);

    public VoxelShape getVoxelShape() {
        return Shapes.join(VOXEL_SHAPE_1, VOXEL_SHAPE_2, BooleanOp.OR);
    }

    public final VoxelShape VOXEL_SHAPE_SOUTH = VoxelShapeUtilities.rotateShape(getVoxelShape(), Direction.NORTH, Direction.SOUTH);
    public final VoxelShape VOXEL_SHAPE_WEST = VoxelShapeUtilities.rotateShape(getVoxelShape(), Direction.NORTH, Direction.WEST);
    public final VoxelShape VOXEL_SHAPE_EAST = VoxelShapeUtilities.rotateShape(getVoxelShape(), Direction.NORTH, Direction.EAST);

    public final VoxelShape VOXEL_SHAPE_FLOOR_X = VoxelShapeUtilities.rotateShape(getVoxelShape(), Direction.NORTH, Direction.UP);
    public final VoxelShape VOXEL_SHAPE_FLOOR_Z = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE_FLOOR_X, Direction.NORTH, Direction.WEST);

    public final VoxelShape VOXEL_SHAPE_FLOOR_X_NEG = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE_FLOOR_X, Direction.NORTH, Direction.SOUTH);
    public final VoxelShape VOXEL_SHAPE_FLOOR_Z_NEG = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE_FLOOR_X, Direction.NORTH, Direction.EAST);

    public final VoxelShape VOXEL_SHAPE_CEILING_X = VoxelShapeUtilities.rotateShape(getVoxelShape(), Direction.NORTH, Direction.DOWN);
    public final VoxelShape VOXEL_SHAPE_CEILING_Z = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE_CEILING_X, Direction.NORTH, Direction.WEST);

    public final VoxelShape VOXEL_SHAPE_CEILING_X_NEG = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE_CEILING_X, Direction.NORTH, Direction.SOUTH);
    public final VoxelShape VOXEL_SHAPE_CEILING_Z_NEG = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE_CEILING_X, Direction.NORTH, Direction.EAST);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;


    public AbstractKeycardReaderBlock(BlockBehaviour.Properties properties) {
        super(properties
                .lightLevel((bs) -> {
                    return bs.getValue(POWERED) ? 6 : 6;
                }));
        this.registerDefaultState(
                this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(LOCKED, false)
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
                default -> getVoxelShape();
            };
            case CEILING -> switch (facing) {
                case SOUTH -> VOXEL_SHAPE_CEILING_X;
                case NORTH -> VOXEL_SHAPE_CEILING_X_NEG;
                case EAST -> VOXEL_SHAPE_CEILING_Z;
                case WEST -> VOXEL_SHAPE_CEILING_Z_NEG;

                default -> getVoxelShape();
            };
            case FLOOR -> switch (facing) {
                case NORTH -> VOXEL_SHAPE_FLOOR_X;
                case SOUTH -> VOXEL_SHAPE_FLOOR_X_NEG;
                case WEST -> VOXEL_SHAPE_FLOOR_Z;
                case EAST -> VOXEL_SHAPE_FLOOR_Z_NEG;
                default -> getVoxelShape();
            };
            default -> getVoxelShape();
        };
    }

    public InteractionResult tryOverride(KeycardReaderBlockEntity reader, Level pLevel, BlockState pState, BlockPos pPos, AccessNetwork keycardNet, AccessLevel keycardLevel, InteractionResult failResult) {
        if (!pState.getValue(LOCKED)) {
            for (Pair<AccessNetwork, AccessLevel> pair :
                    reader.getOverrideAccessLevels()) {
                if (pair.getFirst().getUUID() == keycardNet.getUUID() && pair.getSecond().getUUID() == keycardLevel.getUUID()) {
                    return onSuccess(reader, pLevel, pState, pPos);
                }
            }
        }
        return onFail(reader, pLevel, pState, pPos);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof KeycardReaderBlockEntity reader) {
            ItemStack item = pPlayer.getItemInHand(pHand);
            if (item.getItem() instanceof AccessWrenchItem && (reader.getAccessNetwork() != null ? reader.getAccessNetwork().hasPermission(pPlayer, AccessPermission.READER_EDIT) : true)) {
                NetworkHooks.openScreen((ServerPlayer) pPlayer, reader, pPos);
                return InteractionResult.SUCCESS;
            } else if (item.getItem() instanceof AbstractKeycardItem keycardItem) {
                AccessNetwork keycardNet = keycardItem.getAccessNetwork(item, pLevel);
                AccessNetwork readerNet = reader.getAccessNetwork();

                if (keycardNet == null || readerNet == null) return InteractionResult.PASS;
                if (keycardNet != readerNet)
                    return tryOverride(reader, pLevel, pState, pPos, keycardNet, keycardItem.getAccessLevel(item, pLevel), InteractionResult.PASS);

                AccessLevel keycardAL = keycardItem.getAccessLevel(item, pLevel);
                ArrayList<AccessLevel> readerALs = reader.getAccessLevels();

                if (keycardAL == null || readerALs == null || readerALs.size() == 0 || pState.getValue(LOCKED))
                    return onFail(reader, pLevel, pState, pPos);

                if (switch (reader.getCompareMode()) {
                    case SPECIFIC -> readerALs.contains(keycardAL);
                    case BIGGER_THAN_OR_EQUAL -> keycardAL.getPriority() >= readerALs.get(0).getPriority();
                    case LESSER_THAN_OR_EQUAL -> keycardAL.getPriority() <= readerALs.get(0).getPriority();
                }) {
                    return onSuccess(reader, pLevel, pState, pPos);
                } else {
                    return tryOverride(reader, pLevel, pState, pPos, keycardNet, keycardAL, InteractionResult.PASS);
                }
            }
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    public InteractionResult onFail(KeycardReaderBlockEntity reader, Level pLevel, BlockState pState, BlockPos pPos) {
        Vec3 blockMiddlePos = RenderingUtilities.getBlockMiddlePos(pState, reader.getLevel(), pPos);
        pLevel.playSeededSound(null, pPos.getX() + blockMiddlePos.x, pPos.getY() + blockMiddlePos.y, pPos.getZ() + blockMiddlePos.z,
                AxessSoundRegistry.KEYCARD_READER_DECLINE.get(), SoundSource.BLOCKS, 1f, 1f, 0);
        return InteractionResult.SUCCESS;
    }

    public InteractionResult onSuccess(KeycardReaderBlockEntity reader, Level pLevel, BlockState pState, BlockPos pPos) {
        reader.interact();
        Vec3 blockMiddlePos = RenderingUtilities.getBlockMiddlePos(pState, reader.getLevel(), pPos);
        if (!pState.getValue(POWERED))
            pLevel.playSeededSound(null, pPos.getX() + blockMiddlePos.x, pPos.getY() + blockMiddlePos.y, pPos.getZ() + blockMiddlePos.z,
                    AxessSoundRegistry.KEYCARD_READER_SUCCESS.get(), SoundSource.BLOCKS, 1f, 1f, 0);
        else
            pLevel.playSeededSound(null, pPos.getX() + blockMiddlePos.x, pPos.getY() + blockMiddlePos.y, pPos.getZ() + blockMiddlePos.z,
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
        builder.add(LOCKED);
        builder.add(WATERLOGGED);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluid = pContext.getLevel().getFluidState(pContext.getClickedPos());
        for (Direction direction : pContext.getNearestLookingDirections()) {
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

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    private static final String MORE_INFO_LABEL_KEY = "tooltip."+ Axess.MODID + ".more_info";
    private static final Component LSHIFT_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".lshift");

    private static final Component INFO_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".block.reader");

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable BlockGetter pLevel, List<Component> pTooltipComponents, TooltipFlag pFlag) {
        if (pLevel == null) {super.appendHoverText(pStack, pLevel, pTooltipComponents, pFlag); return;}

        // Tooltip
        if (Minecraft.getInstance().player != null)
            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                pTooltipComponents.add(
                        INFO_LABEL.copy().withStyle(ChatFormatting.GRAY)
                );
            } else {
                pTooltipComponents.add(
                        Component.translatable(MORE_INFO_LABEL_KEY, LSHIFT_LABEL.copy().withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY)
                );
            }

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pFlag);
    }

}
