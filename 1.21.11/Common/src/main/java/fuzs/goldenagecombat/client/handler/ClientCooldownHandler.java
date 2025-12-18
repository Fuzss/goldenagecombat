package fuzs.goldenagecombat.client.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.CommonConfig;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ClientCooldownHandler {
    private static final Component ATTACK_INDICATOR_TOOLTIP = Component.literal(String.format(
            "Attack Indicator has been disabled by %s mod.",
            GoldenAgeCombat.MOD_NAME));

    @Nullable
    private static AttackIndicatorStatus attackIndicator;

    public static void onBeforeRenderGui(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!GoldenAgeCombat.CONFIG.get(CommonConfig.class).removeAttackCooldown) return;
        // this will mostly just remove the attack indicator, except for one niche case when looking at an entity
        // just for that reason the whole indicator is also disabled later on
        // indicator would otherwise render when looking at an entity, even when there is no cooldown
        if (attackIndicator == null) {
            Options options = Minecraft.getInstance().options;
            attackIndicator = options.attackIndicator().get();
            options.attackIndicator().set(AttackIndicatorStatus.OFF);
        }
    }

    public static void onAfterRenderGui(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!GoldenAgeCombat.CONFIG.get(CommonConfig.class).removeAttackCooldown) return;
        // reset to old value; don't just leave this disabled as it'll change the vanilla setting permanently in options.txt, which no mod should do imo
        if (attackIndicator != null) {
            Minecraft.getInstance().options.attackIndicator().set(attackIndicator);
            attackIndicator = null;
        }
    }

    public static void onAfterInit(Minecraft minecraft, VideoSettingsScreen screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets, UnaryOperator<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget) {
        if (!GoldenAgeCombat.CONFIG.getHolder(ServerConfig.class).isAvailable() || !GoldenAgeCombat.CONFIG.get(
                CommonConfig.class).removeAttackCooldown) {
            return;
        }
        // disables the attack indicator option button in the video settings screen
        screen.children()
                .stream()
                .filter(OptionsList.class::isInstance)
                .findAny()
                .map(OptionsList.class::cast)
                .map(optionsList -> optionsList.findOption(minecraft.options.attackIndicator()))
                .ifPresent(widget -> {
                    widget.active = false;
                    widget.setTooltip(Tooltip.create(ATTACK_INDICATOR_TOOLTIP));
                });
    }
}
