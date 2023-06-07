package fuzs.goldenagecombat.handler;

import fuzs.goldenagecombat.mixin.accessor.ItemAccessor;
import com.google.common.collect.Multimap;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.goldenagecombat.core.CommonAbstractions;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;

import java.util.UUID;

public class AttackAttributeHandler {
    public static final UUID BASE_ATTACK_REACH_UUID = UUID.fromString("26cb07a3-209d-4110-8e10-1010243614c8");

    public static void onItemAttributeModifiers$0(ItemStack stack, EquipmentSlot equipmentSlot, Multimap<Attribute, AttributeModifier> attributeModifiers, Multimap<Attribute, AttributeModifier> originalAttributeModifiers) {
        if (!GoldenAgeCombat.CONFIG.getHolder(ServerConfig.class).isAvailable() || !GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.oldAttackDamage) return;
        if (equipmentSlot != EquipmentSlot.MAINHAND) return;
        // don't change items whose attributes have already been changed via the nbt tag
        if (!stack.hasTag() || !stack.getTag().contains("AttributeModifiers", Tag.TAG_LIST)) {
            if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackDamageOverrides.contains(stack.getItem())) {
                replaceDamageAttribute(attributeModifiers, (double) GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackDamageOverrides.get(stack.getItem())[0]);
            } else if (stack.getItem() instanceof SwordItem) {
                replaceDamageAttribute(attributeModifiers, (TieredItem) stack.getItem(), 4.0);
            } else if (stack.getItem() instanceof AxeItem) {
                replaceDamageAttribute(attributeModifiers, (TieredItem) stack.getItem(), 3.0);
            } else if (stack.getItem() instanceof PickaxeItem) {
                replaceDamageAttribute(attributeModifiers, (TieredItem) stack.getItem(), 2.0);
            } else if (stack.getItem() instanceof ShovelItem) {
                replaceDamageAttribute(attributeModifiers, (TieredItem) stack.getItem(), 1.0);
            } else if (stack.getItem() instanceof HoeItem) {
                replaceDamageAttribute(attributeModifiers, (TieredItem) stack.getItem(), 0.0);
            }
        }
    }

    private static void replaceDamageAttribute(Multimap<Attribute, AttributeModifier> attributeModifiers, TieredItem item, double damageBonus) {
        replaceDamageAttribute(attributeModifiers, item.getTier().getAttackDamageBonus() + damageBonus);
    }

    private static void replaceDamageAttribute(Multimap<Attribute, AttributeModifier> attributeModifiers, double newValue) {
        attributeModifiers.removeAll(Attributes.ATTACK_DAMAGE);
        attributeModifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ItemAccessor.goldenagecombat$getBaseAttackDamageUUID(), new ResourceLocation(GoldenAgeCombat.MOD_ID, "attack_damage_modifier").toString(), newValue, AttributeModifier.Operation.ADDITION));
    }

    public static void onItemAttributeModifiers$1(ItemStack stack, EquipmentSlot equipmentSlot, Multimap<Attribute, AttributeModifier> attributeModifiers, Multimap<Attribute, AttributeModifier> originalAttributeModifiers) {
        if (!GoldenAgeCombat.CONFIG.getHolder(ServerConfig.class).isAvailable() || !GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackReach) return;
        if (equipmentSlot != EquipmentSlot.MAINHAND) return;
        // don't change items whose attributes have already been changed via the nbt tag
        if (!stack.hasTag() || !stack.getTag().contains("AttributeModifiers", Tag.TAG_LIST)) {
            if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackReachOverrides.contains(stack.getItem())) {
                setReachAttribute(attributeModifiers, (double) GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackReachOverrides.get(stack.getItem())[0]);
            } else if (stack.getItem() instanceof TridentItem || stack.getItem() instanceof HoeItem) {
                setReachAttribute(attributeModifiers, 1.0);
            } else if (stack.getItem() instanceof SwordItem) {
                setReachAttribute(attributeModifiers, 0.5);
            } else if (stack.getItem() instanceof TieredItem) {
                setReachAttribute(attributeModifiers, 0.0);
            }
        }
    }

    private static void setReachAttribute(Multimap<Attribute, AttributeModifier> attributeModifiers, double newValue) {
        attributeModifiers.put(CommonAbstractions.INSTANCE.getAttackRangeAttribute(), new AttributeModifier(BASE_ATTACK_REACH_UUID, new ResourceLocation(GoldenAgeCombat.MOD_ID, "attack_reach_modifier").toString(), newValue, AttributeModifier.Operation.ADDITION));
    }
}
