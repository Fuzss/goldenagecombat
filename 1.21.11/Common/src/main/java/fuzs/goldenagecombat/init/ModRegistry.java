package fuzs.goldenagecombat.init;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.puzzleslib.api.data.v2.AbstractDatapackRegistriesProvider;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;

public class ModRegistry {
    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder().add(Registries.ENCHANTMENT,
            ModRegistry::bootstrapEnchantments);
    static final RegistryManager REGISTRIES = RegistryManager.from(GoldenAgeCombat.MOD_ID);

    static final TagFactory TAGS = TagFactory.make(GoldenAgeCombat.MOD_ID);
    public static final TagKey<DamageType> BYPASSES_SWORD_BLOCK_DAMAGE_TYPE_TAG = TAGS.registerDamageTypeTag(
            "bypasses_sword_block");

    public static void bootstrap() {
        // NO-OP
    }

    /**
     * @see Enchantments#bootstrap(BootstrapContext)
     */
    public static void bootstrapEnchantments(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> itemLookup = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        AbstractDatapackRegistriesProvider.registerEnchantment(context,
                Enchantments.SHARPNESS,
                Enchantment.enchantment(Enchantment.definition(itemLookup.getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE),
                                itemLookup.getOrThrow(ItemTags.MELEE_WEAPON_ENCHANTABLE),
                                10,
                                5,
                                Enchantment.dynamicCost(1, 11),
                                Enchantment.dynamicCost(21, 11),
                                1,
                                EquipmentSlotGroup.MAINHAND))
                        .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                        .withEffect(EnchantmentEffectComponents.DAMAGE, new AddValue(LevelBasedValue.perLevel(1.25F))));
    }
}
