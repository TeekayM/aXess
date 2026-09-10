package net.teekay.axess.block.networkmanager;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
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
import net.neoforged.fml.loading.FMLLoader;
import net.minecraft.world.item.Item;


public class NetworkManagerBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<NetworkManagerBlock> CODEC = simpleCodec(NetworkManagerBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }


    public NetworkManagerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }


    public static final VoxelShape VOXEL_SHAPE = Block.box(0, 0, 0, 16, 8, 16);

    public NetworkManagerBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(4f, 6f).noOcclusion().requiresCorrectToolForDrops());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemstack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand != InteractionHand.MAIN_HAND)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (pLevel.isClientSide() && FMLLoader.getDist() == Dist.CLIENT) {
            AxessClientMenus.openNetworkManagerScreen();
        }

        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        for (Direction direction : pContext.getNearestLookingDirections()) {
            BlockState blockstate;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockstate = this.defaultBlockState().setValue(FACING, direction == Direction.UP ? pContext.getHorizontalDirection() : pContext.getHorizontalDirection().getOpposite());
            } else {
                blockstate = this.defaultBlockState().setValue(FACING, direction.getOpposite());
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

    private static final String MORE_INFO_LABEL_KEY = "tooltip." + Axess.MODID + ".more_info";

    private static final Component LSHIFT_LABEL = Component.translatable("tooltip." + Axess.MODID + ".lshift");

    private static final Component INFO_LABEL = Component.translatable("tooltip." + Axess.MODID + ".block.network_manager");

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pLevel, List<Component> pTooltipComponents, TooltipFlag pFlag) {
        if (pLevel == null) {
            super.appendHoverText(pStack, pLevel, pTooltipComponents, pFlag);
            return;
        }
        // Tooltip
        if (Minecraft.getInstance().player != null)
            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                pTooltipComponents.add(INFO_LABEL.copy().withStyle(ChatFormatting.GRAY));
            } else {
                pTooltipComponents.add(Component.translatable(MORE_INFO_LABEL_KEY, LSHIFT_LABEL.copy().withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));
            }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pFlag);
    }
}
