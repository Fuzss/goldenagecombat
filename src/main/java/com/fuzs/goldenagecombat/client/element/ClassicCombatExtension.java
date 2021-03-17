package com.fuzs.goldenagecombat.client.element;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.element.ClassicCombatElement;
import com.fuzs.goldenagecombat.mixin.client.accessor.IVideoSettingsScreenAccessor;
import com.fuzs.puzzleslib_gc.element.extension.ElementExtension;
import com.fuzs.puzzleslib_gc.element.side.IClientElement;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class ClassicCombatExtension extends ElementExtension<ClassicCombatElement> implements IClientElement {

    private final Minecraft mc = Minecraft.getInstance();
    private final ITextComponent disabledIndicatorComponent = new StringTextComponent("Attack Indicator has been disabled by " + GoldenAgeCombat.NAME + " mod");
    private List<IReorderingProcessor> disabledIndicatorProcessor;

    private AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.OFF;

    private boolean hideAttackIndicator;
    private boolean removeAttackSpeedTooltip;

    public ClassicCombatExtension(ClassicCombatElement parent) {

        super(parent);
    }

    @Override
    public void setupClient() {

        this.addListener(this::onRenderGameOverlay);
        this.addListener(this::onClientTick);
        this.addListener(this::onItemTooltip, EventPriority.LOW);
        this.addListener(this::onGuiInit);
        this.addListener(this::onDrawScreen);
    }

    @Override
    public void initClient() {

        this.disabledIndicatorProcessor = Minecraft.getInstance().fontRenderer.trimStringToWidth(this.disabledIndicatorComponent, 200);
    }

    @Override
    public void setupClientConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Prevent attack indicator from rendering regardless of what's been set in \"Video Settings\".").define("Remove Attack Indicator", true), v -> this.hideAttackIndicator = v);
        addToConfig(builder.comment("Remove \"Attack Speed\" attribute from inventory tooltips.").define("Remove Attack Speed Tooltip", true), v -> this.removeAttackSpeedTooltip = v);
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

        // some mods change the video settings screem and the button will not be found, so we just catch that exception and do nothing
        if (this.hideAttackIndicator && evt.getGui() instanceof VideoSettingsScreen) {

            try {

                // disable attack indicator button in video settings screen
                ((IVideoSettingsScreenAccessor) evt.getGui()).getOptionsRowList().getEventListeners().stream()
                        .flatMap(optionsRow -> optionsRow.getEventListeners().stream())
                        .filter(eventListener -> eventListener instanceof OptionButton)
                        .filter(eventListener -> ((OptionButton) eventListener).func_238517_a_() == AbstractOption.ATTACK_INDICATOR)
                        .findAny().ifPresent(eventListener -> ((Widget) eventListener).active = false);
            } catch (NoSuchFieldError ignored) {

            }
        }
    }

    private void onDrawScreen(final GuiScreenEvent.DrawScreenEvent.Post evt) {

        if (this.hideAttackIndicator && evt.getGui() instanceof VideoSettingsScreen) {

            try {

                getWidgetAtPosition(((IVideoSettingsScreenAccessor) evt.getGui()).getOptionsRowList(), evt.getMouseX(), evt.getMouseY())
                        .filter(widget -> widget instanceof OptionButton)
                        .filter(widget -> ((OptionButton) widget).func_238517_a_() == AbstractOption.ATTACK_INDICATOR)
                        .ifPresent(widget -> evt.getGui().renderTooltip(evt.getMatrixStack(), this.disabledIndicatorProcessor, evt.getMouseX(), evt.getMouseY()));
            } catch (NoSuchFieldError ignored) {

            }
        }
    }

    private void onItemTooltip(final ItemTooltipEvent evt) {

        if (this.removeAttackSpeedTooltip) {

            evt.getToolTip().removeIf(component -> component.toString().contains("attribute.name.generic.attack_speed"));
        }
    }

    private static Optional<Widget> getWidgetAtPosition(OptionsRowList optionsRowList, double mouseX, double mouseY) {

        for (OptionsRowList.Row optionsRow : optionsRowList.getEventListeners()) {

            for (IGuiEventListener guiEventListener : optionsRow.getEventListeners()) {

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
