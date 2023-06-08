package fuzs.goldenagecombat.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ClientConfig;
import fuzs.goldenagecombat.handler.SwordBlockingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerItemInHandLayer.class)
abstract class PlayerItemInHandLayerMixin<T extends Player, M extends EntityModel<T> & ArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {
    @Shadow
    @Final
    private ItemInHandRenderer itemInHandRenderer;

    public PlayerItemInHandLayerMixin(RenderLayerParent<T, M> renderLayerParent, ItemInHandRenderer itemInHandRenderer) {
        super(renderLayerParent, itemInHandRenderer);
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    protected void renderArmWithItem(LivingEntity entity, ItemStack stack, ItemDisplayContext transform, HumanoidArm arm, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, CallbackInfo callback) {
        if (stack.isEmpty()) return;
        if (entity.getUseItem() == stack && SwordBlockingHandler.isActiveItemStackBlocking((Player) entity) && !GoldenAgeCombat.CONFIG.get(ClientConfig.class).animations.simpleBlockingPose) {
            this.renderBlockingWithSword(entity, stack, transform, arm, poseStack, multiBufferSource, combinedLight);
            callback.cancel();
        } else if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).animations.swordBlockingWithShield) {
            if (entity.getOffhandItem() == stack && stack.getUseAnimation() == UseAnim.BLOCK) {
                callback.cancel();
            } else if (entity.getOffhandItem() == entity.getUseItem() && entity.getUseItem() != stack && entity.getOffhandItem().getUseAnimation() == UseAnim.BLOCK && SwordBlockingHandler.canItemStackBlock(stack)) {
                this.renderBlockingWithSword(entity, stack, transform, arm, poseStack, multiBufferSource, combinedLight);
                callback.cancel();
            }
        }
    }

    private void renderBlockingWithSword(LivingEntity entity, ItemStack stack, ItemDisplayContext transform, HumanoidArm arm, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight) {
        // those transformations are directly ported from Minecraft 1.7, resulting in a pixel-perfect recreation of third-person sword blocking
        // a lot has changed since then (the whole model system has been rewritten twice in 1.8 and 1.9, and had major changes in 1.14 and 1.15),
        // so we reset everything vanilla does now, and apply every single step that was done in 1.7
        // (there were multiple classes and layers involved in 1.7, it is noted down below which class every transformation came from)
        // all this is done in code and not using some custom json model predicate so that every item is supported by default
        poseStack.pushPose();
        this.getParentModel().translateToHand(arm, poseStack);
        boolean leftHand = arm == HumanoidArm.LEFT;
        this.applyItemBlockingTransform(poseStack, leftHand);
        this.applyItemTransformInverse(entity, stack, transform, poseStack, leftHand);
        this.itemInHandRenderer.renderItem(entity, stack, transform, leftHand, poseStack, multiBufferSource, combinedLight);
        poseStack.popPose();
    }

    private void applyItemBlockingTransform(PoseStack poseStack, boolean leftHand) {
        poseStack.translate((leftHand ? 1.0F : -1.0F) / 16.0F, 0.4375F, 0.0625F);
        // blocking
        poseStack.translate(leftHand ? -0.035F : 0.05F, leftHand ? 0.045F : 0.0F, leftHand ? -0.135F : -0.1F);
        poseStack.mulPose(Axis.YP.rotationDegrees((leftHand ? -1.0F : 1.0F) * -50.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(-10.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees((leftHand ? -1.0F : 1.0F) * -60.0F));
        // old item layer
        poseStack.translate(0.0F, 0.1875F, 0.0F);
        // this differs from 1.7 as there was a negative y scale being used, which is not supported on Minecraft 1.16+
        // therefore rotations on X and Y all had to be flipped down the line (and one rotation on X by 180 degrees has been added)
        poseStack.scale(0.625F, 0.625F, 0.625F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(-100.0F));
        poseStack.mulPose(Axis.YN.rotationDegrees(leftHand ? 35.0F : 45.0F));
        // old item renderer
        poseStack.translate(0.0F, -0.3F, 0.0F);
        poseStack.scale(1.5F, 1.5F, 1.5F);
        poseStack.mulPose(Axis.YN.rotationDegrees(50.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(335.0F));
        poseStack.translate(-0.9375F, -0.0625F, 0.0F);
        poseStack.translate(0.5F, 0.5F, 0.25F);
        poseStack.mulPose(Axis.YN.rotationDegrees(180.0F));
        poseStack.translate(0.0F, 0.0F, 0.28125F);
    }

    private void applyItemTransformInverse(LivingEntity entity, ItemStack stack, ItemDisplayContext transform, PoseStack poseStack, boolean leftHand) {
        // revert 1.8+ model changes, so we can work on a blank slate
        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, entity.level, entity, 0);
        applyTransformInverse(model.getTransforms().getTransform(transform), leftHand, poseStack);
    }

    private static void applyTransformInverse(ItemTransform vec, boolean leftHand, PoseStack matrixStackIn) {
        // this does the exact inverse of ItemTransform::apply which should be applied right after, so that in the end nothing has changed
        if (vec != ItemTransform.NO_TRANSFORM) {
            float angleX = vec.rotation.x();
            float angleY = leftHand ? -vec.rotation.y() : vec.rotation.y();
            float angleZ = leftHand ? -vec.rotation.z() : vec.rotation.z();
            Quaternionf quaternion = new Quaternionf().rotationXYZ(angleX * 0.017453292F, angleY * 0.017453292F, angleZ * 0.017453292F);
            quaternion.conjugate();
            matrixStackIn.scale(1.0F / vec.scale.x(), 1.0F / vec.scale.y(), 1.0F / vec.scale.z());
            matrixStackIn.mulPose(quaternion);
            matrixStackIn.translate((leftHand ? -1.0F : 1.0F) * -vec.translation.x(), -vec.translation.y(), -vec.translation.z());
        }
    }
}
