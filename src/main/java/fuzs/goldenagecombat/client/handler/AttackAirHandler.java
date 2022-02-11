package fuzs.goldenagecombat.client.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.mixin.client.accessor.MinecraftAccessor;
import fuzs.goldenagecombat.mixin.client.accessor.MultiPlayerGameModeAccessor;
import fuzs.goldenagecombat.network.client.C2SSweepAttackMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AttackAirHandler {
    private int airSweepTime;

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END) return;
        if (this.airSweepTime > 0) this.airSweepTime--;
    }

    @SubscribeEvent
    public void onLeftClickEmpty$holdAttack(final PlayerInteractEvent.LeftClickEmpty evt) {
        if (!GoldenAgeCombat.CONFIG.server().combatTests.holdAttackButton) return;
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode.hasMissTime()) {
            ((MinecraftAccessor) minecraft).setMissTime(5);
        }
    }

    @SubscribeEvent
    public void onLeftClickEmpty$airAttack(final PlayerInteractEvent.LeftClickEmpty evt) {
        if (!GoldenAgeCombat.CONFIG.server().combatTests.airSweepAttack) return;
        if (GoldenAgeCombat.CONFIG.server().combatTests.continuousAirSweeping || this.airSweepTime <= 0) {
            final Minecraft minecraft = Minecraft.getInstance();
            ((MultiPlayerGameModeAccessor) minecraft.gameMode).callEnsureHasSentCarriedItem();
            GoldenAgeCombat.NETWORK.sendToServer(new C2SSweepAttackMessage((minecraft.player).isShiftKeyDown()));
        }
        // one more than auto-attacking to prevent both from working together as it would be too op
        this.airSweepTime = 6;
    }
}
