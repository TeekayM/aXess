package net.teekay.axess.block.lockdownreceiver;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.teekay.axess.block.link.BlockLink;
import net.teekay.axess.block.link.ILinkableBlockEntity;
import net.teekay.axess.block.link.LinkingSystem;
import net.teekay.axess.item.LinkerItem;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.RenderingUtilities;

public class LockdownReceiverBlockEntityRenderer implements BlockEntityRenderer<LockdownReceiverBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public LockdownReceiverBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(LockdownReceiverBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.getLevel() == null) return;
        if (!(Minecraft.getInstance().player.getMainHandItem().getItem() instanceof LinkerItem)) return;

        Vec3 blockMiddlePos = RenderingUtilities.getBlockMiddlePos(pBlockEntity.getBlockState(), pBlockEntity.getLevel(), pBlockEntity.getBlockPos());

        double offset = 0.0125;
        for (BlockLink link : pBlockEntity.getLinks()) {
            ILinkableBlockEntity otherBEl = LinkingSystem.getLinkableAtBlockPos(pBlockEntity.getLevel(), link.getOther(pBlockEntity.getBlockPos()));
            if (otherBEl == null) return;

            BlockEntity otherBE = otherBEl.getBlockEntity();

            RenderingUtilities.renderLine(
                    pPoseStack, pBuffer,
                    pBlockEntity.getBlockPos(),
                    otherBEl.getBlockEntity().getBlockPos(),
                    blockMiddlePos,
                    RenderingUtilities.getBlockMiddlePos(otherBE.getBlockState(), otherBE.getLevel(), otherBE.getBlockPos()),
                    AxessColors.mixColors(pBlockEntity.getLinkingColor(), otherBEl.getLinkingColor())
            );

            RenderingUtilities.renderVoxelShapeOutline(
                    pPoseStack, pBuffer,
                    pBlockEntity.getBlockState(), pBlockEntity.getLevel(),
                    pBlockEntity.getBlockPos(), pBlockEntity.getLinkingColor(),
                    offset
            );

            offset += 0.0125;
        }
    }



}
