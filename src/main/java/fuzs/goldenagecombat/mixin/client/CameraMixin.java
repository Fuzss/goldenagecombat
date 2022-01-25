package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private Entity entity;
    @Shadow
    private float eyeHeight;
    @Shadow
    private float eyeHeightOld;

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo callbackInfo) {
        if (GoldenAgeCombat.CONFIG.client().animations.instantEyeHeight) {
            if (this.entity != null) {
                this.eyeHeightOld = this.eyeHeight = this.entity.getEyeHeight();
            }
        }
    }
}
