package fuzs.goldenagecombat.handler;

import com.google.common.collect.ImmutableMap;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.CommonConfig;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class AttackAttributeHandler {
    private static final Map<Class<? extends Item>, Double> ATTACK_DAMAGE_BONUS_OVERRIDES = ImmutableMap.of(SwordItem.class,
            4.0,
            AxeItem.class,
            3.0,
            PickaxeItem.class,
            2.0,
            ShovelItem.class,
            1.0,
            HoeItem.class,
            0.0);

    public static void onFinalizeItemComponents(Item item, Consumer<Function<DataComponentMap, DataComponentPatch>> consumer) {
        if (!GoldenAgeCombat.CONFIG.getHolder(CommonConfig.class).isAvailable()) return;
        if (!GoldenAgeCombat.CONFIG.get(CommonConfig.class).noItemDurabilityPenalty) return;
        if (item instanceof SwordItem) {
            consumer.accept((DataComponentMap dataComponents) -> {
                if (dataComponents.has(DataComponents.TOOL)) {
                    Tool tool = dataComponents.get(DataComponents.TOOL);
                    if (tool.damagePerBlock() == 2) {

                        Tool newTool = new Tool(tool.rules(), tool.defaultMiningSpeed(), 1);
                        return DataComponentPatch.builder().set(DataComponents.TOOL, newTool).build();
                    }
                }

                return DataComponentPatch.EMPTY;
            });
        }
    }

    public static void onComputeItemAttributeModifiers(Item item, List<ItemAttributeModifiers.Entry> itemAttributeModifiers) {
        if (!GoldenAgeCombat.CONFIG.getHolder(CommonConfig.class).isAvailable()) return;
        if (!setAttributeValue(item,
                itemAttributeModifiers,
                Attributes.ATTACK_DAMAGE,
                Item.BASE_ATTACK_DAMAGE_ID,
                GoldenAgeCombat.CONFIG.get(CommonConfig.class).attackDamageOverrides)) {
            if (GoldenAgeCombat.CONFIG.get(CommonConfig.class).oldAttackDamage) {
                for (Map.Entry<Class<? extends Item>, Double> entry : ATTACK_DAMAGE_BONUS_OVERRIDES.entrySet()) {
                    if (entry.getKey().isInstance(item) &&
                            item instanceof AttackDamageBonusProvider attackDamageBonusProvider) {
                        float attackDamageBonus = attackDamageBonusProvider.goldenagecombat$getAttackDamageBonus();
                        setAttributeValue(itemAttributeModifiers,
                                Attributes.ATTACK_DAMAGE,
                                Item.BASE_ATTACK_DAMAGE_ID,
                                attackDamageBonus + entry.getValue());
                        break;
                    }
                }
            }
        }
    }

    private static boolean setAttributeValue(Item item, List<ItemAttributeModifiers.Entry> itemAttributeModifiers, Holder<Attribute> attribute, ResourceLocation id, ConfigDataSet<Item> attackDamageOverrides) {
        if (attackDamageOverrides.contains(item)) {
            double newValue = attackDamageOverrides.<Double>getOptional(item, 0).orElseThrow();
            setAttributeValue(itemAttributeModifiers, attribute, id, newValue);
            return true;
        } else {
            return false;
        }
    }

    private static void setAttributeValue(List<ItemAttributeModifiers.Entry> itemAttributeModifiers, Holder<Attribute> attribute, ResourceLocation id, double newValue) {
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
                return;
            }
        }
        itemAttributeModifiers.add(newEntry);
    }
}
