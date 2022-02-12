package fuzs.goldenagecombat.client.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.mixin.accessor.LivingEntityAccessor;
import fuzs.goldenagecombat.mixin.client.accessor.MinecraftAccessor;
import fuzs.goldenagecombat.mixin.client.accessor.MultiPlayerGameModeAccessor;
import fuzs.goldenagecombat.network.client.C2SSweepAttackMessage;
import fuzs.goldenagecombat.network.client.C2SSwingArmMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AttackAirHandler {
    private int airSweepTime;
    private int attackStrengthTicker;

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null) return;
        if (this.airSweepTime > 0) this.airSweepTime--;
    }

    @SubscribeEvent
    public void onClickInput$holdAttack(InputEvent.ClickInputEvent evt) {
        // don't do this for breaking blocks, as it will make the whole breaking process a lot slower
        if (evt.isAttack() && Minecraft.getInstance().hitResult.getType() == HitResult.Type.ENTITY) this.resetMissTime();
    }

    @SubscribeEvent
    public void onLeftClickEmpty$holdAttack(final PlayerInteractEvent.LeftClickEmpty evt) {
        // need to set this again here as miss time is set to 10 by vanilla in case of an actual miss
        this.resetMissTime();
    }

    private void resetMissTime() {
        if (!GoldenAgeCombat.CONFIG.server().combatTests.holdAttackButton) return;
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode.hasMissTime()) {
            ((MinecraftAccessor) minecraft).setMissTime(GoldenAgeCombat.CONFIG.server().combatTests.holdAttackButtonDelay);
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
        this.airSweepTime = GoldenAgeCombat.CONFIG.server().combatTests.holdAttackButtonDelay + 1;
    }

    @SubscribeEvent
    public void onClickInput$minAttackStrength(InputEvent.ClickInputEvent evt) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (evt.isAttack() && minecraft.hitResult.getType() != HitResult.Type.BLOCK) {
            // cancel attack when attack cooldown is not completely recharged
            if (minecraft.player.getAttackStrengthScale(0.5F) < GoldenAgeCombat.CONFIG.server().combatTests.minAttackStrength) {
                evt.setSwingHand(false);
                evt.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onClickInput$retainEnergy(InputEvent.ClickInputEvent evt) {
        if (!GoldenAgeCombat.CONFIG.server().combatTests.retainEnergy) return;
        final Minecraft minecraft = Minecraft.getInstance();
        if (evt.isAttack() && minecraft.hitResult.getType() == HitResult.Type.MISS) {
            // save ticksSinceLastSwing for resetting later
            this.attackStrengthTicker = ((LivingEntityAccessor) minecraft.player).getAttackStrengthTicker();
            // also prevent hand from swinging as this would cause the cooldown to be reset on the server side
            if (evt.shouldSwingHand()) {
                evt.setSwingHand(false);
                minecraft.player.swing(evt.getHand(), false);
                GoldenAgeCombat.NETWORK.sendToServer(new C2SSwingArmMessage(evt.getHand()));
            }
        }
    }

    @SubscribeEvent
    public void onLeftClickEmpty$retainEnergy(PlayerInteractEvent.LeftClickEmpty evt) {
        if (!GoldenAgeCombat.CONFIG.server().combatTests.retainEnergy) return;
        // reset ticksSinceLastSwing to previously saved value
        Player player = Minecraft.getInstance().player;
        ((LivingEntityAccessor) player).setAttackStrengthTicker(Math.max(((LivingEntityAccessor) player).getAttackStrengthTicker(), this.attackStrengthTicker));
    }
}
