package fuzs.goldenagecombat.client;

import fuzs.goldenagecombat.client.handler.AttributesTooltipHandler;
import fuzs.goldenagecombat.client.handler.ClientCooldownHandler;
import fuzs.goldenagecombat.client.handler.ItemInHandHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.ItemTooltipCallback;
import fuzs.puzzleslib.api.client.event.v1.RenderGuiElementEvents;
import fuzs.puzzleslib.api.client.event.v1.RenderHandCallback;
import fuzs.puzzleslib.api.client.event.v1.ScreenEvents;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;

public class GoldenAgeCombatClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        RenderHandCallback.EVENT.register(EventPhase.LAST, ItemInHandHandler::onRenderHand);
        RenderGuiElementEvents.before(RenderGuiElementEvents.CROSSHAIR).register(ClientCooldownHandler::onBeforeRenderGuiElement);
        RenderGuiElementEvents.after(RenderGuiElementEvents.CROSSHAIR).register(ClientCooldownHandler::onAfterRenderGuiElement);
        RenderGuiElementEvents.before(RenderGuiElementEvents.HOTBAR).register(ClientCooldownHandler::onBeforeRenderGuiElement);
        RenderGuiElementEvents.after(RenderGuiElementEvents.HOTBAR).register(ClientCooldownHandler::onAfterRenderGuiElement);
        ScreenEvents.AFTER_INIT.register(ClientCooldownHandler::onAfterInit);
        ItemTooltipCallback.EVENT.register(AttributesTooltipHandler::onItemTooltip);
    }
}