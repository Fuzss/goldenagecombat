package fuzs.goldenagecombat.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.CommonConfig;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(method = "hurtServer",
                       at = @At("RETURN"),
                       slice = @Slice(from = @At(value = "INVOKE",
                                                 target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V")))
    public boolean hurtServer(boolean hurtServer, ServerLevel serverLevel, DamageSource damageSource, float damageAmount) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).weakAttackKnockBack) {
            return hurtServer;
        }

        if (!hurtServer && damageAmount == 0.0F && this.level().getDifficulty() != Difficulty.PEACEFUL) {
            return super.hurtServer(serverLevel, damageSource, damageAmount);
        } else {
            return hurtServer;
        }
    }

    @ModifyExpressionValue(method = "canCriticalAttack",
                           at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"))
    private boolean canCriticalAttack(boolean isSprinting, Entity entity) {
        // Allow landing critical hits when sprint jumping like before Minecraft 1.9 and in Combat Test snapshots.
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).criticalHitsWhileSprinting) {
            return false;
        } else {
            return isSprinting;
        }
    }

    @WrapWithCondition(method = "causeExtraKnockback",
                       at = @At(value = "INVOKE",
                                target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V"))
    public boolean causeExtraKnockback(Player player, boolean isSprinting) {
        // Don't disable sprinting when attacking a target.
        // This is mainly nice to have since you always stop to swim when attacking creatures underwater.
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).sprintAttacks) {
            return false;
        } else {
            return isSprinting;
        }
    }

    @ModifyReturnValue(method = "isSweepAttack", at = @At(value = "RETURN", ordinal = 0))
    public boolean isSweepAttack(boolean isSweepAttack) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).requireSweepingEdge) {
            return isSweepAttack;
        }

        return isSweepAttack && this.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) > 0.0;
    }

    @Inject(method = {"getAttackStrengthScale", "getItemSwapScale"}, at = @At("HEAD"), cancellable = true)
    public void getAttackStrengthScale(float adjustTicks, CallbackInfoReturnable<Float> callback) {
        if (!GoldenAgeCombat.CONFIG.get(CommonConfig.class).removeAttackCooldown) {
            return;
        }

        callback.setReturnValue(1.0F);
    }
}
