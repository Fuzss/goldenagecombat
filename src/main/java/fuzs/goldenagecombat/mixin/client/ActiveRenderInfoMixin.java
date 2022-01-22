package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.client.element.LegacyAnimationsRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
@Mixin(ActiveRenderInfo.class)
public abstract class ActiveRenderInfoMixin {

    @Shadow
    private Entity renderViewEntity;
    @Shadow
    private float height;
    @Shadow
    private float previousHeight;

    @Inject(method = "interpolateHeight", at = @At("TAIL"))
    public void interpolateHeight(CallbackInfo callbackInfo) {

        LegacyAnimationsRenderer element = (LegacyAnimationsRenderer) GoldenAgeCombat.LEGACY_ANIMATIONS;
        if (element.isEnabled() && element.instantEyeHeight) {

            if (this.renderViewEntity != null) {

                this.previousHeight = this.height = this.renderViewEntity.getEyeHeight();
            }
        }

    }

}
