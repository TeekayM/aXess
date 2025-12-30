package net.teekay.axess.block.readers;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.block.receiver.ReceiverBlockEntity;
import net.teekay.axess.item.LinkerItem;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.RenderingUtilities;
import net.teekay.axess.utilities.RotationUtilities;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;

public class KeycardReaderBlockEntityRenderer implements BlockEntityRenderer<KeycardReaderBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public KeycardReaderBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    public static final AxessIconRegistry.AxessIcon ALLOW_ICON = AxessIconRegistry.ACCEPT;
    public static final AxessIconRegistry.AxessIcon NONE_ICON = AxessIconRegistry.CONFIGURE;

    public static final float CYCLE_TIME = 20F;

    @Override
    public void render(KeycardReaderBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {

        Direction facing = pBlockEntity.getBlockState().getValue(AbstractKeycardReaderBlock.FACING);
        AttachFace face = pBlockEntity.getBlockState().getValue(AbstractKeycardReaderBlock.FACE);
        boolean powerState = pBlockEntity.getBlockState().getValue(AbstractKeycardReaderBlock.POWERED);

        pPoseStack.pushPose();

        Vector3f rot = RotationUtilities.rotationFromDirAndFace(facing, face);

        pPoseStack.translate(0.5, 0.5, 0.5);

        pPoseStack.mulPose(Axis.XP.rotationDegrees((float) rot.x));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees((float) rot.z));
        pPoseStack.mulPose(Axis.YP.rotationDegrees((float) rot.y));

        pPoseStack.translate(0f, -2f/16f, 6f/16f - 0.001f);

        pPoseStack.scale(6/16f, 6/16f, 6/16f);

        AxessIconRegistry.AxessIcon icon = null;
        Color color = AxessColors.MAIN;
        ArrayList<AccessLevel> accessLevels = pBlockEntity.getAccessLevels();
        int levels = accessLevels.size();

        if (powerState) {
            icon = ALLOW_ICON;
            color = AxessColors.GREEN;
        } else if (levels != 0) {
            if (pBlockEntity.isOverrideDisplay()) {
                icon = pBlockEntity.getOverrideIcon();
                color = pBlockEntity.getOverrideColor();
            } else {
                int index = (int) (Minecraft.getInstance().level.getGameTime() / CYCLE_TIME) % levels;
                icon = accessLevels.get(index).getIcon();
                color = accessLevels.get(index).getColor();
            }
        }




        VertexConsumer consumer = pBuffer.getBuffer(RenderType.eyes(icon != null ? icon.TEXTURE : NONE_ICON.TEXTURE));
        Matrix4f matrix = pPoseStack.last().pose();

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        consumer.vertex(matrix, 0.5f, 0f, 0f).color(r, g, b, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.pack(15, 15)).normal(0, 1, 0).endVertex();
        consumer.vertex(matrix, -0.5f, 0f, 0f).color(r, g, b, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.pack(15, 15)).normal(0, 1, 0).endVertex();
        consumer.vertex(matrix, -0.5f, 1f, 0f).color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.pack(15, 15)).normal(0, 1, 0).endVertex();
        consumer.vertex(matrix, 0.5f, 1f, 0f).color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.pack(15, 15)).normal(0, 1, 0).endVertex();

        pPoseStack.popPose();


        if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof LinkerItem) {
            KeycardReaderBlockEntity readerPair = pBlockEntity.getReaderPair();
            if (readerPair != null) {
                RenderingUtilities.renderLine(pPoseStack, pBuffer, pBlockEntity.getBlockPos(), readerPair.getBlockPos());
                RenderingUtilities.renderBlockOutline(pPoseStack, pBuffer, pBlockEntity.getBlockPos(), 0);
            }

            ReceiverBlockEntity receiverPair = pBlockEntity.getReceiverPair();
            if (receiverPair != null) {
                RenderingUtilities.renderLine(pPoseStack, pBuffer, pBlockEntity.getBlockPos(), receiverPair.getBlockPos());
                RenderingUtilities.renderBlockOutline(pPoseStack, pBuffer, pBlockEntity.getBlockPos(), 0.1f);
            }
        }
    }



}
