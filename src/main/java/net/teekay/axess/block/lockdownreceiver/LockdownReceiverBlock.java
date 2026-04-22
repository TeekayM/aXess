package net.teekay.axess.block.lockdownreceiver;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.teekay.axess.Axess;
import net.teekay.axess.registry.AxessBlockEntityRegistry;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class LockdownReceiverBlock extends BaseEntityBlock {
    public static final VoxelShape VOXEL_SHAPE = Block.box(0,0,0,16,8,16);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public LockdownReceiverBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                .strength(4f, 6)
                .isRedstoneConductor((state, level, pos) -> false)
                .lightLevel((bs) -> bs.getValue(POWERED) ? 7 : 0)
                .noOcclusion()
                .requiresCorrectToolForDrops());
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false)
        );
    }

    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return AxessBlockEntityRegistry.LOCKDOWN_RECEIVER.get().create(pPos, pState);
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
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        for(Direction direction : pContext.getNearestLookingDirections()) {
            BlockState blockstate;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockstate = this.defaultBlockState()
                        .setValue(FACING, direction == Direction.UP ? pContext.getHorizontalDirection() : pContext.getHorizontalDirection().getOpposite())
                        .setValue(POWERED, false);
            } else {
                blockstate = this.defaultBlockState()
                        .setValue(FACING, direction.getOpposite())
                        .setValue(POWERED, false);
            }

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
            if (pState.getValue(BlockStateProperties.POWERED) && pLevel.getBlockEntity(pPos) instanceof LockdownReceiverBlockEntity receiver) {
                receiver.deactivate();
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
        boolean newValue = pLevel.hasNeighborSignal(pPos);
        if (pState.getValue(BlockStateProperties.POWERED) != newValue && pLevel.getBlockEntity(pPos) instanceof LockdownReceiverBlockEntity receiver) {
            if (newValue) {
                receiver.activate();
            } else {
                receiver.deactivate();
            }
        }
    }

    private static final String MORE_INFO_LABEL_KEY = "tooltip."+ Axess.MODID + ".more_info";
    private static final Component LSHIFT_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".lshift");

    private static final Component INFO_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".block.lockdown_receiver");

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltipComponents, TooltipFlag pFlag) {
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
