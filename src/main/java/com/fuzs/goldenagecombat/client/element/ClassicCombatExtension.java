package com.fuzs.goldenagecombat.client.element;

import com.fuzs.goldenagecombat.element.ClassicCombatElement;
import com.fuzs.goldenagecombat.mixin.client.accessor.IFirstPersonRendererAccessor;
import com.fuzs.goldenagecombat.mixin.client.accessor.IVideoSettingsScreenAccessor;
import com.fuzs.puzzleslib_gc.element.extension.ElementExtension;
import com.fuzs.puzzleslib_gc.element.side.IClientElement;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;

@SuppressWarnings("ConstantConditions")
public class ClassicCombatExtension extends ElementExtension<ClassicCombatElement> implements IClientElement {

    private final Minecraft mc = Minecraft.getInstance();
    private final FirstPersonRenderer itemRenderer = new FirstPersonRenderer(this.mc);

    /**
     * temporary storage for disabling while rendering is happening
     */
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
        this.addListener(this::onRenderTick);
        this.addListener(this::onItemTooltip, EventPriority.LOW);
        this.addListener(this::onGuiInit);
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

            if (!this.mc.isGamePaused()) {

                // FirstPersonRenderer::tick uses cooldown period, so we reset it before calling that
                ClassicCombatElement.disableCooldownPeriod(this.mc.player);
                // tick our own item renderer which doesn't respond to equipped progress being reset
                // values will be synced to original right before rendering
                this.itemRenderer.tick();
            }
        }
    }

    private void onRenderTick(final TickEvent.RenderTickEvent evt) {

        if (this.parent.removeCooldown && evt.phase == TickEvent.Phase.START) {

            // we use a separate item renderer, so now sync its values to the vanilla one
            IFirstPersonRendererAccessor modItemRenderer = (IFirstPersonRendererAccessor) this.itemRenderer;
            IFirstPersonRendererAccessor mainItemRenderer = (IFirstPersonRendererAccessor) this.mc.getFirstPersonRenderer();
            mainItemRenderer.setEquippedProgressMainHand(modItemRenderer.getEquippedProgressMainHand());
            mainItemRenderer.setEquippedProgressOffHand(modItemRenderer.getEquippedProgressOffHand());
            mainItemRenderer.setPrevEquippedProgressMainHand(modItemRenderer.getPrevEquippedProgressMainHand());
            mainItemRenderer.setPrevEquippedProgressOffHand(modItemRenderer.getPrevEquippedProgressOffHand());
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

    private void onItemTooltip(final ItemTooltipEvent evt) {

        if (this.removeAttackSpeedTooltip) {

            evt.getToolTip().removeIf(component -> component.toString().contains("attribute.name.generic.attack_speed"));
        }
    }

}
