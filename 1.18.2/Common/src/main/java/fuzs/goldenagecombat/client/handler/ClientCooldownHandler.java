package fuzs.goldenagecombat.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.puzzleslib.api.client.screen.v2.DeferredTooltipRendering;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ClientCooldownHandler {
    private static final Component ATTACK_INDICATOR_TOOLTIP = new TextComponent(String.format("Attack Indicator has been disabled by %s mod.", GoldenAgeCombat.MOD_NAME));

    @Nullable
    private static AttackIndicatorStatus attackIndicator = null;

    public static EventResult onBeforeRenderGuiElement(Minecraft minecraft, PoseStack guiGraphics, float tickDelta, int screenWidth, int screenHeight) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).removeAttackCooldown) return EventResult.PASS;
        // this will mostly just remove the attack indicator, except for one niche case when looking at an entity
        // just for that reason the whole indicator is also disabled later on
        // indicator would otherwise render when looking at an entity, even when there is no cooldown
        if (attackIndicator == null) {
            attackIndicator = minecraft.options.attackIndicator;
            minecraft.options.attackIndicator = AttackIndicatorStatus.OFF;
        }
        return EventResult.PASS;
    }
    public static void onAfterRenderGuiElement(Minecraft minecraft, PoseStack guiGraphics, float tickDelta, int screenWidth, int screenHeight) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).removeAttackCooldown) return;
        // reset to old value; don't just leave this disabled as it'll change the vanilla setting permanently in options.txt, which no mod should do imo
        if (attackIndicator != null) {
            minecraft.options.attackIndicator = attackIndicator;
            attackIndicator = null;
        }
    }

    public static void onAfterInit(Minecraft minecraft, Screen screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets, Consumer<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget) {
        if (!(screen instanceof VideoSettingsScreen)) return;
        if (!GoldenAgeCombat.CONFIG.getHolder(ServerConfig.class).isAvailable() || !GoldenAgeCombat.CONFIG.get(ServerConfig.class).removeAttackCooldown) return;
        // disables the attack indicator option button in the video settings screen
        screen.children().stream().filter(OptionsList.class::isInstance).findAny().map(OptionsList.class::cast).map(optionsList -> optionsList.findOption(Option.ATTACK_INDICATOR)).ifPresent(widget -> {
            widget.active = false;
            DeferredTooltipRendering.setTooltipForNextRenderPass(minecraft, widget, ATTACK_INDICATOR_TOOLTIP);
        });
    }
}
