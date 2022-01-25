package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getPickRadius", at = @At("TAIL"), cancellable = true)
    public void getPickRadius(CallbackInfoReturnable<Float> callbackInfo) {
        if (GoldenAgeCombat.CONFIG.server().classic.inflateHitboxes) {
            callbackInfo.setReturnValue(0.1F);
        }
    }
}
