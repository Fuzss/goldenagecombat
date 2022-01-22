package fuzs.goldenagecombat.registry;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

public class ModRegistry {
    public static final Tags.IOptionalNamedTag<Item> SWORD_BLOCKING_EXCLUSIONS_TAG = ItemTags.createOptional(new ResourceLocation(GoldenAgeCombat.MOD_ID, "sword_blocking_exclusions"));
    public static final Tags.IOptionalNamedTag<Item> SWORD_BLOCKING_INCLUSIONS_TAG = ItemTags.createOptional(new ResourceLocation(GoldenAgeCombat.MOD_ID, "sword_blocking_inclusions"));

    public static void touch() {

    }
}
