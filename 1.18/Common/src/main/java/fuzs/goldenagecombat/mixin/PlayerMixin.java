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
    private boolean goldenagecombat$sprintsDuringAttack;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hurt", at = @At(value = "RETURN", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;scalesWithDifficulty()Z")), cancellable = true)
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).weakAttacksKnockBackPlayers) return;
        if (amount == 0.0F && this.level.getDifficulty() != Difficulty.PEACEFUL) {
            callback.setReturnValue(super.hurt(source, amount));
        }
    }

    @Inject(method = "attack", at = @At("HEAD"))
    public void attack$0(Entity target, CallbackInfo callback) {
        this.goldenagecombat$sprintsDuringAttack = this.isSprinting();
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", ordinal = 0, shift = At.Shift.AFTER))
    public void attack$1(Entity target, CallbackInfo callback) {
        // allow landing critical hits when sprint jumping like before 1.9 and in combat test snapshots
        // the injection point is fine despite being inside a few conditions as the same conditions must apply for critical hits
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).criticalHitsWhileSprinting) {
            // this disables sprinting, no need to call the dedicated method as it also updates the attribute modifier which is unnecessary since we reset the value anyway
            this.setSharedFlag(3, false);
        }
    }

    @Inject(method = "attack", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;walkDist:F"))
    public void attack$2(Entity target, CallbackInfo callback) {
        // reset to original sprinting value for rest of attack method
        if (this.goldenagecombat$sprintsDuringAttack) this.setSharedFlag(3, true);
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V", shift = At.Shift.AFTER))
    public void attack$3(Entity target, CallbackInfo callback) {
        // don't disable sprinting when attacking a target
        // this is mainly nice to have since you always stop to swim when attacking creatures underwater
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).sprintAttacks) {
            if (this.goldenagecombat$sprintsDuringAttack) this.setSprinting(true);
        }
        this.goldenagecombat$sprintsDuringAttack = false;
    }

    @ModifyVariable(method = "attack", at = @At("LOAD"), ordinal = 3, slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;sweepAttack()V")))
    public boolean attack$4(boolean triggerSweepAttack, Entity target) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).requireSweepingEdge) return triggerSweepAttack;
        return triggerSweepAttack && EnchantmentHelper.getSweepingDamageRatio(Player.class.cast(this)) > 0.0F;
    }

    @Inject(method = "getAttackStrengthScale", at = @At("HEAD"), cancellable = true)
    public void getAttackStrengthScale(float adjustTicks, CallbackInfoReturnable<Float> callback) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).removeAttackCooldown) return;
        callback.setReturnValue(1.0F);
    }
}