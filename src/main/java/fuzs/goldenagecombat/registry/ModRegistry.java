package fuzs.goldenagecombat.registry;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

public class ModRegistry {
    public static final Tags.IOptionalNamedTag<Item> ATTACK_DAMAGE_BLACKLIST_TAG = ItemTags.createOptional(new ResourceLocation(GoldenAgeCombat.MOD_ID, "attack_damage_blacklist"));

    public static void touch() {

    }
}
