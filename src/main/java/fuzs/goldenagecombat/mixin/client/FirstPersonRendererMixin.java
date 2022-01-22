package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.handler.ClassicCombatHandler;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(FirstPersonRenderer.class)
public abstract class FirstPersonRendererMixin {

    @Inject(method = "resetEquippedProgress", at = @At("HEAD"), cancellable = true)
    public void resetEquippedProgress(Hand hand, CallbackInfo callbackInfo) {

        ClassicCombatHandler element = (ClassicCombatHandler) GoldenAgeCombat.CLASSIC_COMBAT;
        if (element.isEnabled() && element.removeCooldown) {

            callbackInfo.cancel();
        }
    }

}
