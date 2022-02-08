package fuzs.goldenagecombat.registry;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.puzzleslib.registry.RegistryManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRegistry {
    private static final RegistryManager REGISTRY = RegistryManager.of(GoldenAgeCombat.MOD_ID);
    public static final RegistryObject<Attribute> ATTACK_REACH_ATTRIBUTE = REGISTRY.register(ForgeRegistries.ATTRIBUTES, "generic.attack_reach", () -> new RangedAttribute("attribute.name.generic.attack_reach", 3.0, 0.0, 1024.0).setSyncable(true));

    public static final Tags.IOptionalNamedTag<Item> SWORD_BLOCKING_EXCLUSIONS_TAG = ItemTags.createOptional(new ResourceLocation(GoldenAgeCombat.MOD_ID, "sword_blocking_exclusions"));
    public static final Tags.IOptionalNamedTag<Item> SWORD_BLOCKING_INCLUSIONS_TAG = ItemTags.createOptional(new ResourceLocation(GoldenAgeCombat.MOD_ID, "sword_blocking_inclusions"));

    public static void touch() {

    }
}