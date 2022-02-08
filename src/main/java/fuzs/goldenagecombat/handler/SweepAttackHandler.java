package fuzs.goldenagecombat.handler;

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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SweepAttackHandler {
//    public static void attackAir(Player player) {
//        if (!player.isAttackAvailable(1.0f)) {
//            return;
//        }
//        player.swing(InteractionHand.MAIN_HAND);
//        float f = (float)player.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
//        if (f > 0.0f && player.checkSweepAttack()) {
//            float g = player.getCurrentAttackReach(1.0f);
//            double d = 2.0;
//            double e = (double)(-Mth.sin(player.getYRot() * ((float)Math.PI / 180))) * 2.0;
//            double h = (double)Mth.cos(player.getYRot() * ((float)Math.PI / 180)) * 2.0;
//            AABB aABB = player.getBoundingBox().inflate(1.0, 0.25, 1.0).move(e, 0.0, h);
//            sweepAttack(player, aABB, g, f, null);
//        }
//        player.resetAttackStrengthTicker(false);
//    }

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
}
