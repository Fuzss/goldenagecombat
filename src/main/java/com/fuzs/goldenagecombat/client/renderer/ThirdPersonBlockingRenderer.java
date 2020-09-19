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

    public static void applyRotations(BipedModel<LivingEntity> model, LivingEntity entity) {

        if (entity instanceof AbstractClientPlayerEntity) {

            if (BLOCKING_HELPER.isActiveItemStackBlocking((PlayerEntity) entity)) {

                boolean flag = ClientConfigHandler.BLOCKING_POSE.get() == ClientConfigHandler.BlockingPose.MODERN;
                if (entity.getActiveHand() == Hand.OFF_HAND) {

                    model.bipedLeftArm.rotateAngleX = model.bipedLeftArm.rotateAngleX - ((float) Math.PI * 2.0F) / 10F;
                    if (flag) model.bipedLeftArm.rotateAngleY = ((float)Math.PI / 6F);
                } else {

                    model.bipedRightArm.rotateAngleX = model.bipedRightArm.rotateAngleX - ((float) Math.PI * 2.0F) / 10F;
                    if (flag) model.bipedRightArm.rotateAngleY = (-(float)Math.PI / 6F);
                }
            }
        }
    }

}
