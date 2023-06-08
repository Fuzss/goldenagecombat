package fuzs.goldenagecombat.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.goldenagecombat.core.CommonAbstractions;
import fuzs.goldenagecombat.init.ModRegistry;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SwordBlockingHandler {
    private static final int DEFAULT_ITEM_USE_DURATION = 72_000;

    public static EventResultHolder<InteractionResultHolder<ItemStack>> onUseItem(Player player, Level level, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (canItemStackBlock(itemInHand)) {
            if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).blocking.prioritizeOffHand || hand != InteractionHand.MAIN_HAND || canActivateBlocking(player, player.getOffhandItem())) {
                InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).blocking.requireBothHands || player.getItemInHand(otherHand).isEmpty()) {
                    player.startUsingItem(hand);
                    // cause reequip animation, but don't swing hand, not to be confused with InteractionResult#SUCCESS; this is also what shields to
                    return EventResultHolder.interrupt(InteractionResultHolder.consume(itemInHand));
                }
            }
        }
        return EventResultHolder.pass();
    }

    public static EventResult onUseItemStart(LivingEntity entity, ItemStack stack, MutableInt remainingUseDuration) {
        if (entity instanceof Player && canItemStackBlock(stack)) remainingUseDuration.accept(DEFAULT_ITEM_USE_DURATION);
        return EventResult.PASS;
    }

    public static EventResult onLivingAttack(LivingEntity entity, DamageSource damageSource, float damageAmount) {

        EventResult result = EventResult.PASS;
        if (!entity.level.isClientSide && entity instanceof Player player) {

            if (isActiveItemStackBlocking(player)) {

                if (DEFAULT_ITEM_USE_DURATION - player.getUseItemRemainingTicks() < GoldenAgeCombat.CONFIG.get(ServerConfig.class).blocking.parryWindow) {

                    if (damageAmount > 0.0F && canBlockDamageSource(player, damageSource)) {

                        result = EventResult.INTERRUPT;
                        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).blocking.damageSwordOnParry) {

                            hurtCurrentlyUsedSword(player, damageAmount);
                        }

                        if (!damageSource.is(DamageTypeTags.IS_PROJECTILE)) {

                            if (damageSource.getDirectEntity() instanceof LivingEntity directEntity) {

                                directEntity.knockback(0.5F, player.getX() - directEntity.getX(), player.getZ() - directEntity.getZ());
                            }
                        }

                        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModRegistry.ITEM_SWORD_BLOCK_SOUND_EVENT.get(), player.getSoundSource(), 1.0F, 0.8F + player.level.getRandom().nextFloat() * 0.4F);
                    }
                }

                if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).blocking.deflectProjectiles) {

                    if (damageSource.is(DamageTypeTags.IS_PROJECTILE) && damageSource.getDirectEntity() instanceof Projectile projectile) {

                        if (!(projectile instanceof AbstractArrow abstractArrow) || abstractArrow.getPierceLevel() == 0) {

                            projectile.hurt(player.damageSources().playerAttack(player), 0.0F);
                            projectile.setOwner(entity);
                            Vec3 lookAngle = entity.getLookAngle();
                            projectile.setDeltaMovement(lookAngle);
                            if (projectile instanceof AbstractHurtingProjectile hurtingProjectile) {
                                hurtingProjectile.xPower = lookAngle.x * 0.1;
                                hurtingProjectile.yPower = lookAngle.y * 0.1;
                                hurtingProjectile.zPower = lookAngle.z * 0.1;
                            }

                            result = EventResult.INTERRUPT;
                        }
                    }
                }
            }
        }

        return result;
    }

    public static EventResult onLivingHurt(LivingEntity entity, DamageSource source, MutableFloat amount) {
        if (entity instanceof Player player && isActiveItemStackBlocking(player)) {
            if (canBlockDamageSource(player, source) && amount.getAsFloat() > 0.0F) {
                if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).blocking.damageSword) {
                    hurtCurrentlyUsedSword(player, amount.getAsFloat());
                }
                float damageAfterBlock = 1.0F + amount.getAsFloat() * (1.0F - (float) GoldenAgeCombat.CONFIG.get(ServerConfig.class).blocking.blockedDamage);
                amount.mapFloat(v -> Math.min(v, damageAfterBlock <= 1.0F ? 0.0F : 1.0F));
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

    private static boolean canBlockDamageSource(Player player, DamageSource source) {
        Entity entity = source.getDirectEntity();
        if (entity instanceof AbstractArrow arrow) {
            if (arrow.getPierceLevel() > 0) {
                return false;
            }
        }
        if (!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
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
        return !getActiveItemStackBlocking(player).isEmpty();
    }

    public static ItemStack getActiveItemStackBlocking(Player player) {
        if (player.isUsingItem()) {
            ItemStack stack = player.getUseItem();
            return canItemStackBlock(stack) ? stack : ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static boolean canItemStackBlock(ItemStack stack) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).blocking.allowBlocking) return false;
        if (stack.is(ModRegistry.EXCLUDED_FROM_SWORD_BLOCKING_ITEM_TAG)) {
            return false;
        } else if (stack.getItem() instanceof SwordItem) {
            return true;
        } else {
            return stack.is(ModRegistry.INCLUDED_FOR_SWORD_BLOCKING_ITEM_TAG);
        }
    }

    protected static void hurtCurrentlyUsedSword(Player player, float damageAmount) {
        if (damageAmount >= 3.0F) {
            int i = 1 + Mth.floor(damageAmount);
            InteractionHand interactionhand = player.getUsedItemHand();
            player.getUseItem().hurtAndBreak(i, player, t -> {
                t.broadcastBreakEvent(interactionhand);
                CommonAbstractions.INSTANCE.onPlayerDestroyItem(t, t.getUseItem(), interactionhand);
            });
            if (player.getUseItem().isEmpty()) {
                if (interactionhand == InteractionHand.MAIN_HAND) {
                    player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }

                player.stopUsingItem();
                player.playSound(SoundEvents.ITEM_BREAK, 0.8F, 0.8F + player.level.random.nextFloat() * 0.4F);
            }
        }
    }

    public static boolean canActivateBlocking(Player player, ItemStack stack) {
        if (stack.is(ModRegistry.OVERRIDES_SWORD_BLOCKING_ITEM_TAG)) return false;
        return switch (stack.getUseAnimation()) {
            case BLOCK, SPYGLASS, BRUSH -> false;
            case EAT, DRINK ->
                    stack.getItem().getFoodProperties() == null || !player.canEat(stack.getItem().getFoodProperties().canAlwaysEat());
            case BOW, CROSSBOW -> player.getProjectile(stack).isEmpty();
            case SPEAR ->
                    stack.getDamageValue() >= stack.getMaxDamage() - 1 || EnchantmentHelper.getRiptide(stack) > 0 && !player.isInWaterOrRain();
            case TOOT_HORN -> player.getCooldowns().isOnCooldown(stack.getItem());
            default -> true;
        };
    }
}
