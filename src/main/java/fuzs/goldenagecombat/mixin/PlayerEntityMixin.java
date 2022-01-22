package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.handler.ClassicCombatHandler;
import fuzs.goldenagecombat.handler.CombatAdjustmentsHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@SuppressWarnings("unused")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World worldIn) {

        super(type, worldIn);
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(floatValue = 0.0F))
    public float getIgnoredDamageAmount(float amount) {

        ClassicCombatHandler element = (ClassicCombatHandler) GoldenAgeCombat.CLASSIC_COMBAT;
        if (element.isEnabled() && element.weakPlayerKnockback) {

            return Float.MIN_VALUE;
        }

        return amount;
    }

    @Redirect(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSprinting()Z"), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;fallDistance:F")))
    public boolean allowCriticalSprinting(PlayerEntity player) {

        ClassicCombatHandler element = (ClassicCombatHandler) GoldenAgeCombat.CLASSIC_COMBAT;
        return (!element.isEnabled() || !element.criticalSprinting) && player.isSprinting();
    }

    @Redirect(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/server/ServerWorld;spawnParticle(Lnet/minecraft/particles/IParticleData;DDDIDDDD)I"))
    public <T extends IParticleData> int spawnParticle(ServerWorld world, T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {

        CombatAdjustmentsHandler element = (CombatAdjustmentsHandler) GoldenAgeCombat.COMBAT_ADJUSTMENTS;
        if (!element.isEnabled() || !element.noDamageIndicators) {

            world.spawnParticle(type, posX, posY, posZ, particleCount, xOffset, yOffset, zOffset, speed);
        }

        return 0;
    }

}