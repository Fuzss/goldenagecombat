package fuzs.goldenagecombat.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.mixin.accessor.ItemAccessor;
import fuzs.goldenagecombat.mixin.accessor.PlayerAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CombatTestHandler {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent evt) {
        if (!GoldenAgeCombat.CONFIG.server().combatTests.fastSwitching) return;
        if (evt.phase != TickEvent.Phase.START) return;
        // switching items no longer triggers the attack cooldown
        Player player = evt.player;
        ItemStack itemstack = player.getMainHandItem();
        if (!ItemStack.matches(((PlayerAccessor) player).getLastItemInMainHand(), itemstack)) {
            ((PlayerAccessor) player).setLastItemInMainHand(itemstack.copy());
        }
    }

    @SubscribeEvent
    public void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {
        if (GoldenAgeCombat.CONFIG.server().combatTests.noShieldDelay && evt.getItem().getUseAnimation() == UseAnim.BLOCK) {
            // remove shield activation delay
            evt.setDuration(evt.getItem().getUseDuration() - 5);
        }
        if (GoldenAgeCombat.CONFIG.server().combatTests.fastDrinking && evt.getItem().getUseAnimation() == UseAnim.DRINK) {
            evt.setDuration(20);
        }
    }

    @SubscribeEvent
    public void onRightClickItem(final PlayerInteractEvent.RightClickItem evt) {
        if (GoldenAgeCombat.CONFIG.server().combatTests.throwablesDelay) {
            Item item = evt.getItemStack().getItem();
            if (evt.getEntityLiving() instanceof Player player && (item instanceof SnowballItem || item instanceof EggItem)) {
                // add delay after using an item
                player.getCooldowns().addCooldown(item, 4);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamage(final LivingDamageEvent evt) {
        if (GoldenAgeCombat.CONFIG.server().combatTests.eatingInterruption) {
            LivingEntity entity = evt.getEntityLiving();
            UseAnim useAction = entity.getUseItem().getUseAnimation();
            if (useAction == UseAnim.EAT || useAction == UseAnim.DRINK) {
                entity.stopUsingItem();
            }
        }
    }

    public static void setMaxStackSize() {
        if (!GoldenAgeCombat.CONFIG.server().combatTests.increaseStackSize) return;
        ((ItemAccessor) Items.SNOWBALL).setMaxStackSize(64);
        ((ItemAccessor) Items.EGG).setMaxStackSize(64);
        ((ItemAccessor) Items.POTION).setMaxStackSize(16);
    }
}
