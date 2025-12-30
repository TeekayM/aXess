package net.teekay.axess.item.keycard;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.math.MatrixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.teekay.axess.Axess;
import net.teekay.axess.client.render.AxessRendererHandler;
import net.teekay.axess.registry.AxessIconRegistry;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

        Color keycardColor = keycardItem.getRenderColor(stack);

        BakedModel model = AxessRendererHandler.keycardBakedModelHashMap.get(modelRL);

        ps.pushPose();

        render(stack,ctx,false,ps,buffer,light,overlay,model);


        VertexConsumer glow = buffer.getBuffer(RenderType.solid());
        for (var quad : model.getQuads(null, null, rS)) {
            if (quad.getTintIndex() != 1) continue;

            glow.putBulkData(ps.last(), quad,
                    keycardColor.getRed() / 255f, keycardColor.getGreen() / 255f, keycardColor.getBlue() / 255f, 1f,
                    LightTexture.FULL_BRIGHT, overlay, true);
        }

        ResourceLocation icon = keycardItem.getIconTex(stack);

        VertexConsumer display = buffer.getBuffer(RenderType.eyes(icon));
        for (var quad : model.getQuads(null, null, rS)) {
            if (quad.getTintIndex() != 2) continue;

            Matrix4f mat = ps.last().pose();
            Matrix3f normalMat = ps.last().normal();

            int[] data = quad.getVertices();

            for (int i = 0; i < 4; i++) {
                float x = Float.intBitsToFloat(data[i * 8]);
                float y = Float.intBitsToFloat(data[i * 8 + 1]);
                float z = Float.intBitsToFloat(data[i * 8 + 2]);

                float u = (i == 1 || i == 0) ? 0.0f : 1.0f;
                float v = (i == 1 || i == 2) ? 1.0f : 0.0f;

                display.vertex(mat, x, y, z)
                        .color(keycardColor.getRed() / 255f, keycardColor.getGreen() / 255f, keycardColor.getBlue() / 255f, 255)
                        .uv(u, v)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(LightTexture.FULL_BRIGHT)
                        .normal(normalMat, quad.getDirection().getStepX(), quad.getDirection().getStepY(), quad.getDirection().getStepZ())
                        .endVertex();
            }
        }

        ps.popPose();
    }

    public void render(ItemStack pItemStack, ItemDisplayContext pDisplayContext, boolean pLeftHand, PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay, BakedModel pModel) {
        if (!pItemStack.isEmpty()) {
            pPoseStack.pushPose();

            for (var model : pModel.getRenderPasses(pItemStack, false)) {
                for (var rendertype : model.getRenderTypes(pItemStack, false)) {
                    VertexConsumer vertexconsumer;

                    vertexconsumer = ItemRenderer.getFoilBufferDirect(pBuffer, rendertype, true, pItemStack.hasFoil());

                    Minecraft.getInstance().getItemRenderer().renderModelLists(model, pItemStack, pCombinedLight, pCombinedOverlay, pPoseStack, vertexconsumer);
                }
            }

            pPoseStack.popPose();
        }
    }
}
