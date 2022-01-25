package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.client.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Input.class)
public abstract class InputMixin {
    @Shadow
    public float forwardImpulse;

    @Inject(method = "hasForwardImpulse", at = @At("HEAD"), cancellable = true)
    public void hasForwardImpulse(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (GoldenAgeCombat.CONFIG.server().classic.quickSlowdown) {
            callbackInfo.setReturnValue(this.forwardImpulse > 0.8F);
        }
    }
}
