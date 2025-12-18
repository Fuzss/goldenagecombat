package fuzs.goldenagecombat.handler;

import com.google.common.collect.ImmutableList;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.CommonConfig;
import fuzs.goldenagecombat.init.ModRegistry;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemComponentsHandler {

    public static void onFinalizeItemComponents(Item item, Consumer<Function<DataComponentMap, DataComponentPatch>> consumer) {
        if (!GoldenAgeCombat.CONFIG.getHolder(CommonConfig.class).isAvailable()) {
            return;
        }

        if (GoldenAgeCombat.CONFIG.get(CommonConfig.class).noItemDurabilityPenalty) {
            consumer.accept((DataComponentMap dataComponents) -> {
                DataComponentPatch weaponPatch = getWeaponPatch(dataComponents);
                if (weaponPatch != null) {
                    return weaponPatch;
                } else {
                    DataComponentPatch toolPatch = getToolPatch(dataComponents);
                    return toolPatch != null ? toolPatch : DataComponentPatch.EMPTY;
                }
            });
        }

        if (GoldenAgeCombat.CONFIG.get(CommonConfig.class).allowSwordBlocking) {
            consumer.accept(ItemComponentsHandler::getBlocksAttacksPatch);
        }

        if (GoldenAgeCombat.CONFIG.get(CommonConfig.class).oldAttackDamage) {
            consumer.accept((DataComponentMap dataComponents) -> {
                return getAttackDamagePatch(item, dataComponents);
            });
        }

        if (GoldenAgeCombat.CONFIG.get(CommonConfig.class).removeAttackCooldown) {
            consumer.accept(ItemComponentsHandler::getAttackSpeedPatch);
        }
    }

    private static @Nullable DataComponentPatch getWeaponPatch(DataComponentMap dataComponents) {
        Weapon weapon = dataComponents.get(DataComponents.WEAPON);
        if (weapon != null && weapon.itemDamagePerAttack() == 2) {
            return DataComponentPatch.builder()
                    .set(DataComponents.WEAPON, new Weapon(1, weapon.disableBlockingForSeconds()))
                    .build();
        } else {
            return null;
        }
    }

    private static @Nullable DataComponentPatch getToolPatch(DataComponentMap dataComponents) {
        Tool tool = dataComponents.get(DataComponents.TOOL);
        if (tool != null && tool.damagePerBlock() == 2) {
            return DataComponentPatch.builder()
                    .set(DataComponents.TOOL,
                            new Tool(tool.rules(), tool.defaultMiningSpeed(), 1, tool.canDestroyBlocksInCreative()))
                    .build();
        } else {
            return null;
        }
    }

    private static DataComponentPatch getBlocksAttacksPatch(DataComponentMap dataComponents) {
        Weapon weapon = dataComponents.get(DataComponents.WEAPON);
        if (weapon != null && weapon.itemDamagePerAttack() == 1 && weapon.disableBlockingForSeconds() == 0.0F) {
            return DataComponentPatch.builder().set(DataComponents.BLOCKS_ATTACKS,
                    // The original blocking angle should be 360 degrees, but reduce it to be more inline with shield balancing.
                    // The hurt sound does not play when blocking, so use the hurt sound itself as the blocking sound.
                    new BlocksAttacks(0.0F,
                            0.0F,
                            List.of(new BlocksAttacks.DamageReduction(180.0F, Optional.empty(), 0.0F, 0.5F)),
                            new BlocksAttacks.ItemDamageFunction(0.0F, 0.0F, 0.0F),
                            Optional.of(ModRegistry.BYPASSES_SWORD_BLOCK_DAMAGE_TYPE_TAG),
                            Optional.of(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.GENERIC_HURT)),
                            Optional.empty())).build();
        } else {
            return DataComponentPatch.EMPTY;
        }
    }

    private static DataComponentPatch getAttackDamagePatch(Item item, DataComponentMap dataComponents) {
        List<ItemAttributeModifiers.Entry> itemAttributeModifiers = dataComponents.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS,
                ItemAttributeModifiers.EMPTY).modifiers();
        List<ItemAttributeModifiers.Entry> itemAttributeModifiers2 = setAttributeValue(item,
                itemAttributeModifiers,
                Attributes.ATTACK_DAMAGE,
                Item.BASE_ATTACK_DAMAGE_ID,
                GoldenAgeCombat.CONFIG.get(CommonConfig.class).attackDamageOverrides);
        if (itemAttributeModifiers == itemAttributeModifiers2) {
            OptionalDouble baseAttackDamage = getBaseAttackDamage(dataComponents);
            OptionalDouble attackDamageBonus = getAttackDamageBonus(dataComponents);
            if (baseAttackDamage.isPresent() && attackDamageBonus.isPresent()) {
                itemAttributeModifiers = setAttributeValue(itemAttributeModifiers,
                        Attributes.ATTACK_DAMAGE,
                        Item.BASE_ATTACK_DAMAGE_ID,
                        baseAttackDamage.getAsDouble() + attackDamageBonus.getAsDouble());
            }
        } else {
            itemAttributeModifiers = itemAttributeModifiers2;
        }

        if (dataComponents.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers()
                != itemAttributeModifiers) {
            return DataComponentPatch.builder()
                    .set(DataComponents.ATTRIBUTE_MODIFIERS,
                            new ItemAttributeModifiers(ImmutableList.copyOf(itemAttributeModifiers)))
                    .build();
        } else {
            return DataComponentPatch.EMPTY;
        }
    }

    private static DataComponentPatch getAttackSpeedPatch(DataComponentMap dataComponents) {
        List<ItemAttributeModifiers.Entry> itemAttributeModifiers = dataComponents.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS,
                ItemAttributeModifiers.EMPTY).modifiers();
        List<ItemAttributeModifiers.Entry> itemAttributeModifiers2 = hideAttribute(itemAttributeModifiers,
                Attributes.ATTACK_SPEED);
        if (itemAttributeModifiers != itemAttributeModifiers2) {
            return DataComponentPatch.builder()
                    .set(DataComponents.ATTRIBUTE_MODIFIERS,
                            new ItemAttributeModifiers(ImmutableList.copyOf(itemAttributeModifiers2)))
                    .build();
        } else {
            return DataComponentPatch.EMPTY;
        }
    }

    private static List<ItemAttributeModifiers.Entry> hideAttribute(List<ItemAttributeModifiers.Entry> itemAttributeModifiers, Holder<Attribute> holder) {
        for (ItemAttributeModifiers.Entry entry : itemAttributeModifiers) {
            if (entry.attribute().is(holder)) {
                List<ItemAttributeModifiers.Entry> newItemAttributeModifiers = new ArrayList<>(itemAttributeModifiers);
                ListIterator<ItemAttributeModifiers.Entry> iterator = newItemAttributeModifiers.listIterator();
                while (iterator.hasNext()) {
                    ItemAttributeModifiers.Entry newEntry = iterator.next();

                    if (newEntry.attribute().is(holder)) {
                        iterator.set(new ItemAttributeModifiers.Entry(newEntry.attribute(),
                                newEntry.modifier(),
                                newEntry.slot(),
                                ItemAttributeModifiers.Display.hidden()));
                    }
                }

                return newItemAttributeModifiers;
            }
        }

        return itemAttributeModifiers;
    }

    private static OptionalDouble getBaseAttackDamage(DataComponentMap dataComponents) {
        ToolMaterial toolMaterial = ToolMaterials.getToolMaterial(dataComponents);
        return toolMaterial != null ? OptionalDouble.of(toolMaterial.attackDamageBonus()) : OptionalDouble.empty();
    }

    private static OptionalDouble getAttackDamageBonus(DataComponentMap dataComponents) {
        if (ToolComponentsHelper.hasComponentsForBlocks(dataComponents, BlockTags.SWORD_EFFICIENT)) {
            return OptionalDouble.of(4.0);
        } else if (ToolComponentsHelper.hasComponentsForBlocks(dataComponents, BlockTags.MINEABLE_WITH_AXE)) {
            return OptionalDouble.of(3.0);
        } else if (ToolComponentsHelper.hasComponentsForBlocks(dataComponents, BlockTags.MINEABLE_WITH_PICKAXE)) {
            return OptionalDouble.of(2.0);
        } else if (ToolComponentsHelper.hasComponentsForBlocks(dataComponents, BlockTags.MINEABLE_WITH_SHOVEL)) {
            return OptionalDouble.of(1.0);
        } else if (ToolComponentsHelper.hasComponentsForBlocks(dataComponents, BlockTags.MINEABLE_WITH_HOE)) {
            return OptionalDouble.of(0.0);
        } else {
            return OptionalDouble.empty();
        }
    }

    private static List<ItemAttributeModifiers.Entry> setAttributeValue(Item item, List<ItemAttributeModifiers.Entry> itemAttributeModifiers, Holder<Attribute> attribute, Identifier id, ConfigDataSet<Item> attackDamageOverrides) {
        if (attackDamageOverrides.contains(item)) {
            double newValue = attackDamageOverrides.<Double>getOptional(item, 0).orElseThrow();
            return setAttributeValue(itemAttributeModifiers, attribute, id, newValue);
        } else {
            return itemAttributeModifiers;
        }
    }

    private static List<ItemAttributeModifiers.Entry> setAttributeValue(List<ItemAttributeModifiers.Entry> itemAttributeModifiers, Holder<Attribute> attribute, Identifier id, double newValue) {
        itemAttributeModifiers = new ArrayList<>(itemAttributeModifiers);
        AttributeModifier attributeModifier = new AttributeModifier(id,
                newValue,
                AttributeModifier.Operation.ADD_VALUE);
        ItemAttributeModifiers.Entry newEntry = new ItemAttributeModifiers.Entry(attribute,
                attributeModifier,
                EquipmentSlotGroup.MAINHAND);
        ListIterator<ItemAttributeModifiers.Entry> iterator = itemAttributeModifiers.listIterator();
        while (iterator.hasNext()) {
            ItemAttributeModifiers.Entry entry = iterator.next();
            if (entry.slot() == EquipmentSlotGroup.MAINHAND && entry.matches(attribute, id)) {
                iterator.set(newEntry);
                return itemAttributeModifiers;
            }
        }

        itemAttributeModifiers.add(newEntry);
        return itemAttributeModifiers;
    }
}
