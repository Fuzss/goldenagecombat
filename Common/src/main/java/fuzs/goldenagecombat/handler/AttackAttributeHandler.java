package fuzs.goldenagecombat.handler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.goldenagecombat.core.CommonAbstractions;
import fuzs.goldenagecombat.mixin.accessor.ItemAccessor;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;

import java.util.Map;
import java.util.UUID;

public class AttackAttributeHandler {
    public static final UUID BASE_ATTACK_RANGE_UUID = UUID.fromString("26cb07a3-209d-4110-8e10-1010243614c8");
    private static final String ATTACK_DAMAGE_MODIFIER_NAME = GoldenAgeCombat.id("attack_damage_modifier").toString();
    private static final String ATTACK_RANGE_MODIFIER_NAME = GoldenAgeCombat.id("attack_range_modifier").toString();
    private static final Map<Class<? extends TieredItem>, Double> ATTACK_DAMAGE_BONUS_OVERRIDES = ImmutableMap.<Class<? extends TieredItem>, Double>builder().put(SwordItem.class, 4.0).put(AxeItem.class, 3.0).put(PickaxeItem.class, 2.0).put(ShovelItem.class, 1.0).put(HoeItem.class, 4.0).build();
    private static final Map<Class<?>, Double> ATTACK_RANGE_BONUS_OVERRIDES = ImmutableMap.<Class<?>, Double>builder().put(TridentItem.class, 0.5).put(HoeItem.class, 0.5).put(SwordItem.class, 0.5).put(TieredItem.class, 0.5).build();

    public static void onItemAttributeModifiers$0(ItemStack stack, EquipmentSlot equipmentSlot, Multimap<Attribute, AttributeModifier> attributeModifiers, Multimap<Attribute, AttributeModifier> originalAttributeModifiers) {
        if (!GoldenAgeCombat.CONFIG.getHolder(ServerConfig.class).isAvailable() || !GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.oldAttackDamage) return;
        // don't change items whose attributes have already been changed via the nbt tag
        if (equipmentSlot == EquipmentSlot.MAINHAND && (!stack.hasTag() || !stack.getTag().contains("AttributeModifiers", Tag.TAG_LIST))) {
            if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackDamageOverrides.contains(stack.getItem())) {
                replaceDamageAttribute(attributeModifiers, GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackDamageOverrides.<Double>getOptional(stack.getItem(), 0).orElseThrow());
            } else {
                for (Map.Entry<Class<? extends TieredItem>, Double> entry : ATTACK_DAMAGE_BONUS_OVERRIDES.entrySet()) {
                    if (entry.getKey().isInstance(stack.getItem())) {
                        replaceDamageAttribute(attributeModifiers, (TieredItem) stack.getItem(), entry.getValue());
                    }
                }
            }
        }
    }

    private static void replaceDamageAttribute(Multimap<Attribute, AttributeModifier> attributeModifiers, TieredItem item, double damageBonus) {
        replaceDamageAttribute(attributeModifiers, item.getTier().getAttackDamageBonus() + damageBonus);
    }

    private static void replaceDamageAttribute(Multimap<Attribute, AttributeModifier> attributeModifiers, double newValue) {
        attributeModifiers.removeAll(Attributes.ATTACK_DAMAGE);
        attributeModifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ItemAccessor.goldenagecombat$getBaseAttackDamageUUID(), ATTACK_DAMAGE_MODIFIER_NAME, newValue, AttributeModifier.Operation.ADDITION));
    }

    public static void onItemAttributeModifiers$1(ItemStack stack, EquipmentSlot equipmentSlot, Multimap<Attribute, AttributeModifier> attributeModifiers, Multimap<Attribute, AttributeModifier> originalAttributeModifiers) {
        if (!GoldenAgeCombat.CONFIG.getHolder(ServerConfig.class).isAvailable() || !GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackRange) return;
        // don't change items whose attributes have already been changed via the nbt tag
        if (equipmentSlot == EquipmentSlot.MAINHAND && (!stack.hasTag() || !stack.getTag().contains("AttributeModifiers", Tag.TAG_LIST))) {
            if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackReachOverrides.contains(stack.getItem())) {
                setReachAttribute(attributeModifiers, GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackReachOverrides.<Double>getOptional(stack.getItem(), 0).orElseThrow());
            } else {
                for (Map.Entry<Class<?>, Double> entry : ATTACK_RANGE_BONUS_OVERRIDES.entrySet()) {
                    if (entry.getKey().isInstance(stack.getItem())) {
                        setReachAttribute(attributeModifiers, entry.getValue());
                    }
                }
            }
        }
    }

    private static void setReachAttribute(Multimap<Attribute, AttributeModifier> attributeModifiers, double newValue) {
        attributeModifiers.put(CommonAbstractions.INSTANCE.getAttackRangeAttribute(), new AttributeModifier(BASE_ATTACK_RANGE_UUID, ATTACK_RANGE_MODIFIER_NAME, newValue, AttributeModifier.Operation.ADDITION));
    }
}
