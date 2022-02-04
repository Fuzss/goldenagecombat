package fuzs.goldenagecombat.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.registry.ModRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SwordBlockingHandler {
    @SubscribeEvent
    public void onRightClickItem(final PlayerInteractEvent.RightClickItem evt) {
        Player player = evt.getPlayer();
        if (canItemStackBlock(evt.getItemStack())) {
            if (!GoldenAgeCombat.CONFIG.server().adjustments.prioritizeShield || evt.getHand() != InteractionHand.MAIN_HAND || player.getOffhandItem().getUseAnimation() != UseAnim.BLOCK) {
                player.startUsingItem(evt.getHand());
                // cause reequip animation, but don't swing hand, not to be confused with ActionResultType#SUCCESS
                // partial version seems to not affect game stats which is probably better since you can just spam sword blocking haha
                evt.setCancellationResult(InteractionResult.CONSUME_PARTIAL);
                evt.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {
        if (evt.getEntityLiving() instanceof Player && canItemStackBlock(evt.getItem())) {
            // default use duration for items
            evt.setDuration(72000);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(final LivingHurtEvent evt) {
        if (evt.getEntityLiving() instanceof Player player) {
            if (isDamageSourceBlockable(evt.getSource()) && isActiveItemStackBlocking(player) && evt.getAmount() > 0.0F) {
                evt.setAmount((1.0F + evt.getAmount()) * 0.5F);
            }
        }
    }

    private static boolean isDamageSourceBlockable(DamageSource damageSourceIn) {
        Entity entity = damageSourceIn.getDirectEntity();
        if (entity instanceof AbstractArrow arrow) {
            if (arrow.getPierceLevel() > 0) {
                return false;
            }
        }
        return !damageSourceIn.isBypassArmor();
    }

    public static boolean isActiveItemStackBlocking(Player player) {
        return player.isUsingItem() && canItemStackBlock(player.getUseItem());
    }

    public static boolean canItemStackBlock(ItemStack stack) {
        if (!GoldenAgeCombat.CONFIG.server().blocking.allowBlocking) return false;
        Item item = stack.getItem();
        if (stack.is(ModRegistry.SWORD_BLOCKING_EXCLUSIONS_TAG)) {
            return false;
        } else if (item instanceof SwordItem) {
            return true;
        } else {
            return stack.is(ModRegistry.SWORD_BLOCKING_INCLUSIONS_TAG);
        }
    }
}
