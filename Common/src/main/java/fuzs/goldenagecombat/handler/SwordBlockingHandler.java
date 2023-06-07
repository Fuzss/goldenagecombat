package fuzs.goldenagecombat.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.goldenagecombat.init.ModRegistry;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SwordBlockingHandler {

    public static EventResultHolder<InteractionResultHolder<ItemStack>> onUseItem(Player player, Level level, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (canItemStackBlock(itemInHand)) {
            if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).blocking.prioritizeShield || hand != InteractionHand.MAIN_HAND || player.getOffhandItem().getUseAnimation() != UseAnim.BLOCK) {
                player.startUsingItem(hand);
                // cause reequip animation, but don't swing hand, not to be confused with ActionResultType#SUCCESS
                // partial version seems to not affect game stats which is probably better since you can just spam sword blocking haha
                return EventResultHolder.interrupt(InteractionResultHolder.consume(itemInHand));
            }
        }
        return EventResultHolder.pass();
    }

    public static EventResult onUseItemStart(LivingEntity entity, ItemStack stack, MutableInt remainingUseDuration) {
        if (entity instanceof Player && canItemStackBlock(stack)) remainingUseDuration.accept(72000);
        return EventResult.PASS;
    }

    public static EventResult onLivingHurt(LivingEntity entity, DamageSource source, MutableFloat amount) {
        if (entity instanceof Player player) {
            if (isDamageSourceBlockable(source, player) && amount.getAsFloat() > 0.0F) {
                amount.mapFloat(f -> (1.0F + f) * 0.5F);
            }
        }
        return EventResult.PASS;
    }

    public static EventResult onLivingKnockBack(LivingEntity entity, DefaultedDouble strength, DefaultedDouble ratioX, DefaultedDouble ratioZ) {
        if (entity instanceof Player player && isActiveItemStackBlocking(player)) {
            float knockBackMultiplier = 1.0F - (float) GoldenAgeCombat.CONFIG.get(ServerConfig.class).blocking.knockbackReduction;
            if (knockBackMultiplier == 0.0F) {
                return EventResult.INTERRUPT;
            } else {
                strength.mapDouble(v -> v * knockBackMultiplier);
            }
        }
        return EventResult.PASS;
    }

    private static boolean isDamageSourceBlockable(DamageSource source, Player player) {
        Entity entity = source.getDirectEntity();
        if (entity instanceof AbstractArrow arrow) {
            if (arrow.getPierceLevel() > 0) {
                return false;
            }
        }
        if (!source.is(DamageTypeTags.BYPASSES_ARMOR) && isActiveItemStackBlocking(player)) {
            Vec3 position = source.getSourcePosition();
            if (position != null) {
                Vec3 viewVector = player.getViewVector(1.0F);
                position = position.vectorTo(player.position()).normalize();
                position = new Vec3(position.x, 0.0, position.z);
                return position.dot(viewVector) < -Math.cos(GoldenAgeCombat.CONFIG.get(ServerConfig.class).blocking.protectionArc * Math.PI * 0.5 / 180.0);
            }
        }
        return false;
    }

    public static boolean isActiveItemStackBlocking(Player player) {
        return player.isUsingItem() && canItemStackBlock(player.getUseItem());
    }

    public static boolean canItemStackBlock(ItemStack stack) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).blocking.allowBlocking) return false;
        if (stack.is(ModRegistry.SWORD_BLOCKING_EXCLUSIONS_TAG)) {
            return false;
        } else if (stack.getItem() instanceof SwordItem) {
            return true;
        } else {
            return stack.is(ModRegistry.SWORD_BLOCKING_INCLUSIONS_TAG);
        }
    }
}
