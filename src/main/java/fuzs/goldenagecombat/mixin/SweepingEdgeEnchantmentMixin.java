package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SweepingEdgeEnchantment.class)
public abstract class SweepingEdgeEnchantmentMixin {
    @Inject(method = "getSweepingDamageRatio", at = @At("TAIL"), cancellable = true)
    private static void getSweepingDamageRatio(int level, CallbackInfoReturnable<Float> callbackInfo) {
        if (GoldenAgeCombat.CONFIG.server().adjustments.halfSweepingDamage) {
            callbackInfo.setReturnValue(callbackInfo.getReturnValueF() * 0.5F);
        }
    }
}
