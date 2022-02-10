package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.handler.SweepAttackHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
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

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;fallDistance:F")))
    public boolean attack$isSprinting(Player player) {
        // allow landing critical hits when sprint jumping like before 1.9 and in combat test snapshots
        if (!GoldenAgeCombat.CONFIG.server().classic.criticalHitsSprinting) return player.isSprinting();
        return false;
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V"))
    public void attack$setSprinting(Player player, boolean oldValue) {
        // don't disable sprinting when attacking a target
        // this is mainly nice to have since you always stop to swim when attacking creatures underwater
        if (!GoldenAgeCombat.CONFIG.server().combatTests.sprintAttacks) {
            player.setSprinting(oldValue);
        }
    }

    @ModifyVariable(method = "attack", at = @At("LOAD"), ordinal = 5, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V")))
    public float attack$damageDealt(float damageDealt) {
        // hide dealt damage heart particles, since they are much better suited for a slow combat system, but are just annoying with a fast-paced one
        if (!GoldenAgeCombat.CONFIG.server().classic.noDamageIndicators) return damageDealt;
        return 0.0F;
    }

    @ModifyVariable(method = "attack", at = @At("LOAD"), ordinal = 3, slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;sweepAttack()V")))
    public boolean attack$triggerSweepAttack(boolean triggerSweepAttack, Entity target) {
        return triggerSweepAttack && SweepAttackHandler.checkSweepAttack((Player) (Object) this);
    }
}