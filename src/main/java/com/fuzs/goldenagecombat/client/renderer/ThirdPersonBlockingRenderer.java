package com.fuzs.goldenagecombat.client.renderer;

import com.fuzs.goldenagecombat.client.config.ClientConfigHandler;
import com.fuzs.goldenagecombat.util.BlockingItemHelper;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThirdPersonBlockingRenderer {

    private static final BlockingItemHelper BLOCKING_HELPER = new BlockingItemHelper();

    public static void applyRotations(BipedModel<LivingEntity> entityModel, LivingEntity entity) {

        if (entity instanceof AbstractClientPlayerEntity) {

            if (BLOCKING_HELPER.isActiveItemStackBlocking((PlayerEntity) entity)) {

                boolean isModernPose = ClientConfigHandler.BLOCKING_POSE.get() == ClientConfigHandler.BlockingPose.MODERN;
                if (entity.getActiveHand() == Hand.OFF_HAND) {

                    entityModel.bipedLeftArm.rotateAngleX = entityModel.bipedLeftArm.rotateAngleX - ((float) Math.PI * 2.0F) / 10.0F;
                    if (isModernPose) {

                        entityModel.bipedLeftArm.rotateAngleY = ((float) Math.PI / 6.0F);
                    }
                } else {

                    entityModel.bipedRightArm.rotateAngleX = entityModel.bipedRightArm.rotateAngleX - ((float) Math.PI * 2.0F) / 10.0F;
                    if (isModernPose) {

                        entityModel.bipedRightArm.rotateAngleY = ((float) -Math.PI / 6.0F);
                    }
                }
            }
        }
    }

}
