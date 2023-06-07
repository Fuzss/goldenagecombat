package fuzs.goldenagecombat.client.handler;

import fuzs.goldenagecombat.mixin.client.accessor.MinecraftAccessor;
import fuzs.goldenagecombat.mixin.client.accessor.MultiPlayerGameModeAccessor;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.goldenagecombat.network.client.ServerboundSweepAttackMessage;
import fuzs.goldenagecombat.network.client.ServerboundSwingArmMessage;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

public class AttackAirHandler {
    private static int airSweepTime;

    public static void onEndTick(Minecraft minecraft) {
        if (airSweepTime > 0) airSweepTime--;
    }

    public static void resetMissTime() {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.holdAttackButton) return;
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode.hasMissTime()) {
            ((MinecraftAccessor) minecraft).goldenagecombat$setMissTime(GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.holdAttackButtonDelay);
        }
    }

    public static EventResult onAttackInteraction(Minecraft minecraft, LocalPlayer player) {
        if (minecraft.hitResult.getType() != HitResult.Type.BLOCK) {
            // cancel attack when attack cooldown is not completely recharged
            if (minecraft.player.getAttackStrengthScale(0.5F) < GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.minAttackStrength) {
                return EventResult.INTERRUPT;
            }
        }
        // don't do this for breaking blocks, as it will make the whole breaking process a lot slower
        if (minecraft.hitResult.getType() == HitResult.Type.ENTITY) {
            resetMissTime();
        }
        return EventResult.PASS;
    }

    public static void onLeftClickEmpty() {
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.airSweepAttack) {
            if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.continuousAirSweeping || airSweepTime <= 0) {
                final Minecraft minecraft = Minecraft.getInstance();
                ((MultiPlayerGameModeAccessor) minecraft.gameMode).goldenagecombat$callEnsureHasSentCarriedItem();
                GoldenAgeCombat.NETWORK.sendToServer(new ServerboundSweepAttackMessage((minecraft.player).isShiftKeyDown()));
            }
            // one more than auto-attacking to prevent both from working together as it would be too op
            airSweepTime = GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.holdAttackButtonDelay + 1;
        }
        // need to set this again here as miss time is set to 10 by vanilla in case of an actual miss
        resetMissTime();
    }

    public static void swingHandRetainAttackStrength(Player player, InteractionHand interactionHand) {
        player.swing(interactionHand, false);
        GoldenAgeCombat.NETWORK.sendToServer(new ServerboundSwingArmMessage(interactionHand));
    }
}
