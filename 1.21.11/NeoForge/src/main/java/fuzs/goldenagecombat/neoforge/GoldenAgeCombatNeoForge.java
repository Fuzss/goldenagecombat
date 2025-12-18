package fuzs.goldenagecombat.neoforge;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.data.ModDatapackRegistriesProvider;
import fuzs.goldenagecombat.data.tags.ModDamageTypeTagsProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.common.Mod;

@Mod(GoldenAgeCombat.MOD_ID)
public class GoldenAgeCombatNeoForge {

    public GoldenAgeCombatNeoForge() {
        ModConstructor.construct(GoldenAgeCombat.MOD_ID, GoldenAgeCombat::new);
        DataProviderHelper.registerDataProviders(GoldenAgeCombat.MOD_ID, ModDamageTypeTagsProvider::new);
        DataProviderHelper.registerDataProviders(GoldenAgeCombat.BOOSTED_SHARPNESS_ID,
                PackType.SERVER_DATA,
                ModDatapackRegistriesProvider::new);
    }
}
