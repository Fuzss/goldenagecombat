package com.fuzs.goldenagecombat.client.handler;

import com.fuzs.goldenagecombat.config.CommonConfigHandler;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class NoCooldownHandler {

    private boolean hasOptiFine;

    private final Minecraft mc = Minecraft.getInstance();
    private final FirstPersonRenderer itemRenderer = new FirstPersonRenderer(this.mc);

    private int ticksSinceLastSwing;
    private AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.OFF;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent evt) {

        if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) {

            return;
        }

        if (evt instanceof RenderGameOverlayEvent.Pre) {

            this.modifyValues(player -> {

                this.ticksSinceLastSwing = player.ticksSinceLastSwing;
                player.ticksSinceLastSwing = (int) Math.ceil(player.getCooldownPeriod());
            }, gamesettings -> {

                this.attackIndicator = gamesettings.attackIndicator;
                gamesettings.attackIndicator = AttackIndicatorStatus.OFF;
            });
        } else if (evt instanceof RenderGameOverlayEvent.Post) {

            this.modifyValues(player -> player.ticksSinceLastSwing = this.ticksSinceLastSwing,
                    gamesettings -> gamesettings.attackIndicator = this.attackIndicator);
        }
    }

    private void modifyValues(Consumer<ClientPlayerEntity> noCooldown, Consumer<GameSettings> hideIndicator) {

        if (this.mc.player != null && CommonConfigHandler.REMOVE_ATTACK_COOLDOWN.get()) {

            // disable attack indicator from rendering
            noCooldown.accept(this.mc.player);
        }

        if (CommonConfigHandler.HIDE_ATTACK_INDICATOR.get()) {

            // disable attack indicator from rendering when pointing at a living entity
            hideIndicator.accept(this.mc.gameSettings);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onGuiInit(final GuiScreenEvent.InitGuiEvent.Post evt) {

        // no easy way to detect OptiFine, so this has to throw an exception once
        if (!this.hasOptiFine && CommonConfigHandler.HIDE_ATTACK_INDICATOR.get() && evt.getGui() instanceof VideoSettingsScreen) {

            try {

                // disable attack indicator button in video settings screen
                ((VideoSettingsScreen) evt.getGui()).optionsRowList.children().stream().flatMap(it -> it.children().stream()).filter(it -> it instanceof OptionButton)
                        .map(it -> (OptionButton) it).filter(it -> it.enumOptions.equals(AbstractOption.ATTACK_INDICATOR)).findFirst().ifPresent(it -> it.active = false);
            } catch (NoSuchFieldError ignored) {

                this.hasOptiFine = true;
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent evt) {

        if (CommonConfigHandler.REMOVE_ATTACK_COOLDOWN.get() && evt.phase == TickEvent.Phase.END) {

            if (this.mc.world != null && this.mc.player != null && !this.mc.isGamePaused()) {

                // calculate equipped progress in a separate item renderer where it's not reset occasionally
                this.mc.player.ticksSinceLastSwing = (int) Math.ceil(this.mc.player.getCooldownPeriod());
                this.itemRenderer.tick();
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderTick(final TickEvent.RenderTickEvent evt) {

        if (CommonConfigHandler.REMOVE_ATTACK_COOLDOWN.get() && evt.phase == TickEvent.Phase.START) {

            this.syncProgress(this.mc.getFirstPersonRenderer());
        }
    }

    private void syncProgress(FirstPersonRenderer itemRenderer) {

        itemRenderer.equippedProgressMainHand = this.itemRenderer.equippedProgressMainHand;
        itemRenderer.equippedProgressOffHand = this.itemRenderer.equippedProgressOffHand;
        itemRenderer.prevEquippedProgressMainHand = this.itemRenderer.prevEquippedProgressMainHand;
        itemRenderer.prevEquippedProgressOffHand = this.itemRenderer.prevEquippedProgressOffHand;
    }

}