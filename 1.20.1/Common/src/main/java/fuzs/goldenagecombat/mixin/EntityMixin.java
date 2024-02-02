package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
abstract class EntityMixin {

    @Inject(method = "getPickRadius", at = @At("HEAD"), cancellable = true)
    public void getPickRadius(CallbackInfoReturnable<Float> callback) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).inflateHitboxes) return;
        callback.setReturnValue(0.1F);
    }
}
