package com.fuzs.goldenagecombat.handler;

import com.fuzs.goldenagecombat.util.BlockingItemHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InitiateBlockHandler {

    private final BlockingItemHelper blockingHelper = new BlockingItemHelper();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRightClickItem(final PlayerInteractEvent.RightClickItem evt) {

        PlayerEntity player = evt.getPlayer();
        if (this.blockingHelper.canItemStackBlock(evt.getItemStack())) {

            player.setActiveHand(evt.getHand());
            // cause reequip animation, but don't swing hand
            evt.setCancellationResult(ActionResultType.CONSUME);
            evt.setCanceled(true);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity && this.blockingHelper.canItemStackBlock(evt.getItem())) {

            evt.setDuration(72000);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(final LivingHurtEvent evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) evt.getEntityLiving();
            if (!evt.getSource().isUnblockable() && this.blockingHelper.isActiveItemStackBlocking(player) && evt.getAmount() > 0.0F) {

                evt.setAmount((1.0F + evt.getAmount()) * 0.5F);
            }
        }

    }

}
