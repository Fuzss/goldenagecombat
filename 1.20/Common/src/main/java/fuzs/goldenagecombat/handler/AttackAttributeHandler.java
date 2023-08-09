package fuzs.goldenagecombat.handler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.goldenagecombat.mixin.accessor.ItemAccessor;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;

import java.util.Map;
import java.util.UUID;

public class AttackAttributeHandler {
    public static final UUID BASE_ATTACK_DAMAGE_UUID = ItemAccessor.goldenagecombat$getBaseAttackDamageUUID();
    private static final String ATTACK_DAMAGE_MODIFIER_NAME = GoldenAgeCombat.id("attack_damage_modifier").toString();
    private static final Map<Class<? extends TieredItem>, Double> ATTACK_DAMAGE_BONUS_OVERRIDES = ImmutableMap.of(SwordItem.class, 4.0, AxeItem.class, 3.0, PickaxeItem.class, 2.0, ShovelItem.class, 1.0, HoeItem.class, 4.0);

    public static void onItemAttributeModifiers(ItemStack stack, EquipmentSlot equipmentSlot, Multimap<Attribute, AttributeModifier> attributeModifiers, Multimap<Attribute, AttributeModifier> originalAttributeModifiers) {
        if (!GoldenAgeCombat.CONFIG.getHolder(ServerConfig.class).isAvailable()) return;
        // don't change items whose attributes have already been changed via the nbt tag
        if (equipmentSlot == EquipmentSlot.MAINHAND && (!stack.hasTag() || !stack.getTag().contains("AttributeModifiers", Tag.TAG_LIST))) {
            if (!trySetNewAttributeValue(stack, attributeModifiers, Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE_UUID, ATTACK_DAMAGE_MODIFIER_NAME, GoldenAgeCombat.CONFIG.get(ServerConfig.class).attackDamageOverrides)) {
                if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).oldAttackDamage) {
                    for (Map.Entry<Class<? extends TieredItem>, Double> entry : ATTACK_DAMAGE_BONUS_OVERRIDES.entrySet()) {
                        if (entry.getKey().isInstance(stack.getItem())) {
                            setNewAttributeValue(attributeModifiers, Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE_UUID, ATTACK_DAMAGE_MODIFIER_NAME, ((TieredItem) stack.getItem()).getTier().getAttackDamageBonus() + entry.getValue());
                            break;
                        }
                    }
                }
            }
        }
    }

    private static boolean trySetNewAttributeValue(ItemStack itemStack, Multimap<Attribute, AttributeModifier> attributeModifiers, Attribute attribute, UUID modifierUUID, String modifierName, ConfigDataSet<Item> attackDamageOverrides) {
        if (attackDamageOverrides.contains(itemStack.getItem())) {
            double newValue = attackDamageOverrides.<Double>getOptional(itemStack.getItem(), 0).orElseThrow();
            setNewAttributeValue(attributeModifiers, attribute, modifierUUID, modifierName, newValue);
            return true;
        }
        return false;
    }

    private static void setNewAttributeValue(Multimap<Attribute, AttributeModifier> attributeModifiers, Attribute attribute, UUID modifierUUID, String modifierName, double newValue) {
        attributeModifiers.removeAll(attribute);
        attributeModifiers.put(attribute, new AttributeModifier(modifierUUID, modifierName, newValue, AttributeModifier.Operation.ADDITION));
    }
}
