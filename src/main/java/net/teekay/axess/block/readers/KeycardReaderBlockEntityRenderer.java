package net.teekay.axess.block.readers;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.block.link.BlockLink;
import net.teekay.axess.block.link.ILinkableBlockEntity;
import net.teekay.axess.block.link.LinkingSystem;
import net.teekay.axess.block.receiver.ReceiverBlockEntity;
import net.teekay.axess.item.LinkerItem;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.RenderingUtilities;
import net.teekay.axess.utilities.RotationUtilities;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;

public class KeycardReaderBlockEntityRenderer implements BlockEntityRenderer<KeycardReaderBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    private final RandomSource rS;

    public KeycardReaderBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
        this.rS = RandomSource.create(0);

    }

    public static final AxessIconRegistry.AxessIcon ALLOW_ICON = AxessIconRegistry.ACCEPT;
    public static final AxessIconRegistry.AxessIcon LOCKED_ICON = AxessIconRegistry.LOCKED;
    public static final AxessIconRegistry.AxessIcon NONE_ICON = AxessIconRegistry.CONFIGURE;

    public static final float CYCLE_TIME = 20F;

    public void renderIconQuad(
            BakedQuad originalQuad,
            PoseStack poseStack,
            VertexConsumer consumer,
            Color color) {

        if (originalQuad.getTintIndex() != 1) return;

        PoseStack.Pose pose = poseStack.last();
        Matrix4f mat = pose.pose();
        Matrix3f normalMat = pose.normal();

        int[] verts = originalQuad.getVertices();

        for (int i = 0; i < 4; i++) {
            int base = i * 8;

            float x = Float.intBitsToFloat(verts[base + 0]);
            float y = Float.intBitsToFloat(verts[base + 1]);
            float z = Float.intBitsToFloat(verts[base + 2]);

            float nx = originalQuad.getDirection().getStepX();
            float ny = originalQuad.getDirection().getStepY();
            float nz = originalQuad.getDirection().getStepZ();

            float u = (i == 0 || i == 1) ? 1f : 0f;
            float v = (i == 0 || i == 3) ? 1f : 0f;

            consumer.vertex(mat, x, y, z)
                    .color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f)
                    .uv(u, v)
                    .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(normalMat, nx, ny, nz)
                    .endVertex();
        }
    }


    @Override
    public void render(KeycardReaderBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {

        BakedModel model = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(pBlockEntity.getBlockState());

        BlockState state = pBlockEntity.getBlockState();

        Direction facing = state.getValue(AbstractKeycardReaderBlock.FACING);
        AttachFace face = state.getValue(AbstractKeycardReaderBlock.FACE);
        boolean powerState = state.getValue(AbstractKeycardReaderBlock.POWERED);
        boolean lockedState = state.getValue(AbstractKeycardReaderBlock.LOCKED);

       /* pPoseStack.pushPose();

        Vector3f rot = RotationUtilities.rotationFromDirAndFace(facing, face);

        pPoseStack.translate(0.5, 0.5, 0.5);

        pPoseStack.mulPose(Axis.XP.rotationDegrees((float) rot.x));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees((float) rot.z));
        pPoseStack.mulPose(Axis.YP.rotationDegrees((float) rot.y));

        pPoseStack.translate(0f, -2f/16f, 6f/16f - 0.001f);

        pPoseStack.scale(6/16f, 6/16f, 6/16f);

        pPoseStack.popPose();
        */

        AxessIconRegistry.AxessIcon icon = null;
        Color color = AxessColors.MAIN;
        ArrayList<AccessLevel> accessLevels = pBlockEntity.getAccessLevels();
        int levels = accessLevels.size();
        if (powerState && levels != 0) {
            icon = ALLOW_ICON;
            color = AxessColors.GREEN;
        } else if (lockedState && levels != 0) {
            icon = LOCKED_ICON;
            color = AxessColors.RED;
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



        //consumer.vertex(matrix, 0.5f, 0f, 0f).color(r, g, b, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.pack(15, 15)).normal(0, 1, 0).endVertex();
        //consumer.vertex(matrix, -0.5f, 0f, 0f).color(r, g, b, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.pack(15, 15)).normal(0, 1, 0).endVertex();
        //consumer.vertex(matrix, -0.5f, 1f, 0f).color(r, g, b, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.pack(15, 15)).normal(0, 1, 0).endVertex();
        //consumer.vertex(matrix, 0.5f, 1f, 0f).color(r, g, b, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.pack(15, 15)).normal(0, 1, 0).endVertex();

        for (Direction dir : Direction.values()) {
            for (BakedQuad quad : model.getQuads(state, dir, rS)) {
                renderIconQuad(quad, pPoseStack, consumer, color);
            }
        }

        for (BakedQuad quad : model.getQuads(state, null, rS)) {
            renderIconQuad(quad, pPoseStack, consumer, color);
        }


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
