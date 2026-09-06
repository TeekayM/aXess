package net.teekay.axess.item.keycard;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.teekay.axess.Axess;
import net.teekay.axess.client.render.AxessRendererHandler;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.awt.Color;

@OnlyIn(Dist.CLIENT)
public class KeycardItemRenderer extends BlockEntityWithoutLevelRenderer {

    private final ResourceLocation modelRL;
    private final RandomSource rS;

    public KeycardItemRenderer(String id) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.modelRL = ResourceLocation.fromNamespaceAndPath(Axess.MODID, id);
        this.rS = RandomSource.create(0);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack ps,
                             MultiBufferSource buffer, int light, int overlay) {
        if (!(stack.getItem() instanceof AbstractKeycardItem keycardItem)) return;

        BakedModel model = AxessRendererHandler.keycardBakedModelHashMap.get(modelRL);
        if (model == null) return;

        Color keycardColor = keycardItem.getRenderColor(stack);
        float r = keycardColor.getRed() / 255f;
        float g = keycardColor.getGreen() / 255f;
        float b = keycardColor.getBlue() / 255f;

        ps.pushPose();

        render(stack, ctx, false, ps, buffer, light, overlay, model);

        VertexConsumer glow = buffer.getBuffer(RenderType.entityCutoutNoCull(model.getParticleIcon().atlasLocation()));

        Matrix4f glowMat = ps.last().pose();
        Matrix3f glowNormalMat = ps.last().normal();
        float glowBias = 0.001f;

        for (var quad : model.getQuads(null, null, rS)) {
            if (quad.getTintIndex() != 1) continue;

            int[] data = quad.getVertices();
            float nx = quad.getDirection().getStepX();
            float ny = quad.getDirection().getStepY();
            float nz = quad.getDirection().getStepZ();

            for (int i = 0; i < 4; i++) {
                int offset = i * 8;
                float x = Float.intBitsToFloat(data[offset]) + (nx * glowBias);
                float y = Float.intBitsToFloat(data[offset + 1]) + (ny * glowBias);
                float z = Float.intBitsToFloat(data[offset + 2]) + (nz * glowBias);

                float u = Float.intBitsToFloat(data[offset + 4]);
                float v = Float.intBitsToFloat(data[offset + 5]);

                glow.vertex(glowMat, x, y, z)
                        .color(r, g, b, 1.0f)
                        .uv(u, v)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(LightTexture.FULL_BRIGHT)
                        .normal(glowNormalMat, nx, ny, nz)
                        .endVertex();
            }
        }

        ResourceLocation icon = keycardItem.getIconTex(stack);
        if (icon != null) {
            VertexConsumer display = buffer.getBuffer(RenderType.entityCutoutNoCull(icon));

            Matrix4f mat = ps.last().pose();
            Matrix3f normalMat = ps.last().normal();

            float[] uvsU = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
            float[] uvsV = new float[]{0.0f, 1.0f, 1.0f, 0.0f};

            for (var quad : model.getQuads(null, null, rS)) {
                if (quad.getTintIndex() != 2) continue;

                int[] data = quad.getVertices();

                float normalX = quad.getDirection().getStepX();
                float normalY = quad.getDirection().getStepY();
                float normalZ = quad.getDirection().getStepZ();

                float bias = 0.001f;

                for (int i = 0; i < 4; i++) {
                    int offset = i * 8;
                    float x = Float.intBitsToFloat(data[offset]) + (normalX * bias);
                    float y = Float.intBitsToFloat(data[offset + 1]) + (normalY * bias);
                    float z = Float.intBitsToFloat(data[offset + 2]) + (normalZ * bias);

                    float u = uvsU[i];
                    float v = uvsV[i];

                    display.vertex(mat, x, y, z)
                            .color(r, g, b, 1.0f)
                            .uv(u, v)
                            .overlayCoords(OverlayTexture.NO_OVERLAY)
                            .uv2(LightTexture.FULL_BRIGHT)
                            .normal(normalMat, normalX, normalY, normalZ)
                            .endVertex();
                }
            }
        }

        ps.popPose();
    }

    public void render(ItemStack pItemStack, ItemDisplayContext pDisplayContext, boolean pLeftHand,
                       PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight,
                       int pCombinedOverlay, BakedModel pModel) {
        if (!pItemStack.isEmpty()) {
            pPoseStack.pushPose();

            for (var renderPassModel : pModel.getRenderPasses(pItemStack, false)) {
                for (var renderType : renderPassModel.getRenderTypes(pItemStack, false)) {
                    VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(pBuffer, renderType, true, pItemStack.hasFoil());
                    Minecraft.getInstance().getItemRenderer().renderModelLists(renderPassModel, pItemStack, pCombinedLight, pCombinedOverlay, pPoseStack, vertexconsumer);
                }
            }

            pPoseStack.popPose();
        }
    }
}