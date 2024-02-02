package fuzs.goldenagecombat.fabric;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class GoldenAgeCombatFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(GoldenAgeCombat.MOD_ID, GoldenAgeCombat::new);
    }
}
