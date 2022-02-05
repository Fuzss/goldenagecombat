package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "getAttackAnim", at = @At("TAIL"), cancellable = true)
    public void getAttackAnim(float partialTicks, CallbackInfoReturnable<Float> callbackInfo) {
        if (!GoldenAgeCombat.CONFIG.client().animations.swingAnimation) return;
        final float swingProgress = callbackInfo.getReturnValueF();
        if (swingProgress > 0.4F && swingProgress < 0.95F) {
            callbackInfo.setReturnValue(0.4F + 0.6F * (float) Math.pow((swingProgress - 0.4F) / 0.6F, 4.0));
        } else {
            callbackInfo.setReturnValue(swingProgress);
        }
    }
}
