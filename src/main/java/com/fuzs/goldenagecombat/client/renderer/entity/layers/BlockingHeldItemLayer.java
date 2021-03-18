package com.fuzs.goldenagecombat.client.renderer.entity.layers;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.client.element.LegacyAnimationsElement;
import com.fuzs.goldenagecombat.element.SwordBlockingElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class BlockingHeldItemLayer extends HeldItemLayer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
    
    public BlockingHeldItemLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRendererIn) {
        
        super(entityRendererIn);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        boolean isPrimaryHandRight = player.getPrimaryHand() == HandSide.RIGHT;
        ItemStack itemMainHand = isPrimaryHandRight ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
        ItemStack itemOffHand = isPrimaryHandRight ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
        if (!itemMainHand.isEmpty() || !itemOffHand.isEmpty()) {

            matrixStackIn.push();
            if (this.getEntityModel().isChild) {

                matrixStackIn.translate(0.0, 0.75, 0.0);
                matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            }

            this.renderHeldItem(player, itemMainHand, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HandSide.RIGHT, matrixStackIn, bufferIn, packedLightIn);
            this.renderHeldItem(player, itemOffHand, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HandSide.LEFT, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.pop();
        }
    }

    private void renderHeldItem(AbstractClientPlayerEntity player, ItemStack stack, ItemCameraTransforms.TransformType transform, HandSide handSide, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

        if (stack.isEmpty()) {

            return;
        }

        matrixStackIn.push();
        ((IHasArm) this.getEntityModel()).translateHand(handSide, matrixStackIn);
        boolean isHandSideLeft = handSide == HandSide.LEFT;
        Hand hand = isHandSideLeft ? Hand.OFF_HAND : Hand.MAIN_HAND;
        LegacyAnimationsElement element = (LegacyAnimationsElement) GoldenAgeCombat.LEGACY_ANIMATIONS;
        boolean renderLegacyPose = element.isEnabled() && element.oldBlockingPose;
        if (GoldenAgeCombat.SWORD_BLOCKING.isEnabled() && renderLegacyPose && SwordBlockingElement.isActiveItemStackBlocking(player) && player.getActiveHand() == hand) {

            matrixStackIn.translate((isHandSideLeft ? 1.0F : -1.0F) / 16.0F, 0.4375F, 0.0625F);

            // blocking
            matrixStackIn.translate(isHandSideLeft ? -0.035F : 0.05F, isHandSideLeft ? 0.045F : 0.0F, isHandSideLeft ? -0.135F : -0.1F);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees((isHandSideLeft ? -1.0F : 1.0F) * -50.0F));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-10.0F));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((isHandSideLeft ? -1.0F : 1.0F) * -60.0F));

            // old item layer
            matrixStackIn.translate(0.0F, 0.1875F, 0.0F);
            matrixStackIn.scale(0.625F, -0.625F, 0.625F);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-100.0F));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(isHandSideLeft ? 35.0F : 45.0F));

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
            applyTransformReverse(matrixStackIn, ibakedmodel.getItemCameraTransforms().getTransform(transform), isHandSideLeft);
        } else {

            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0F));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
            matrixStackIn.translate((isHandSideLeft ? -1.0F : 1.0F) / 16.0F, 0.125, -0.625);
        }

        Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(player, stack, transform, isHandSideLeft, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
    }

    public static void applyTransformReverse(MatrixStack matrixStackIn, net.minecraft.client.renderer.model.ItemTransformVec3f vec, boolean leftHand) {

        if (vec != net.minecraft.client.renderer.model.ItemTransformVec3f.DEFAULT) {

            float xAngle = vec.rotation.getX();
            float yAngle = leftHand ? -vec.rotation.getY() : vec.rotation.getY();
            float zAngle = leftHand ? -vec.rotation.getZ() : vec.rotation.getZ();
            Quaternion quaternion = new Quaternion(xAngle, yAngle, zAngle, true);
            quaternion.conjugate();

            matrixStackIn.scale(1.0F / vec.scale.getX(), 1.0F / vec.scale.getY(), 1.0F / vec.scale.getZ());
            matrixStackIn.rotate(quaternion);
            matrixStackIn.translate((leftHand ? -1.0F : 1.0F) * -vec.translation.getX(), -vec.translation.getY(), -vec.translation.getZ());
        }
    }

}
