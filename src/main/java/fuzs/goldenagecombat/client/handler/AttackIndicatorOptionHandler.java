package fuzs.goldenagecombat.client.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Optional;

/**
 * all this class does is disabling the attack indicator button in the video settings screen (since the attack indicator no longer shows up anyways)
 * this is totally unnecessary, but I made it (twice haha) so here it is
 */
public class AttackIndicatorOptionHandler {
    private static final Component ATTACK_INDICATOR_TOOLTIP_COMPONENT = new TextComponent(String.format("Attack Indicator has been disabled by %s mod.", GoldenAgeCombat.MOD_NAME));

    @SubscribeEvent
    public void onScreenInit(final ScreenEvent.InitScreenEvent.Post evt) {
        if (evt.getScreen() instanceof VideoSettingsScreen && GoldenAgeCombat.CONFIG.isServerAvailable() && GoldenAgeCombat.CONFIG.server().classic.removeCooldown) {
            // disable attack indicator button in video settings screen
            getOptionsList(evt.getScreen())
                    .flatMap(optionsList -> ((List<?>) optionsList.children()).stream()
                            .flatMap(optionsRow -> ((ContainerObjectSelectionList.Entry<?>) optionsRow).children().stream())
                            .filter(optionButton -> optionButton instanceof AbstractWidget widget && isAttackIndicatorOption(widget))
                            .findFirst())
                    .ifPresent(eventListener -> ((AbstractWidget) eventListener).active = false);
        }
    }

    @SubscribeEvent
    public void onDrawScreen(final ScreenEvent.DrawScreenEvent.Post evt) {
        if (evt.getScreen() instanceof VideoSettingsScreen && GoldenAgeCombat.CONFIG.isServerAvailable() && GoldenAgeCombat.CONFIG.server().classic.removeCooldown) {
            // render tooltip explaining why the button is disabled
            getOptionsList(evt.getScreen()).flatMap(list -> getMouseOverList(list, evt.getMouseX(), evt.getMouseY())).ifPresent(widget -> {
                if (isAttackIndicatorOption(widget)) {
                    final Minecraft minecraft = Minecraft.getInstance();
                    evt.getScreen().renderTooltip(evt.getPoseStack(), minecraft.font.split(ATTACK_INDICATOR_TOOLTIP_COMPONENT, 200), evt.getMouseX(), evt.getMouseY());
                }
            });
        }
    }

    private static Optional<OptionsList> getOptionsList(Screen screen) {
        // don't access optionsRowList field directly using reflection or an accessor since OptiFine removes it
        return screen.children().stream()
                .filter(listener -> listener instanceof OptionsList)
                .findFirst()
                .map(listener -> (OptionsList) listener);
    }

    private static boolean isAttackIndicatorOption(AbstractWidget widget) {
        if (widget.getMessage() instanceof TranslatableComponent component) {
            if (component.getArgs().length > 0 && component.getArgs()[0] instanceof TranslatableComponent component1) {
                return component1.getKey().equals("options.attackIndicator");
            }
        }
        return false;
    }

    private static Optional<AbstractWidget> getMouseOverList(AbstractSelectionList<?> list, double mouseX, double mouseY) {
        for (AbstractSelectionList.Entry<?> optionslist$entry : list.children()) {
            if (optionslist$entry instanceof ContainerObjectSelectionList.Entry)
            for (GuiEventListener guiEventListener : ((ContainerObjectSelectionList.Entry<?>) optionslist$entry).children()) {
                if (guiEventListener instanceof AbstractWidget abstractWidget && isMouseOverWidget(abstractWidget, mouseX, mouseY)) {
                    return Optional.of(abstractWidget);
                }
            }
        }
        return Optional.empty();
    }

    private static boolean isMouseOverWidget(AbstractWidget widget, double mouseX, double mouseY) {
        // omits check for active, otherwise unchanged
        return widget.visible && mouseX >= widget.x && mouseY >= widget.y && mouseX < (widget.x + widget.getWidth()) && mouseY < (widget.y + widget.getHeight());
    }
}
