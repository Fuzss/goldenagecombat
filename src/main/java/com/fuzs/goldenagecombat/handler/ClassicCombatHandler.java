package com.fuzs.goldenagecombat.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;

public class ClassicCombatHandler {

    public static void onFishingBobberCollision(FishingBobberEntity bobber, PlayerEntity angler, Entity caughtEntity) {

        if (caughtEntity instanceof LivingEntity) {

            caughtEntity.attackEntityFrom(DamageSource.causeThrownDamage(bobber, angler), 0.0F);
        }
    }

    public static Vector3d getCaughtEntityMotion(Vector3d vec3d) {

        double x = vec3d.getX() * 10.0, y = vec3d.getY() * 10.0, z = vec3d.getZ() * 10.0;
        return vec3d.add(0.0, Math.pow(x * x + y * y + z * z, 0.25) * 0.08, 0.0);
    }

}
