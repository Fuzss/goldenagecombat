package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PlayerMixin extends LivingEntity {
    @Unique
    private boolean combatnouveau$sprintsDuringAttack;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hurt", at = @At(value = "RETURN", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;scalesWithDifficulty()Z")), cancellable = true)
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).classic.weakAttacksKnockBackPlayers) return;
        if (amount == 0.0F && this.level().getDifficulty() != Difficulty.PEACEFUL) {
            callback.setReturnValue(super.hurt(source, amount));
        }
    }

    @Inject(method = "attack", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;fallDistance:F"))
    public void attack$0(Entity target, CallbackInfo callback) {
        this.combatnouveau$sprintsDuringAttack = this.isSprinting();
        // allow landing critical hits when sprint jumping like before 1.9 and in combat test snapshots
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).classic.criticalHitsWhileSprinting) {
            this.setSharedFlag(3, false);
        }
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z", shift = At.Shift.AFTER))
    public void attack$1(Entity target, CallbackInfo callback) {
        if (this.combatnouveau$sprintsDuringAttack) this.setSharedFlag(3, true);
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V", shift = At.Shift.AFTER))
    public void attack$2(Entity target, CallbackInfo callback) {
        // don't disable sprinting when attacking a target
        // this is mainly nice to have since you always stop to swim when attacking creatures underwater
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.sprintAttacks) {
            if (this.combatnouveau$sprintsDuringAttack) this.setSprinting(true);
        }
        this.combatnouveau$sprintsDuringAttack = false;
    }

    @ModifyVariable(method = "attack", at = @At("LOAD"), ordinal = 3, slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;sweepAttack()V")))
    public boolean attack$3(boolean triggerSweepAttack, Entity target) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.requireSweepingEdge) return triggerSweepAttack;
        return triggerSweepAttack && EnchantmentHelper.getSweepingDamageRatio(Player.class.cast(this)) > 0.0F;
    }

    @Inject(method = "getAttackStrengthScale", at = @At("HEAD"), cancellable = true)
    public void getAttackStrengthScale(float adjustTicks, CallbackInfoReturnable<Float> callback) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).classic.removeAttackCooldown) return;
        callback.setReturnValue(1.0F);
    }
}