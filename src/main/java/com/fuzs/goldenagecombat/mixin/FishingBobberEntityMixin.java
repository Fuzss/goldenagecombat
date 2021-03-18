package com.fuzs.goldenagecombat.mixin;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.element.ClassicCombatElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends Entity {

    @Shadow
    @Final
    private PlayerEntity angler;
    @Shadow
    public Entity caughtEntity;

    public FishingBobberEntityMixin(EntityType<?> entityTypeIn, World worldIn) {

        super(entityTypeIn, worldIn);
    }

    @Inject(method = "checkCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;setHookedEntity()V"))
    protected void checkCollision(CallbackInfo callbackInfo) {

        ClassicCombatElement element = (ClassicCombatElement) GoldenAgeCombat.CLASSIC_COMBAT;
        if (element.isEnabled() && element.rodKnockback && this.caughtEntity != null) {

            // won't really do anything as attacks with an amount of 0 are ignored, this is patched elsewhere
            this.caughtEntity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.angler), 0.0F);
        }
    }

    @Redirect(method = "bringInHookedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getMotion()Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d getMotion(Entity entity) {

        Vec3d motion = entity.getMotion();
        ClassicCombatElement element = (ClassicCombatElement) GoldenAgeCombat.CLASSIC_COMBAT;
        if (element.isEnabled() && element.rodLaunch) {

            // values taken from Minecraft 1.8
            double x = motion.getX() * 10.0, y = motion.getY() * 10.0, z = motion.getZ() * 10.0;
            return motion.add(0.0, Math.pow(x * x + y * y + z * z, 0.25) * 0.08, 0.0);
        }
        
        return motion;
    }

}
