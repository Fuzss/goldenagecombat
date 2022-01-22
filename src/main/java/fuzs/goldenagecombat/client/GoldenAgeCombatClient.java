package fuzs.goldenagecombat.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.client.element.AttackSpeedTooltipHandler;
import fuzs.goldenagecombat.client.element.LegacyAnimationsRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = GoldenAgeCombat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GoldenAgeCombatClient {
    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        registerHandlers();
    }

    private static void registerHandlers() {
        final LegacyAnimationsRenderer legacyAnimationsRenderer = new LegacyAnimationsRenderer();
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, legacyAnimationsRenderer::onRenderHand);
        MinecraftForge.EVENT_BUS.addListener(legacyAnimationsRenderer::onRenderGameOverlay);
        final AttackSpeedTooltipHandler attackSpeedTooltipHandler = new AttackSpeedTooltipHandler();
        MinecraftForge.EVENT_BUS.addListener(attackSpeedTooltipHandler::onItemTooltip);
    }
}
