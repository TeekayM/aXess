package net.teekay.axess.block.receiver;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.teekay.axess.Axess;
import net.teekay.axess.registry.AxessBlockEntityRegistry;
import net.teekay.axess.utilities.VoxelShapeUtilities;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import net.minecraft.world.item.Item;


public class ReceiverBlock extends DirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final com.mojang.serialization.MapCodec<ReceiverBlock> CODEC = simpleCodec(ReceiverBlock::new);

    @Override
    protected com.mojang.serialization.MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }


    public ReceiverBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public final VoxelShape VOXEL_SHAPE = Block.box(4, 0, 4, 12, 8, 12);

    public VoxelShape getVoxelShape() {
        return VOXEL_SHAPE;
    }

    @SuppressWarnings("this-escape")
    public final VoxelShape VOXEL_SHAPE_NORTH =  VoxelShapeUtilities.rotateShape(getVoxelShape(), Direction.UP, Direction.DOWN);

    @SuppressWarnings("this-escape")
    public final VoxelShape VOXEL_SHAPE_SOUTH = VoxelShapeUtilities.rotateShape(getVoxelShape(), Direction.UP, Direction.SOUTH);
    public final VoxelShape VOXEL_SHAPE_WEST =  VoxelShapeUtilities.rotateShape(VOXEL_SHAPE_NORTH, Direction.SOUTH, Direction.EAST);
    public final VoxelShape VOXEL_SHAPE_EAST = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE_NORTH, Direction.SOUTH, Direction.WEST);

    public final VoxelShape VOXEL_SHAPE_DOWN = VoxelShapeUtilities.rotateShape(VOXEL_SHAPE_NORTH, Direction.SOUTH, Direction.DOWN);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @SuppressWarnings("this-escape")
    public ReceiverBlock() {
        super(
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .noOcclusion()
                        .strength(4f, 6f)
                        .requiresCorrectToolForDrops()
                        .isRedstoneConductor((state, level, pos) -> false)
                        .isViewBlocking((state, level, pos) -> false)
                        .lightLevel((bs) -> {
                            return bs.getValue(POWERED) ? 7 : 0 ;
                        })
        );
        this.registerDefaultState(
                this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(FACING, Direction.UP)
        );
    }

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
        builder.add(FACING);
        builder.add(POWERED);
        builder.add(WATERLOGGED);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @javax.annotation.Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluid = pContext.getLevel().getFluidState(pContext.getClickedPos());
        for(Direction direction : pContext.getNearestLookingDirections()) {
            BlockState blockstate;
            blockstate = this.defaultBlockState()
                    .setValue(FACING, direction.getOpposite())
                    .setValue(POWERED, false)
                    .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);

            if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
                return blockstate;
            }
        }

        return null;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction facing = pState.getValue(FACING);
        return switch (facing) {
                case UP -> getVoxelShape();
                case DOWN -> VOXEL_SHAPE_DOWN;
                case SOUTH -> VOXEL_SHAPE_SOUTH;
                case WEST -> VOXEL_SHAPE_WEST;
                case EAST -> VOXEL_SHAPE_EAST;
                case NORTH -> VOXEL_SHAPE_NORTH;
                default -> getVoxelShape();
            };
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
        return pBlockState.getValue(POWERED) && pBlockState.getValue(BlockStateProperties.FACING) == pSide ? 15 : 0;
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(POWERED)) {
            double d0 = (double)pPos.getX() + 0.5D + (pRandom.nextDouble() - 0.5D) * 0.2D;
            double d1 = (double)pPos.getY() + 0.4D + (pRandom.nextDouble() - 0.5D) * 0.2D;
            double d2 = (double)pPos.getZ() + 0.5D + (pRandom.nextDouble() - 0.5D) * 0.2D;
            pLevel.addParticle(DustParticleOptions.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
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
        pLevel.updateNeighborsAt(pPos.relative(pState.getValue(FACING).getOpposite()), this);
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

    private static final String MORE_INFO_LABEL_KEY = "tooltip."+ Axess.MODID + ".more_info";
    private static final Component LSHIFT_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".lshift");

    private static final Component INFO_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".block.receiver");

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pLevel, List<Component> pTooltipComponents, TooltipFlag pFlag) {
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
