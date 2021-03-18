package com.fuzs.goldenagecombat.mixin.client;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.client.element.LegacyAnimationsElement;
import com.fuzs.goldenagecombat.element.SwordBlockingElement;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(BipedModel.class)
public abstract class BipedModelMixin<T extends LivingEntity> extends AgeableModel<T> {

    @Shadow
    public ModelRenderer bipedRightArm;
    @Shadow
    public ModelRenderer bipedLeftArm;

    // setArmSwingRotations
    @Inject(method = "setRotationAngles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/BipedModel;func_230486_a_(Lnet/minecraft/entity/LivingEntity;F)V"))
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callbackInfo) {

        if (entityIn instanceof AbstractClientPlayerEntity) {

            if (GoldenAgeCombat.SWORD_BLOCKING.isEnabled() && SwordBlockingElement.isActiveItemStackBlocking((PlayerEntity) entityIn)) {

                LegacyAnimationsElement element = (LegacyAnimationsElement) GoldenAgeCombat.LEGACY_ANIMATIONS;
                boolean renderModernPose = !element.isEnabled() || !element.oldBlockingPose;
                if (entityIn.getActiveHand() == Hand.OFF_HAND) {

                    this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX - ((float) Math.PI * 2.0F) / 10.0F;
                    if (renderModernPose) {

                        this.bipedLeftArm.rotateAngleY = ((float) Math.PI / 6.0F);
                    }
                } else {

                    this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX - ((float) Math.PI * 2.0F) / 10.0F;
                    if (renderModernPose) {

                        this.bipedRightArm.rotateAngleY = ((float) -Math.PI / 6.0F);
                    }
                }
            }
        }
    }
    
}
