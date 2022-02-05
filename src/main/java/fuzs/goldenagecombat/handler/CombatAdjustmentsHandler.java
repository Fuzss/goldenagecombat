package fuzs.goldenagecombat.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.mixin.client.accessor.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CombatAdjustmentsHandler {
    @SubscribeEvent
    public void onCriticalHit(final CriticalHitEvent evt) {
        // prevent sweeping from taking effect unless the enchantment is in place, onGround flag is reset next tick anyways
        if (GoldenAgeCombat.CONFIG.server().adjustments.sweepingRequired && EnchantmentHelper.getSweepingDamageRatio(evt.getPlayer()) == 0.0F || GoldenAgeCombat.CONFIG.server().adjustments.noSneakSweeping && evt.getPlayer().isShiftKeyDown()) {
            evt.getPlayer().setOnGround(false);
        }
    }

    @SubscribeEvent
    public void onPlaySoundAtEntity(final PlaySoundAtEntityEvent evt) {
        // disable combat update player attack sounds
        if (GoldenAgeCombat.CONFIG.server().adjustments.canceledAttackSounds.contains(evt.getSound())) {
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLeftClickEmpty(final PlayerInteractEvent.LeftClickEmpty evt) {
        if (!GoldenAgeCombat.CONFIG.server().adjustments.holdAttackButton) return;
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode.hasMissTime()) {
            ((MinecraftAccessor) minecraft).setMissTime(5);
        }
    }
}
