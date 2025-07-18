package fuzs.goldenagecombat.fabric.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.client.GoldenAgeCombatClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class GoldenAgeCombatFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(GoldenAgeCombat.MOD_ID, GoldenAgeCombatClient::new);
    }
}
