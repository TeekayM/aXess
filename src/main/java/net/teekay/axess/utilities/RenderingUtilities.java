package net.teekay.axess.utilities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.awt.*;

public class RenderingUtilities {
    public static void renderLine(PoseStack poseStack, MultiBufferSource buffer, BlockPos bp1, BlockPos bp2, Vec3 offset1, Vec3 offset2, Color color) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.debugLineStrip(3.0f));
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f matrixNrml = poseStack.last().normal();


        BlockPos a = new BlockPos(0,0,0);
        BlockPos b = new BlockPos(bp2.getX() - bp1.getX(), bp2.getY() - bp1.getY(), bp2.getZ() - bp1.getZ());

        float re = color.getRed() / 255f;
        float gr = color.getGreen() / 255f;
        float bl = color.getBlue() / 255f;

        consumer.addVertex(matrix, (float) offset1.x + a.getX(), (float) offset1.y + a.getY(), (float) offset1.z + a.getZ()).setColor(re, gr, bl, 1F)
                //.setNormal(matrixNrml, 0F, 1F, 0F)
                ;
        consumer.addVertex(matrix, (float) offset2.x +  b.getX(), (float) offset2.y + b.getY(), (float) offset2.z + b.getZ()).setColor(re, gr, bl, 1F)
                //.setNormal(matrixNrml, 0F, 1F, 0F)
                ;
    }

    public static void renderBlockOutline(PoseStack poseStack, MultiBufferSource buffer, BlockPos pos, float offset) {
        LevelRenderer.renderLineBox(poseStack, buffer.getBuffer(RenderType.lines()),
                offset, offset, offset, 1-offset, 1-offset, 1-offset,
                AxessColors.MAIN.getRed() / 255f,
                AxessColors.MAIN.getGreen() / 255f,
                AxessColors.MAIN.getBlue() / 255f, 1F);
    }

    public static Vec3 getBlockMiddlePos(
            BlockState state,
            Level level,
            BlockPos pos
    ) {
        VoxelShape shape = state.getShape(level, pos);
        AABB box = shape.bounds();
        return box.getCenter();
    }

    public static void renderVoxelShapeOutline(
            PoseStack poseStack,
            MultiBufferSource buffer,
            BlockState state,
            Level level,
            BlockPos pos,
            Color color,
            double offset
    ) {
        VoxelShape shape = state.getShape(level, pos);
        AABB box = shape.bounds();

        LevelRenderer.renderLineBox(
                poseStack,
                buffer.getBuffer(RenderType.lines()),
                box.minX - offset, box.minY - offset, box.minZ - offset,
                box.maxX + offset, box.maxY + offset, box.maxZ + offset,
                color.getRed() / 255f,
                color.getGreen() / 255f,
                color.getBlue() / 255f, 1
        );
    }
}
