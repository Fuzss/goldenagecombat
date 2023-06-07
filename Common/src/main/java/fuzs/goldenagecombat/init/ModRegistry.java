package fuzs.goldenagecombat.init;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.instant(GoldenAgeCombat.MOD_ID);
    public static final TagKey<Item> SWORD_BLOCKING_EXCLUSIONS_TAG = REGISTRY.registerItemTag("sword_blocking_exclusions");
    public static final TagKey<Item> SWORD_BLOCKING_INCLUSIONS_TAG = REGISTRY.registerItemTag("sword_blocking_inclusions");

    public static void touch() {

    }
}