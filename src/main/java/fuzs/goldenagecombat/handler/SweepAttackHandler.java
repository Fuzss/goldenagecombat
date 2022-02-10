package fuzs.goldenagecombat.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.registry.ModRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SweepAttackHandler {
    public static void attackAir(Player player) {
        double d0 = player.walkDist - player.walkDistO;
        if (!player.isOnGround() || !(d0 < player.getSpeed())) return;
        float attackDamage = (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        ClassicCombatHandler.disableCooldownPeriod(player);
        if (attackDamage > 0.0f && player.getAttackStrengthScale(0.5F) > 0.9F && checkSweepAttack(player)) {
            float attackReach = (float) getCurrentAttackReach(player);
            double moveX = (double)(-Mth.sin(player.getYRot() * ((float)Math.PI / 180))) * 2.0;
            double moveZ = (double)Mth.cos(player.getYRot() * ((float)Math.PI / 180)) * 2.0;
            AABB aABB = player.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(player, player).move(moveX, 0.0, moveZ);
            sweepAttack(player, aABB, attackReach, attackDamage, null);
        }
        // also resets attack ticker
        player.swing(InteractionHand.MAIN_HAND);
    }

    public static void sweepAttack(Player player, AABB aABB, float currentAttackReach, float baseAttackDamage, @Nullable Entity target) {
        float h = 1.0f + EnchantmentHelper.getSweepingDamageRatio(player) * baseAttackDamage;
        List<LivingEntity> list = player.level.getEntitiesOfClass(LivingEntity.class, aABB);
        for (LivingEntity livingEntity : list) {
            if (livingEntity == player || livingEntity == target || player.isAlliedTo(livingEntity) || livingEntity instanceof ArmorStand && ((ArmorStand)livingEntity).isMarker()) continue;
            float i = currentAttackReach + livingEntity.getBbWidth() * 0.5f;
            if (!(player.distanceToSqr(livingEntity) < (double)(i * i))) continue;
            livingEntity.knockback(0.4f, Mth.sin(player.getYRot() * ((float)Math.PI / 180)), -Mth.cos(player.getYRot() * ((float)Math.PI / 180)));
            livingEntity.hurt(DamageSource.playerAttack(player), h);
        }
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0f, 1.0f);
        if (player.level instanceof ServerLevel) {
            double d = -Mth.sin(player.getYRot() * ((float)Math.PI / 180));
            double i = Mth.cos(player.getYRot() * ((float)Math.PI / 180));
            ((ServerLevel)player.level).sendParticles(ParticleTypes.SWEEP_ATTACK, player.getX() + d, player.getY() + (double)player.getBbHeight() * 0.5, player.getZ() + i, 0, d, 0.0, i, 0.0);
        }
    }

    public static boolean checkSweepAttack(Player player) {
        if (GoldenAgeCombat.CONFIG.server().combatTests.noSneakSweeping && player.isShiftKeyDown()) {
            return false;
        }
        if (GoldenAgeCombat.CONFIG.server().combatTests.sweepingRequired) {
            return EnchantmentHelper.getSweepingDamageRatio(player) > 0.0f;
        }
        return player.getItemInHand(InteractionHand.MAIN_HAND).canPerformAction(ToolActions.SWORD_SWEEP);
    }

    public static double getCurrentAttackReach(Player player) {
        if (!GoldenAgeCombat.CONFIG.server().attributes.attackReach) return 3.0;
        double attackReach = player.getAttribute(ModRegistry.ATTACK_REACH_ATTRIBUTE.get()).getValue();
        if (player.isCreative()) {
            attackReach += 0.5;
        }
        if (player.isCrouching()) {
            attackReach -= 0.5;
        }
        return attackReach;
    }
}
