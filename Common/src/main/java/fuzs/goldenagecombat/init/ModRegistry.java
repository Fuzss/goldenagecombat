package fuzs.goldenagecombat.init;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.instant(GoldenAgeCombat.MOD_ID);
    public static final RegistryReference<SoundEvent> ITEM_SWORD_BLOCK_SOUND_EVENT = REGISTRY.registerSoundEvent("item.sword.block");

    public static final TagKey<Item> EXCLUDED_FROM_SWORD_BLOCKING_ITEM_TAG = REGISTRY.registerItemTag("excluded_from_sword_blocking");
    public static final TagKey<Item> INCLUDED_FOR_SWORD_BLOCKING_ITEM_TAG = REGISTRY.registerItemTag("included_for_sword_blocking");
    public static final TagKey<Item> OVERRIDES_SWORD_BLOCKING_ITEM_TAG = REGISTRY.registerItemTag("overrides_sword_blocking");

    public static void touch() {

    }
}