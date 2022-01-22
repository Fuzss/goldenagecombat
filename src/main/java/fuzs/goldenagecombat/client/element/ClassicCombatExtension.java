package fuzs.goldenagecombat.client.element;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.handler.ClassicCombatHandler;
import fuzs.puzzleslib.config.annotation.Config;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Optional;

public class ClassicCombatExtension {
    private final Minecraft minecraft = Minecraft.getInstance();
    private final Component disabledIndicatorComponent = new TextComponent(String.format("Attack Indicator has been disabled by %s mod", GoldenAgeCombat.MOD_NAME));
    private List<FormattedCharSequence> disabledIndicatorProcessor;

    private AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.OFF;

    @Config(name = "hide_attack_indicator", description = "Prevent attack indicator from rendering regardless of what's been set for the \"Attack Indicator\" option in video settings.")
    public boolean hideAttackIndicator = true;
    @Config(name = "inflate_hitboxes", description = "Expand all entity hitboxes by 10%, making hitting a target possible from a slightly greater range and with much increased accuracy.")
    public boolean inflateHitboxes = false;
    @Config(name = "quick_slowdown", description = "When slowing down movement or stopping completely momentum is lost much quicker.")
    public boolean quickSlowdown = false;

    @Override
    public void setupClient() {
        final ClassicCombatExtension classicCombatExtension = new ClassicCombatExtension();
        MinecraftForge.EVENT_BUS.addListener(classicCombatExtension::onRenderGameOverlay);
        MinecraftForge.EVENT_BUS.addListener(classicCombatExtension::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(classicCombatExtension::onGuiInit);
        MinecraftForge.EVENT_BUS.addListener(classicCombatExtension::onDrawScreen);
    }

    @Override
    public void initClient() {

        // leads to NullPointerException if ran much earlier
        this.disabledIndicatorProcessor = this.minecraft.font.split(this.disabledIndicatorComponent, 200);
    }

    @Override
    public void setupClientConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Expand all entity hitboxes by 10%, making hitting a target possible from a slightly greater range and with much increased accuracy.").define("Inflate Hitboxes", false), v -> this.inflateHitboxes = v);
        addToConfig(builder.comment("When slowing down movement or stopping completely momentum is lost much quicker.").define("Quick Slowdown", false), v -> this.quickSlowdown = v);
        addToConfig(builder.comment("Prevent attack indicator from rendering regardless of what's been set for the \"Attack Indicator\" option in video settings.").define("Remove Attack Indicator", true), v -> this.hideAttackIndicator = v);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent evt) {
        if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (evt instanceof RenderGameOverlayEvent.Pre) {
            if (GoldenAgeCombat.CONFIG.server().classic.removeCooldown) {
                // this will mostly just remove the attack indicator, except for one niche case when looking at an entity
                // just for that reason the whole indicator is also disabled later on
                ClassicCombatHandler.disableCooldownPeriod(this.minecraft.player);
            }
            if (this.hideAttackIndicator) {
                // indicator would otherwise render when looking at an entity, even when there is no cooldown
                this.attackIndicator = this.minecraft.options.attackIndicator;
                this.minecraft.options.attackIndicator = AttackIndicatorStatus.OFF;
            }
        } else if (evt instanceof RenderGameOverlayEvent.Post) {
            if (this.hideAttackIndicator) {
                // reset to old value
                this.minecraft.options.attackIndicator = this.attackIndicator;
            }
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

    @SubscribeEvent
    public void onGuiInit(final ScreenEvent.InitScreenEvent.Post evt) {

        if (this.hideAttackIndicator && evt.getScreen() instanceof VideoSettingsScreen) {

            // disable attack indicator button in video settings screen
            getOptionsRowList(evt.getScreen())
                    .flatMap(optionsRowList -> optionsRowList.getEventListeners().stream()
                            .flatMap(optionsRow -> optionsRow.getEventListeners().stream())
                            .filter(optionButton -> optionButton instanceof OptionButton)
                            .filter(optionButton -> ((OptionButton) optionButton).func_238517_a_() == AbstractOption.ATTACK_INDICATOR)
                            .findFirst())
                    .ifPresent(eventListener -> ((Widget) eventListener).active = false);
        }
    }

    @SubscribeEvent
    public void onDrawScreen(final ScreenEvent.DrawScreenEvent.Post evt) {
        if (this.hideAttackIndicator && evt.getScreen() instanceof VideoSettingsScreen) {

            // render tooltip explaining why the button is disabled
            getOptionsRowList(evt.getScreen())
                    .flatMap(rowList -> getWidgetAtPosition(rowList, evt.getMouseX(), evt.getMouseY())
                            .filter(widget -> widget instanceof OptionButton)
                            .filter(widget -> ((OptionButton) widget).func_238517_a_() == AbstractOption.ATTACK_INDICATOR))
                    .ifPresent(widget -> evt.getGui().renderTooltip(evt.getMatrixStack(), this.disabledIndicatorProcessor, evt.getMouseX(), evt.getMouseY()));
        }
    }

    private static Optional<OptionsRowList> getOptionsRowList(Screen screen) {

        // don't access optionsRowList field directly using reflection or an accessor since OptiFine removes it
        return screen.getEventListeners().stream()
                .filter(listener -> listener instanceof OptionsRowList)
                .findFirst()
                .map(listener -> (OptionsRowList) listener);
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
