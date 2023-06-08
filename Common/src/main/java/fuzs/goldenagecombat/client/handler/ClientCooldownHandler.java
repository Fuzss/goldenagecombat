package fuzs.goldenagecombat.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public class ClientCooldownHandler {
    @Nullable
    private static AttackIndicatorStatus attackIndicator = null;

    public static EventResult onBeforeRenderGuiElement(Minecraft minecraft, PoseStack poseStack, float tickDelta, int screenWidth, int screenHeight) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).classic.removeCooldown) return EventResult.PASS;
        // this will mostly just remove the attack indicator, except for one niche case when looking at an entity
        // just for that reason the whole indicator is also disabled later on
        // indicator would otherwise render when looking at an entity, even when there is no cooldown
        if (attackIndicator == null) {
            attackIndicator = minecraft.options.attackIndicator().get();
            minecraft.options.attackIndicator().set(AttackIndicatorStatus.OFF);
        }
        return EventResult.PASS;
    }

    public static void onAfterRenderGuiElement(Minecraft minecraft, PoseStack poseStack, float tickDelta, int screenWidth, int screenHeight) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).classic.removeCooldown) return;
        // reset to old value; don't just leave this disabled as it'll change the vanilla setting permanently, which no mod should do imo
        if (attackIndicator != null) {
            minecraft.options.attackIndicator().set(attackIndicator);
            attackIndicator = null;
        }
    }
}
