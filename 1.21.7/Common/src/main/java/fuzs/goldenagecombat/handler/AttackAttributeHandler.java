package fuzs.goldenagecombat.handler;

import com.google.common.collect.ImmutableList;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.CommonConfig;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.Function;

public class AttackAttributeHandler {

    public static void onFinalizeItemComponents(Item item, Consumer<Function<DataComponentMap, DataComponentPatch>> consumer) {
        if (!GoldenAgeCombat.CONFIG.getHolder(CommonConfig.class).isAvailable()) return;
        if (GoldenAgeCombat.CONFIG.get(CommonConfig.class).noItemDurabilityPenalty) {
            consumer.accept((DataComponentMap dataComponents) -> {
                Weapon weapon = dataComponents.get(DataComponents.WEAPON);
                if (weapon != null && weapon.itemDamagePerAttack() == 2) {
                    return DataComponentPatch.builder()
                            .set(DataComponents.WEAPON, new Weapon(1, weapon.disableBlockingForSeconds()))
                            .build();
                }
                Tool tool = dataComponents.get(DataComponents.TOOL);
                if (tool != null && tool.damagePerBlock() == 2) {
                    return DataComponentPatch.builder()
                            .set(DataComponents.TOOL,
                                    new Tool(tool.rules(),
                                            tool.defaultMiningSpeed(),
                                            1,
                                            tool.canDestroyBlocksInCreative()))
                            .build();
                }

                return DataComponentPatch.EMPTY;
            });
        }
        consumer.accept((DataComponentMap dataComponents) -> {
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
            if (dataComponents.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY)
                    .modifiers() != itemAttributeModifiers) {
                return DataComponentPatch.builder()
                        .set(DataComponents.ATTRIBUTE_MODIFIERS,
                                new ItemAttributeModifiers(ImmutableList.copyOf(itemAttributeModifiers)))
                        .build();
            } else {
                return DataComponentPatch.EMPTY;
            }
        });
    }

    private static OptionalDouble getBaseAttackDamage(DataComponentMap dataComponents) {
        ToolMaterial toolMaterial = ToolMaterials.getToolMaterial(dataComponents);
        return toolMaterial != null ? OptionalDouble.of(toolMaterial.attackDamageBonus()) : OptionalDouble.empty();
    }

    private static OptionalDouble getAttackDamageBonus(DataComponentMap dataComponents) {
        if (ToolComponentsHelper.isComponentsForBlocks(dataComponents, BlockTags.SWORD_EFFICIENT)) {
            return OptionalDouble.of(4.0);
        } else if (ToolComponentsHelper.isComponentsForBlocks(dataComponents, BlockTags.MINEABLE_WITH_AXE)) {
            return OptionalDouble.of(3.0);
        } else if (ToolComponentsHelper.isComponentsForBlocks(dataComponents, BlockTags.MINEABLE_WITH_PICKAXE)) {
            return OptionalDouble.of(2.0);
        } else if (ToolComponentsHelper.isComponentsForBlocks(dataComponents, BlockTags.MINEABLE_WITH_SHOVEL)) {
            return OptionalDouble.of(1.0);
        } else if (ToolComponentsHelper.isComponentsForBlocks(dataComponents, BlockTags.MINEABLE_WITH_HOE)) {
            return OptionalDouble.of(0.0);
        } else {
            return OptionalDouble.empty();
        }
    }

    private static List<ItemAttributeModifiers.Entry> setAttributeValue(Item item, List<ItemAttributeModifiers.Entry> itemAttributeModifiers, Holder<Attribute> attribute, ResourceLocation id, ConfigDataSet<Item> attackDamageOverrides) {
        if (attackDamageOverrides.contains(item)) {
            double newValue = attackDamageOverrides.<Double>getOptional(item, 0).orElseThrow();
            return setAttributeValue(itemAttributeModifiers, attribute, id, newValue);
        } else {
            return itemAttributeModifiers;
        }
    }

    private static List<ItemAttributeModifiers.Entry> setAttributeValue(List<ItemAttributeModifiers.Entry> itemAttributeModifiers, Holder<Attribute> attribute, ResourceLocation id, double newValue) {
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
