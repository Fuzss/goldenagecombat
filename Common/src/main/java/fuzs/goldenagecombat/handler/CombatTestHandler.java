package fuzs.goldenagecombat.handler;

import fuzs.goldenagecombat.mixin.accessor.ItemAccessor;
import fuzs.goldenagecombat.mixin.accessor.PlayerAccessor;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class CombatTestHandler {
    
    public static void onStartPlayerTick(Player player) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.fastSwitching) return;
        // switching items no longer triggers the attack cooldown
        ItemStack stack = player.getMainHandItem();
        if (!ItemStack.matches(((PlayerAccessor) player).goldenagecombat$getLastItemInMainHand(), stack)) {
            ((PlayerAccessor) player).goldenagecombat$setLastItemInMainHand(stack.copy());
        }
    }

    public static EventResult onUseItemStart(LivingEntity entity, ItemStack stack, MutableInt remainingUseDuration) {
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.noShieldDelay && stack.getUseAnimation() == UseAnim.BLOCK) {
            // remove shield activation delay
            remainingUseDuration.accept(stack.getUseDuration() - 5);
        }
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.fastDrinking && stack.getUseAnimation() == UseAnim.DRINK) {
            remainingUseDuration.accept(20);
        }
        return EventResult.PASS;
    }

    public static EventResultHolder<InteractionResultHolder<ItemStack>> onUseItem(Player player, Level level, InteractionHand hand) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.throwablesDelay) return EventResultHolder.pass();
        ItemStack itemInHand = player.getItemInHand(hand);
        if (itemInHand.getItem() instanceof SnowballItem || itemInHand.getItem() instanceof EggItem) {
            // add delay after using an item
            player.getCooldowns().addCooldown(itemInHand.getItem(), 4);
        }
        return EventResultHolder.pass();
    }

    public static EventResult onLivingHurt(LivingEntity entity, DamageSource source, MutableFloat amount) {
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.eatingInterruption) {
            UseAnim useAction = entity.getUseItem().getUseAnimation();
            if (useAction == UseAnim.EAT || useAction == UseAnim.DRINK) {
                entity.stopUsingItem();
            }
        }
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.noProjectileImmunity) {
            if (source.is(DamageTypeTags.IS_PROJECTILE)) {
                // immediately reset damage immunity after being hit by any projectile, fixes multishot
                entity.invulnerableTime = 0;
            }
        }
        return EventResult.PASS;
    }

    public static void setMaxStackSize(ServerConfig serverConfig) {
        if (!serverConfig.combatTests.increaseStackSize) return;
        ((ItemAccessor) Items.SNOWBALL).goldenagecombat$setMaxStackSize(64);
        ((ItemAccessor) Items.EGG).goldenagecombat$setMaxStackSize(64);
        ((ItemAccessor) Items.POTION).goldenagecombat$setMaxStackSize(16);
    }
}
