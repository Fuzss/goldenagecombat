package fuzs.goldenagecombat.client;

import fuzs.goldenagecombat.client.handler.AttributesTooltipHandler;
import fuzs.goldenagecombat.client.handler.ClientCooldownHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.gui.ItemTooltipCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenEvents;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;

public class GoldenAgeCombatClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        RenderGuiLayerEvents.before(RenderGuiLayerEvents.CROSSHAIR).register(ClientCooldownHandler::onBeforeRenderGuiLayer);
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.CROSSHAIR).register(ClientCooldownHandler::onAfterRenderGuiLayer);
        RenderGuiLayerEvents.before(RenderGuiLayerEvents.HOTBAR).register(ClientCooldownHandler::onBeforeRenderGuiLayer);
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.HOTBAR).register(ClientCooldownHandler::onAfterRenderGuiLayer);
        ScreenEvents.afterInit(VideoSettingsScreen.class).register(ClientCooldownHandler::onAfterInit);
//        ItemTooltipCallback.EVENT.register(AttributesTooltipHandler::onItemTooltip);
    }
}
