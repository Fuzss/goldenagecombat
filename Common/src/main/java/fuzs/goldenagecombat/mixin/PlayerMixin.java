package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.handler.SweepAttackHandler;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hurt", at = @At(value = "RETURN", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;scalesWithDifficulty()Z")), cancellable = true)
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).classic.weakPlayerKnockback) return;
        if (amount == 0.0F && this.level.getDifficulty() != Difficulty.PEACEFUL) {
            callback.setReturnValue(super.hurt(source, amount));
        }
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;fallDistance:F")))
    public boolean attack$0(Player player) {
        // allow landing critical hits when sprint jumping like before 1.9 and in combat test snapshots
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).classic.criticalHitsSprinting) return player.isSprinting();
        return false;
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V"))
    public void attack$1(Player player, boolean isSprinting) {
        // don't disable sprinting when attacking a target
        // this is mainly nice to have since you always stop to swim when attacking creatures underwater
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.sprintAttacks) {
            player.setSprinting(isSprinting);
        }
    }

    @ModifyVariable(method = "attack", at = @At("LOAD"), ordinal = 3, slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;sweepAttack()V")))
    public boolean attack$2(boolean triggerSweepAttack, Entity target) {
        return triggerSweepAttack && SweepAttackHandler.checkSweepAttack(Player.class.cast(this));
    }

    @Inject(method = "getAttackStrengthScale", at = @At("HEAD"), cancellable = true)
    public void getAttackStrengthScale(float adjustTicks, CallbackInfoReturnable<Float> callback) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).classic.removeCooldown) return;
        callback.setReturnValue(1.0F);
    }
}