package fuzs.goldenagecombat.data;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.CommonConfig;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.data.v2.AbstractRegistriesDatapackGenerator;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;

public class DynamicEnchantmentRegistryProvider extends AbstractRegistriesDatapackGenerator.Enchantments {

    public DynamicEnchantmentRegistryProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    protected void addBootstrap(BootstrapContext<Enchantment> context) {
        // need this here to work across world restarts on Fabric
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isFabricLike() && !GoldenAgeCombat.CONFIG.get(
                CommonConfig.class).boostSharpness) {
            return;
        }
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        this.add(net.minecraft.world.item.enchantment.Enchantments.SHARPNESS, Enchantment.enchantment(
                        Enchantment.definition(items.getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE),
                                items.getOrThrow(ItemTags.SWORD_ENCHANTABLE), 10, 5, Enchantment.dynamicCost(1, 11),
                                Enchantment.dynamicCost(21, 11), 1, EquipmentSlotGroup.MAINHAND
                        ))
                .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.DAMAGE, new AddValue(LevelBasedValue.perLevel(1.25F))));
    }
}