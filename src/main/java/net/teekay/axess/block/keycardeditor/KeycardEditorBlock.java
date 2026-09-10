package net.teekay.axess.block.keycardeditor;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.teekay.axess.Axess;
import net.teekay.axess.client.AxessClientMenus;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.world.item.Item;


public class KeycardEditorBlock extends BaseEntityBlock {
    public static final MapCodec<KeycardEditorBlock> CODEC = simpleCodec(KeycardEditorBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }


    public KeycardEditorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }


    public static final VoxelShape VOXEL_SHAPE = Block.box(0,0,0,16,8,16);

    public KeycardEditorBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .strength(4f, 6)
                .noOcclusion()
                .requiresCorrectToolForDrops());
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new KeycardEditorBlockEntity(pPos, pState);
    }


    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemstack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand != InteractionHand.MAIN_HAND) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof KeycardEditorBlockEntity) {
                ((ServerPlayer) pPlayer).openMenu((KeycardEditorBlockEntity) entity, buf -> buf.writeBlockPos(pPos));
            } else {
                throw new IllegalStateException("Missing container provider");
            }
        }

        return net.minecraft.world.ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
    }


    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof KeycardEditorBlockEntity) {
                ((KeycardEditorBlockEntity) blockEntity).drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return VOXEL_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        for(Direction direction : pContext.getNearestLookingDirections()) {
            BlockState blockstate;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockstate = this.defaultBlockState()
                        .setValue(FACING, direction == Direction.UP ? pContext.getHorizontalDirection() : pContext.getHorizontalDirection().getOpposite());
            } else {
                blockstate = this.defaultBlockState()
                        .setValue(FACING, direction.getOpposite());
            }

            if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
                return blockstate;
            }
        }

        return null;
    }

    private static final String MORE_INFO_LABEL_KEY = "tooltip."+ Axess.MODID + ".more_info";
    private static final Component LSHIFT_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".lshift");

    private static final Component INFO_LABEL = Component.translatable("tooltip."+ Axess.MODID + ".block.keycard_editor");

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
