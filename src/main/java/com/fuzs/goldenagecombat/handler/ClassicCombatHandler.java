package com.fuzs.goldenagecombat.handler;

import com.fuzs.goldenagecombat.config.CommonConfigHandler;
import com.google.common.collect.Sets;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;

public class ClassicCombatHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent evt) {

        // disable cooldown right before every attack
        if (CommonConfigHandler.REMOVE_ATTACK_COOLDOWN.get()) {

            evt.getPlayer().ticksSinceLastSwing = (int) Math.ceil(evt.getPlayer().getCooldownPeriod());
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onCriticalHit(final CriticalHitEvent evt) {

        // prevent sweeping from taking effect unless the enchantment is in place, onGround flag is reset next tick anyways
        if (CommonConfigHandler.SWEEPING_REQUIRED.get() && EnchantmentHelper.getSweepingDamageRatio(evt.getPlayer()) == 0.0F) {

            evt.getPlayer().onGround = false;
        }
    }

    public static float addEnchantmentDamage(PlayerEntity player) {

        // every level of sharpness adds 1.25 attack damage again
        int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, player.getHeldItemMainhand());
        if (sharpness > 0) {

            return -0.5F + sharpness * 0.75F;
        }

        return 0;
    }

    public static void onFishingBobberCollision(FishingBobberEntity bobber, PlayerEntity angler, Entity caughtEntity) {

        if (caughtEntity instanceof LivingEntity) {

            caughtEntity.attackEntityFrom(DamageSource.causeThrownDamage(bobber, angler), 0.0F);
        }
    }

    public static Vec3d getCaughtEntityMotion(Vec3d vec3d) {

        double x = vec3d.getX() * 10.0, y = vec3d.getY() * 10.0, z = vec3d.getZ() * 10.0;
        return vec3d.add(0.0, Math.pow(x * x + y * y + z * z, 0.25) * 0.08, 0.0);
    }

}
