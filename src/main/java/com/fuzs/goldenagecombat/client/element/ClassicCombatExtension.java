package com.fuzs.goldenagecombat.client.element;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.element.ClassicCombatElement;
import com.fuzs.goldenagecombat.mixin.client.accessor.IOptionButtonAccessor;
import com.fuzs.puzzleslib_gc.element.extension.ElementExtension;
import com.fuzs.puzzleslib_gc.element.side.IClientElement;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class ClassicCombatExtension extends ElementExtension<ClassicCombatElement> implements IClientElement {

    private final Minecraft mc = Minecraft.getInstance();
    private final List<String> disabledIndicator = Lists.newArrayList("Attack Indicator has been disabled", "by " + GoldenAgeCombat.NAME + " mod");

    private AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.OFF;

    private boolean hideAttackIndicator;

    public ClassicCombatExtension(ClassicCombatElement parent) {

        super(parent);
    }

    @Override
    public void setupClient() {

        this.addListener(this::onRenderGameOverlay);
        this.addListener(this::onClientTick);
        this.addListener(this::onGuiInit);
        this.addListener(this::onDrawScreen);
    }

    @Override
    public void setupClientConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Prevent attack indicator from rendering regardless of what's been set for the \"Attack Indicator\" option in video settings.").define("Remove Attack Indicator", true), v -> this.hideAttackIndicator = v);
    }

    private void onRenderGameOverlay(final RenderGameOverlayEvent evt) {

        if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) {

            return;
        }

        if (evt instanceof RenderGameOverlayEvent.Pre) {

            if (this.parent.removeCooldown) {

                // this will mostly just remove the attack indicator, except for one niche case when looking at an entity
                // just for that reason the whole indicator is also disabled later on
                ClassicCombatElement.disableCooldownPeriod(this.mc.player);
            }

            if (this.hideAttackIndicator) {

                // indicator would otherwise render when looking at an entity, even when there is no cooldown
                this.attackIndicator = this.mc.gameSettings.attackIndicator;
                this.mc.gameSettings.attackIndicator = AttackIndicatorStatus.OFF;
            }
        } else if (evt instanceof RenderGameOverlayEvent.Post) {

            if (this.hideAttackIndicator) {

                // reset to old value
                this.mc.gameSettings.attackIndicator = this.attackIndicator;
            }
        }
    }

    private void onClientTick(final TickEvent.ClientTickEvent evt) {

        if (this.parent.removeCooldown && evt.phase == TickEvent.Phase.END) {

            if (this.mc.player != null && !this.mc.isGamePaused()) {

                // FirstPersonRenderer::tick uses cooldown period, so we reset it before calling that
                ClassicCombatElement.disableCooldownPeriod(this.mc.player);
            }
        }
    }

    private void onGuiInit(final GuiScreenEvent.InitGuiEvent.Post evt) {

        if (this.hideAttackIndicator && evt.getGui() instanceof VideoSettingsScreen) {

            // disable attack indicator button in video settings screen
            getOptionsRowList(evt.getGui())
                    .flatMap(optionsRowList -> optionsRowList.children().stream()
                            .flatMap(optionsRow -> optionsRow.children().stream())
                            .filter(optionButton -> optionButton instanceof OptionButton)
                            .filter(optionButton -> ((IOptionButtonAccessor) optionButton).getEnumOptions() == AbstractOption.ATTACK_INDICATOR)
                            .findFirst())
                    .ifPresent(eventListener -> ((Widget) eventListener).active = false);
        }
    }

    private void onDrawScreen(final GuiScreenEvent.DrawScreenEvent.Post evt) {

        if (this.hideAttackIndicator && evt.getGui() instanceof VideoSettingsScreen) {

            // render tooltip explaining why the button is disabled
            getOptionsRowList(evt.getGui())
                    .flatMap(rowList -> getWidgetAtPosition(rowList, evt.getMouseX(), evt.getMouseY())
                            .filter(widget -> widget instanceof OptionButton)
                            .filter(widget -> ((IOptionButtonAccessor) widget).getEnumOptions() == AbstractOption.ATTACK_INDICATOR))
                    .ifPresent(widget -> evt.getGui().renderTooltip(this.disabledIndicator, evt.getMouseX(), evt.getMouseY()));
        }
    }

    private static Optional<OptionsRowList> getOptionsRowList(Screen screen) {

        // don't access optionsRowList field directly using reflection or an accessor since OptiFine removes it
        return screen.children().stream()
                .filter(listener -> listener instanceof OptionsRowList)
                .findFirst()
                .map(listener -> (OptionsRowList) listener);
    }

    private static Optional<Widget> getWidgetAtPosition(OptionsRowList optionsRowList, double mouseX, double mouseY) {

        for (OptionsRowList.Row optionsRow : optionsRowList.children()) {

            for (IGuiEventListener guiEventListener : optionsRow.children()) {

                if (guiEventListener instanceof Widget && !((Widget) guiEventListener).active) {

                    ((Widget) guiEventListener).active = true;
                    boolean isMouseOver = guiEventListener.isMouseOver(mouseX, mouseY);
                    ((Widget) guiEventListener).active = false;
                    if (isMouseOver) {

                        return Optional.of((Widget) guiEventListener);
                    }
                }
            }
        }

        return Optional.empty();
    }

}
