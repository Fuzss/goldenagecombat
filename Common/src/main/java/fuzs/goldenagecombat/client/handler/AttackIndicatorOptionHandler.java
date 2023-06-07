package fuzs.goldenagecombat.client.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public class AttackIndicatorOptionHandler {
    private static final Component ATTACK_INDICATOR_TOOLTIP = Component.literal(String.format("Attack Indicator has been disabled by %s mod.", GoldenAgeCombat.MOD_NAME));

    public static void onAfterInit(Minecraft minecraft, Screen screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets, Consumer<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget) {
        if (!(screen instanceof VideoSettingsScreen)) return;
        if (!GoldenAgeCombat.CONFIG.getHolder(ServerConfig.class).isAvailable() || !GoldenAgeCombat.CONFIG.get(ServerConfig.class).classic.removeCooldown) return;
        screen.children().stream()
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
