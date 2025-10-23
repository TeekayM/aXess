package net.teekay.axess.block.receiver;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.block.readers.KeycardReaderBlockEntity;
import net.teekay.axess.block.receiver.ReceiverBlockEntity;
import net.teekay.axess.item.LinkerItem;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.registry.AxessItemRegistry;
import net.teekay.axess.registry.AxessSoundRegistry;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.RenderingUtilities;
import net.teekay.axess.utilities.RotationUtilities;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;

public class ReceiverBlockEntityRenderer implements BlockEntityRenderer<ReceiverBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public ReceiverBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(ReceiverBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof LinkerItem) {
            KeycardReaderBlockEntity readerPair = pBlockEntity.getReaderPair();
            if (readerPair != null) {
                RenderingUtilities.renderLine(pPoseStack, pBuffer, pBlockEntity.getBlockPos(), readerPair.getBlockPos());
                RenderingUtilities.renderBlockOutline(pPoseStack, pBuffer, pBlockEntity.getBlockPos(), 0.1f);
            }
        }
    }



}
