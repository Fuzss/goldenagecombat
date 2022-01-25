package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.handler.ClassicCombatHandler;
import fuzs.goldenagecombat.handler.CombatAdjustmentsHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @ModifyConstant(method = "hurt", constant = @Constant(floatValue = 0.0F))
    public float hurt$ignoredDamageAmount(float oldAmount) {
        // replaces value used for comparing what amount to ignore, but also damage amount for peaceful mode
        // both are necessary as the values are compared using '=='
        if (GoldenAgeCombat.CONFIG.server().classic.weakPlayerKnockback) {
            return Float.MIN_VALUE;
        }
        return oldAmount;
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