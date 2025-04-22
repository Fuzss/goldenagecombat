package fuzs.goldenagecombat.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ClassicCombatHandler {

    public static EventResult onProjectileImpact(Projectile projectile, HitResult hitResult) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).weakAttacksKnockBackPlayers) return EventResult.PASS;
        if (hitResult.getType() == HitResult.Type.ENTITY && projectile.getOwner() == null) {
            // enable knockback for item projectiles fired from dispensers by making shooter not be null
            // something similar is already done in AbstractArrowEntity::onEntityHit to account for arrows fired from dispensers
            projectile.setOwner(projectile);
        }
        return EventResult.PASS;
    }

    public static void onUseItemFinish(LivingEntity livingEntity, MutableValue<ItemStack> itemStack, ItemStack originalItemStack) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).goldenAppleEffects) return;
        if (itemStack.get().getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
            livingEntity.removeEffect(MobEffects.ABSORPTION);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 4));
        }
    }

    public static EventResult onLivingKnockBack(LivingEntity livingEntity, MutableDouble knockbackStrength, MutableDouble ratioX, MutableDouble ratioZ) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).upwardsKnockback) return EventResult.PASS;
        if (!livingEntity.onGround() && !livingEntity.isInWater()) {
            knockbackStrength.mapDouble((double v) -> v *
                    (1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
            final Vec3 deltaMovement = livingEntity.getDeltaMovement();
            livingEntity.setDeltaMovement(deltaMovement.x,
                    Math.min(0.4, deltaMovement.y / 2.0 + knockbackStrength.getAsDouble()),
                    deltaMovement.x);
        }
        return EventResult.PASS;
    }

    public static EventResult onPlaySoundAtPosition(Level level, Vec3 position, MutableValue<Holder<SoundEvent>> soundEvent, MutableValue<SoundSource> soundSource, MutableFloat soundVolume, MutableFloat soundPitch) {
        // disable combat update player attack sounds
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).canceledAttackSounds.contains(soundEvent.get().value())) {
            return EventResult.PASS;
        } else {
            return EventResult.INTERRUPT;
        }
    }
}
