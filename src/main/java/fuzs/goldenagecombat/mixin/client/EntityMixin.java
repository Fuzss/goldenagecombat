package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.handler.ClassicCombatHandler;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(Entity.class)
public abstract class EntityMixin extends CapabilityProvider<Entity> {

    protected EntityMixin(Class<Entity> baseClass) {

        super(baseClass);
    }

    @Inject(method = "getCollisionBorderSize", at = @At("TAIL"), cancellable = true)
    public void getCollisionBorderSize(CallbackInfoReturnable<Float> callbackInfo) {

        ClassicCombatHandler element = (ClassicCombatHandler) GoldenAgeCombat.CLASSIC_COMBAT;
        if (element.isEnabled() && element.extension.inflateHitboxes) {

            callbackInfo.setReturnValue(0.1F);
        }
    }

}
