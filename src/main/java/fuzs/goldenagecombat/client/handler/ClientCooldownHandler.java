package fuzs.goldenagecombat.client.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.handler.ClassicCombatHandler;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientCooldownHandler {
    private final Minecraft minecraft = Minecraft.getInstance();
    private AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.OFF;

    @SubscribeEvent
    public void onRenderGameOverlay$Pre(final RenderGameOverlayEvent.Pre evt) {
        if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (GoldenAgeCombat.CONFIG.server().classic.removeCooldown) {
            // this will mostly just remove the attack indicator, except for one niche case when looking at an entity
            // just for that reason the whole indicator is also disabled later on
            ClassicCombatHandler.disableCooldownPeriod(this.minecraft.player);
        }
        if (GoldenAgeCombat.CONFIG.client().classic.hideAttackIndicator) {
            // indicator would otherwise render when looking at an entity, even when there is no cooldown
            this.attackIndicator = this.minecraft.options.attackIndicator;
            this.minecraft.options.attackIndicator = AttackIndicatorStatus.OFF;
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay$Post(final RenderGameOverlayEvent.Post evt) {
        if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (GoldenAgeCombat.CONFIG.client().classic.hideAttackIndicator) {
            // reset to old value
            // we don't just leave this disabled as it'll change the vanilla setting permanently, which a mod shouldn't do imo
            this.minecraft.options.attackIndicator = this.attackIndicator;
        }
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END) return;
        if (GoldenAgeCombat.CONFIG.server().classic.removeCooldown) {
            if (this.minecraft.player != null && !this.minecraft.isPaused()) {
                // FirstPersonRenderer::tick uses cooldown period, so we reset it before calling that
                ClassicCombatHandler.disableCooldownPeriod(this.minecraft.player);
            }
        }
    }
}
