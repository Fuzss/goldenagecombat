package fuzs.goldenagecombat.client;

import fuzs.goldenagecombat.client.handler.ClientCooldownHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenEvents;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;

public class GoldenAgeCombatClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        RenderGuiEvents.BEFORE.register(ClientCooldownHandler::onBeforeRenderGui);
        RenderGuiEvents.AFTER.register(ClientCooldownHandler::onAfterRenderGui);
        ScreenEvents.afterInit(VideoSettingsScreen.class).register(ClientCooldownHandler::onAfterInit);
    }
}
