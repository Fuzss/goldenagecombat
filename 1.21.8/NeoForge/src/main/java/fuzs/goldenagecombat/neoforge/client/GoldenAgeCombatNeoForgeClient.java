package fuzs.goldenagecombat.neoforge.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.client.GoldenAgeCombatClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = GoldenAgeCombat.MOD_ID, dist = Dist.CLIENT)
public class GoldenAgeCombatNeoForgeClient {

    public GoldenAgeCombatNeoForgeClient() {
        ClientModConstructor.construct(GoldenAgeCombat.MOD_ID, GoldenAgeCombatClient::new);
    }
}
