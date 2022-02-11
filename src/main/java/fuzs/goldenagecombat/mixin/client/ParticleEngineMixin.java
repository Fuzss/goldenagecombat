package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
    @Inject(method = "makeParticle", at = @At("HEAD"), cancellable = true)
    private <T extends ParticleOptions> void makeParticle(T p_107396_, double p_107397_, double p_107398_, double p_107399_, double p_107400_, double p_107401_, double p_107402_, CallbackInfoReturnable<Particle> callbackInfo) {
        if (GoldenAgeCombat.CONFIG.server().classic.canceledParticles.contains(p_107396_.getType())) callbackInfo.setReturnValue(null);
    }
}
