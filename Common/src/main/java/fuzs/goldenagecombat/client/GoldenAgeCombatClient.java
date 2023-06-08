package fuzs.goldenagecombat.client;

import fuzs.goldenagecombat.client.handler.*;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.*;

public class GoldenAgeCombatClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        RenderHandCallback.EVENT.register(FirstPersonRenderingHandler::onRenderHand);
        ScreenEvents.AFTER_INIT.register(AttackIndicatorOptionHandler::onAfterInit);
        RenderHandCallback.EVENT.register(ItemInHandHandler::onRenderHand);
        RenderGuiElementEvents.before(RenderGuiElementEvents.CROSSHAIR).register(ClientCooldownHandler::onBeforeRenderGuiElement);
        RenderGuiElementEvents.after(RenderGuiElementEvents.CROSSHAIR).register(ClientCooldownHandler::onAfterRenderGuiElement);
        InteractionInputEvents.ATTACK.register(AttackAirHandler::onAttackInteraction);
        ClientTickEvents.END.register(AttackAirHandler::onEndTick);
        ItemTooltipCallback.EVENT.register(AttributesTooltipHandler::onItemTooltip);
    }
}
