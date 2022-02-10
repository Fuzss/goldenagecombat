package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 180.0F), slice = @Slice(to = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;attackAnim:F")))
    public float tick$backwardsRotation(float oldRotation) {
        // before 1.12 the player's body would tilt to the side when walking backwards, now it remains straight
        if (!GoldenAgeCombat.CONFIG.server().classic.backwardsWalking) return oldRotation;
        return 0.0F;
    }

    @Inject(method = "blockedByShield", at = @At("HEAD"), cancellable = true)
    protected void blockedByShield$head(LivingEntity target, CallbackInfo callbackInfo) {
        if (!GoldenAgeCombat.CONFIG.server().adjustments.shieldKnockbackFix) return;
        this.knockback(0.5, target.getX() - this.getX(), target.getZ() - this.getZ());
        callbackInfo.cancel();
    }

    @Shadow
    public abstract void knockback(double p_147241_, double p_147242_, double p_147243_);
}
