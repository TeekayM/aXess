package net.teekay.axess.block.keycardeditor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.teekay.axess.block.AccessBlockPowerState;
import net.teekay.axess.block.readers.AbstractKeycardReaderBlock;
import net.teekay.axess.block.readers.KeycardReaderBlockEntity;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.utilities.RotationUtilities;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Optional;

public class KeycardEditorBlockEntityRenderer implements BlockEntityRenderer<KeycardEditorBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public KeycardEditorBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(KeycardEditorBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {

        Direction facing = pBlockEntity.getBlockState().getValue(AbstractKeycardReaderBlock.FACING);

        pPoseStack.pushPose();

        pPoseStack.translate(0.5, 0.5, 0.5);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(switch (facing) {
            case NORTH -> 0;
            case SOUTH -> 180;
            case WEST -> 90;
            case EAST -> 270;
            default -> 0;
        }));
        pPoseStack.translate(0f, 6f/16f, 3f/16f);
        pPoseStack.mulPose(Axis.YP.rotation((pBlockEntity.getLevel().getGameTime() + pPartialTick) / 20f));
        pPoseStack.scale(1f/2.5f, 1f/2.5F, 1f/2.5f);
        //pPoseStack.mulPose(Axis.YP.rotationDegrees(90));

        Optional<IItemHandler> ih = pBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.NORTH).resolve();

        if (ih.isPresent() && pBlockEntity.getLevel() != null) {
            this.context.getItemRenderer().renderStatic(
                    ih.get().getStackInSlot(KeycardEditorBlockEntity.KEYCARD_SLOT),
                    ItemDisplayContext.FIXED,
                    getLightLevel(pBlockEntity.getLevel(), pBlockEntity.getBlockPos()),
                    OverlayTexture.NO_OVERLAY,
                    pPoseStack,
                    pBuffer,
                    pBlockEntity.getLevel(),
                    0
            );
        }


        pPoseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
