package fuzs.goldenagecombat.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.client.handler.*;
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
        final ItemInHandHandler itemInHandHandler = new ItemInHandHandler();
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, itemInHandHandler::onRenderHand);
        final AttributesTooltipHandler attributesTooltipHandler = new AttributesTooltipHandler();
        MinecraftForge.EVENT_BUS.addListener(attributesTooltipHandler::onItemTooltip);
        final SwordBlockingRenderer swordBlockingRenderer = new SwordBlockingRenderer();
        MinecraftForge.EVENT_BUS.addListener(swordBlockingRenderer::onRenderHand);
        final ClientCooldownHandler clientCooldownHandler = new ClientCooldownHandler();
        MinecraftForge.EVENT_BUS.addListener(clientCooldownHandler::onRenderGameOverlay$Pre);
        MinecraftForge.EVENT_BUS.addListener(clientCooldownHandler::onRenderGameOverlay$Post);
        MinecraftForge.EVENT_BUS.addListener(clientCooldownHandler::onClientTick);
        final AttackIndicatorOptionHandler attackIndicatorOptionHandler = new AttackIndicatorOptionHandler();
        MinecraftForge.EVENT_BUS.addListener(attackIndicatorOptionHandler::onScreenInit);
        MinecraftForge.EVENT_BUS.addListener(attackIndicatorOptionHandler::onDrawScreen);
        final AttackAirHandler attackAirHandler = new AttackAirHandler();
        MinecraftForge.EVENT_BUS.addListener(attackAirHandler::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(attackAirHandler::onLeftClickEmpty$holdAttack);
        MinecraftForge.EVENT_BUS.addListener(attackAirHandler::onLeftClickEmpty$airAttack);
    }
}
