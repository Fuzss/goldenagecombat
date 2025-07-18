package fuzs.goldenagecombat.neoforge;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.fml.common.Mod;

@Mod(GoldenAgeCombat.MOD_ID)
public class GoldenAgeCombatNeoForge {

    public GoldenAgeCombatNeoForge() {
        ModConstructor.construct(GoldenAgeCombat.MOD_ID, GoldenAgeCombat::new);
    }
}
