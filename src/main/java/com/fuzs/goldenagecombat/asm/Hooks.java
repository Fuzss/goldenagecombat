package com.fuzs.goldenagecombat.asm;

import com.fuzs.goldenagecombat.client.renderer.ThirdPersonBlockingRenderer;
import com.fuzs.goldenagecombat.client.renderer.entity.layers.RedArmorLayer;
import com.fuzs.goldenagecombat.config.CommonConfigHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * this class is mostly used for redirecting hooks to their appropriate place
 */
@SuppressWarnings("unused")
public class Hooks {

    /**
     * change armor model to turn red on hit in net.minecraft.client.renderer.entity.layers.ArmorLayer#renderArmorPart
     */
    @OnlyIn(Dist.CLIENT)
    public static <T extends LivingEntity, M extends BipedModel<T>> void renderArmor(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean glintIn, M modelIn, float red, float green, float blue, ResourceLocation armorResource, T entity) {

        RedArmorLayer.renderArmor(matrixStackIn, bufferIn, packedLightIn, glintIn, modelIn, red, green, blue, armorResource, entity);
    }

    /**
     * set arm rotation angle when blocking
     */
    @OnlyIn(Dist.CLIENT)
    public static void applyRotations(BipedModel<LivingEntity> model, LivingEntity entity) {

        if (CommonConfigHandler.BLOCKING.get()) {

            ThirdPersonBlockingRenderer.applyRotations(model, entity);
        }
    }

}
