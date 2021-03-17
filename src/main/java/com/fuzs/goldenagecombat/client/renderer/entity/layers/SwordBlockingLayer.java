package com.fuzs.goldenagecombat.client.renderer.entity.layers;

import com.fuzs.goldenagecombat.client.config.ClientConfigHandler;
import com.fuzs.goldenagecombat.client.renderer.BlockingPlayerRenderer;
import com.fuzs.goldenagecombat.util.BlockingHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("deprecation")
public class SwordBlockingLayer extends HeldItemLayer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private final BlockingHelper blockingHelper = new BlockingHelper();

    public SwordBlockingLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> p_i50934_1_) {
        super(p_i50934_1_);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        boolean flag = player.getPrimaryHand() == HandSide.RIGHT;
        ItemStack itemstack = flag ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
        ItemStack itemstack1 = flag ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
        if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {

            matrixStackIn.push();
            if (this.getEntityModel().isChild) {

                matrixStackIn.translate(0.0D, 0.75D, 0.0D);
                matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            }

            this.renderHeldItem(player, itemstack1, net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HandSide.RIGHT, matrixStackIn, bufferIn, packedLightIn);
            this.renderHeldItem(player, itemstack, net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HandSide.LEFT, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.pop();
        }
    }

    private void renderHeldItem(AbstractClientPlayerEntity player, ItemStack stack, net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType transform, HandSide handSide, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

        if (!stack.isEmpty()) {

            matrixStackIn.push();
            ((IHasArm) this.getEntityModel()).translateHand(handSide, matrixStackIn);
            boolean flag = handSide == HandSide.LEFT;
            if (ClientConfigHandler.BLOCKING_POSE.get() == ClientConfigHandler.BlockingPose.LEGACY && this.blockingHelper.isActiveItemStackBlocking(player) && player.getActiveHand() == (flag ? Hand.OFF_HAND : Hand.MAIN_HAND)) {

                matrixStackIn.translate((float) (flag ? 1 : -1) / 16.0F, 0.4375F, 0.0625F);

                // blocking
                matrixStackIn.translate(flag ? -0.035F : 0.05F, flag ? 0.045F : 0.0F, flag ? -0.135F : -0.1F);
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees((flag ? -1.0F : 1.0F) * -50.0F));
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-10.0F));
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((flag ? -1.0F : 1.0F) * -60.0F));

                // old item layer
                matrixStackIn.translate(0.0F, 0.1875F, 0.0F);
                matrixStackIn.scale(0.625F, -0.625F, 0.625F);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-100.0F));
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(flag ? 35.0F : 45.0F));

                // old item renderer
                matrixStackIn.translate(0.0F, -0.3F, 0.0F);
                matrixStackIn.scale(1.5F, 1.5F, 1.5F);
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(50.0F));
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(335.0F));
                matrixStackIn.translate(-0.9375F, -0.0625F, 0.0F);
                matrixStackIn.translate(0.5F, 0.5F, 0.25F);
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
                matrixStackIn.translate(0.0F, 0.0F, 0.28125F);

                // revert 1.8+ model changes
                IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, player.world, player);
                BlockingPlayerRenderer.applyTransformReverse(ibakedmodel.getItemCameraTransforms().getTransform(transform), flag, matrixStackIn);
            } else {

                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0F));
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
                matrixStackIn.translate((flag ? -1.0F : 1.0F) / 16.0F, 0.125D, -0.625D);
            }

            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(player, stack, transform, flag, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.pop();
        }
    }

}
