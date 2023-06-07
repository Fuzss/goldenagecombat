package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ClientConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getAttackAnim", at = @At("TAIL"), cancellable = true)
    public void getAttackAnim(float tickDelta, CallbackInfoReturnable<Float> callback) {
        if (!GoldenAgeCombat.CONFIG.get(ClientConfig.class).animations.swingAnimation) return;
        final float swingProgress = callback.getReturnValueF();
        if (swingProgress > 0.4F && swingProgress < 0.95F) {
            callback.setReturnValue(0.4F + 0.6F * (float) Math.pow((swingProgress - 0.4F) / 0.6F, 4.0));
        }
    }
}
