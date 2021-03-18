package com.fuzs.goldenagecombat.mixin;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.element.ClassicCombatElement;
import com.fuzs.goldenagecombat.mixin.accessor.IItemAccessor;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Redirect(method = "getAttributeModifiers", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getAttributeModifiers(Lnet/minecraft/inventory/EquipmentSlotType;Lnet/minecraft/item/ItemStack;)Lcom/google/common/collect/Multimap;", remap = false))
    public Multimap<String, AttributeModifier> getAttributeModifiers(Item item, EquipmentSlotType slot, ItemStack stack) {

        Multimap<String, AttributeModifier> multimap = item.getAttributeModifiers(slot, stack);
        ClassicCombatElement element = (ClassicCombatElement) GoldenAgeCombat.CLASSIC_COMBAT;
        if (element.isEnabled() && element.oldAttackDamage && !stack.getItem().isIn(ClassicCombatElement.ATTACK_DAMAGE_BLACKLIST_TAG)) {

            if (item instanceof TieredItem && slot == EquipmentSlotType.MAINHAND) {

                // always one less to account for base value of 1.0
                if (stack.getItem() instanceof SwordItem) {

                    this.replaceDamageAttribute(multimap, (TieredItem) stack.getItem(), 4.0F);
                } else if (stack.getItem() instanceof AxeItem) {

                    this.replaceDamageAttribute(multimap, (TieredItem) stack.getItem(), 3.0F);
                } else if (stack.getItem() instanceof PickaxeItem) {

                    this.replaceDamageAttribute(multimap, (TieredItem) stack.getItem(), 2.0F);
                } else if (stack.getItem() instanceof ShovelItem) {

                    this.replaceDamageAttribute(multimap, (TieredItem) stack.getItem(), 1.0F);
                } else if (stack.getItem() instanceof HoeItem) {

                    this.replaceDamageAttribute(multimap, (TieredItem) stack.getItem(), 0.0F);
                }
            }
        }

        return multimap;
    }

    private void replaceDamageAttribute(Multimap<String, AttributeModifier> multimap, TieredItem tieredItem, float damageBonus) {

        multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
        multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(IItemAccessor.getAttackDamageModifier(), GoldenAgeCombat.MODID + " modifier", tieredItem.getTier().getAttackDamage() + damageBonus, AttributeModifier.Operation.ADDITION));
    }

}
