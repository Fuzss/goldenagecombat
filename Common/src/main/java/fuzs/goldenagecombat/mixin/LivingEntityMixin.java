package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 180.0F), slice = @Slice(to = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;attackAnim:F")))
    public float tick(float oldRotation) {
        // before 1.12 the player's body would tilt to the side when walking backwards, now it remains straight
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).classic.backwardsWalking) return oldRotation;
        return 0.0F;
    }

    @Inject(method = "blockedByShield", at = @At("HEAD"), cancellable = true)
    protected void blockedByShield(LivingEntity target, CallbackInfo callback) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.shieldKnockbackFix) return;
        this.knockback(0.5, target.getX() - this.getX(), target.getZ() - this.getZ());
        callback.cancel();
    }

    @Shadow
    public abstract void knockback(double p_147241_, double p_147242_, double p_147243_);

    @Inject(method = "isDamageSourceBlocked", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;getSourcePosition()Lnet/minecraft/world/phys/Vec3;", shift = At.Shift.BEFORE), cancellable = true)
    public void isDamageSourceBlocked(DamageSource damageSource, CallbackInfoReturnable<Boolean> callback) {
        if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) return;
        Vec3 sourcePosition = damageSource.getSourcePosition();
        Objects.requireNonNull(sourcePosition, "source position is null");
        Vec3 viewVector = this.getViewVector(1.0F);
        Vec3 vec3 = sourcePosition.vectorTo(this.position()).normalize();
        vec3 = new Vec3(vec3.x, 0.0, vec3.z);
        double protectionArc = -Math.cos(GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.shieldProtectionArc * Math.PI * 0.5 / 180.0);
        callback.setReturnValue(vec3.dot(viewVector) < protectionArc);
    }
}
