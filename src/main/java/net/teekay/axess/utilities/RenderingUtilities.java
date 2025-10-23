package net.teekay.axess.utilities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderingUtilities {
    public static void renderLine(PoseStack poseStack, MultiBufferSource buffer, BlockPos bp1, BlockPos bp2) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.debugLineStrip(3.0f));
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f matrixNrml = poseStack.last().normal();

        BlockPos a = new BlockPos(0,0,0);
        BlockPos b = new BlockPos(bp2.getX() - bp1.getX(), bp2.getY() - bp1.getY(), bp2.getZ() - bp1.getZ());

        float re = AxessColors.MAIN.getRed() / 255f;
        float gr = AxessColors.MAIN.getGreen() / 255f;
        float bl = AxessColors.MAIN.getBlue() / 255f;

        consumer.vertex(matrix, (float) a.getX() + 0.5f, (float) a.getY() + 0.5f, (float) a.getZ() + 0.5f)
                .color(re, gr, bl, 1F)
                //.normal(matrixNrml,0F, 1F, 0F)
                .endVertex();
        consumer.vertex(matrix, (float) b.getX() + 0.5f, (float) b.getY() + 0.5f, (float) b.getZ() + 0.5f)
                .color(re, gr, bl, 1F)
                //.normal(matrixNrml,0F, 1F, 0F)
                .endVertex();
    }

    public static void renderBlockOutline(PoseStack poseStack, MultiBufferSource buffer, BlockPos pos, float offset) {
        LevelRenderer.renderLineBox(poseStack, buffer.getBuffer(RenderType.lines()),
                offset, offset, offset, 1-offset, 1-offset, 1-offset,
                AxessColors.MAIN.getRed() / 255f,
                AxessColors.MAIN.getGreen() / 255f,
                AxessColors.MAIN.getBlue() / 255f, 1F);
    }
}
