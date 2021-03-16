package com.fuzs.goldenagecombat.asm;

import com.fuzs.goldenagecombat.client.renderer.ThirdPersonBlockingRenderer;
import com.fuzs.goldenagecombat.client.renderer.entity.layers.RedArmorLayer;
import com.fuzs.goldenagecombat.config.CommonConfigHandler;
import com.fuzs.goldenagecombat.handler.ClassicCombatHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * this class is mostly used for redirecting hooks to their appropriate place
 */
@SuppressWarnings("unused")
public class Hooks {

    /**
     * allow critical strikes when the player is sprinting in {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem}
     */
    public static boolean allowCriticalSprinting(boolean isSprinting) {

        return !CommonConfigHandler.MORE_SPRINTING.get() && !isSprinting;
    }

    /**
     * change armor model to turn red on hit in net.minecraft.client.renderer.entity.layers.ArmorLayer#renderArmorPart
     */
    @OnlyIn(Dist.CLIENT)
    public static <T extends LivingEntity, M extends BipedModel<T>> void renderArmor(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean glintIn, M modelIn, float red, float green, float blue, ResourceLocation armorResource, T entity) {

        RedArmorLayer.renderArmor(matrixStackIn, bufferIn, packedLightIn, glintIn, modelIn, red, green, blue, armorResource, entity);
    }

    /**
     * make fishing bobber cause an attack when landing on a living entity in net.minecraft.entity.projectile.FishingBobberEntity#checkCollision
     */
    public static void onFishingBobberCollision(FishingBobberEntity bobber, PlayerEntity angler, Entity caughtEntity) {

        if (CommonConfigHandler.OLD_FISHING_ROD.get()) {

            ClassicCombatHandler.onFishingBobberCollision(bobber, angler, caughtEntity);
        }
    }

    /**
     * add slight upwards motion when pulling an entity in net.minecraft.entity.projectile.FishingBobberEntity#bringInHookedEntity
     */
    public static Vector3d getCaughtEntityMotion(Vector3d vec3d) {

        return CommonConfigHandler.OLD_FISHING_ROD.get() ? ClassicCombatHandler.getCaughtEntityMotion(vec3d) : vec3d;

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
