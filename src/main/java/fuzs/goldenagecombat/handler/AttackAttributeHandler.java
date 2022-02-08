package fuzs.goldenagecombat.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.mixin.accessor.ItemAccessor;
import fuzs.goldenagecombat.registry.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AttackAttributeHandler {
    public static final UUID BASE_ATTACK_REACH_UUID = UUID.fromString("26cb07a3-209d-4110-8e10-1010243614c8");

    @SubscribeEvent
    public void onItemAttributeModifier$Damage(final ItemAttributeModifierEvent evt) {
        if (!GoldenAgeCombat.CONFIG.server().attributes.oldAttackDamage) return;
        if (evt.getSlotType() != EquipmentSlot.MAINHAND) return;
        ItemStack stack = evt.getItemStack();
        // don't change items whose attributes have already been changed via the nbt tag
        if (!stack.hasTag() || !stack.getTag().contains("AttributeModifiers", 9)) {
            if (GoldenAgeCombat.CONFIG.server().attributes.attackDamageOverrides.containsKey(stack.getItem())) {
                this.replaceDamageAttribute(evt::removeAttribute, evt::addModifier, GoldenAgeCombat.CONFIG.server().attributes.attackDamageOverrides.get(stack.getItem()));
            } else if (stack.getItem() instanceof TieredItem) {
                // always one less to account for base value of 1.0
                if (stack.getItem() instanceof SwordItem) {
                    this.replaceDamageAttribute(evt::removeAttribute, evt::addModifier, (TieredItem) stack.getItem(), 4.0);
                } else if (stack.getItem() instanceof AxeItem) {
                    this.replaceDamageAttribute(evt::removeAttribute, evt::addModifier, (TieredItem) stack.getItem(), 3.0);
                } else if (stack.getItem() instanceof PickaxeItem) {
                    this.replaceDamageAttribute(evt::removeAttribute, evt::addModifier, (TieredItem) stack.getItem(), 2.0);
                } else if (stack.getItem() instanceof ShovelItem) {
                    this.replaceDamageAttribute(evt::removeAttribute, evt::addModifier, (TieredItem) stack.getItem(), 1.0);
                } else if (stack.getItem() instanceof HoeItem) {
                    this.replaceDamageAttribute(evt::removeAttribute, evt::addModifier, (TieredItem) stack.getItem(), 0.0);
                }
            }
        }
    }

    private void replaceDamageAttribute(Consumer<Attribute> removeAttribute, BiConsumer<Attribute, AttributeModifier> addModifier, TieredItem item, double damageBonus) {
        this.replaceDamageAttribute(removeAttribute, addModifier, item.getTier().getAttackDamageBonus() + damageBonus);
    }

    private void replaceDamageAttribute(Consumer<Attribute> removeAttribute, BiConsumer<Attribute, AttributeModifier> addModifier, double newValue) {
        removeAttribute.accept(Attributes.ATTACK_DAMAGE);
        addModifier.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(ItemAccessor.getBaseAttackDamageUUID(), new ResourceLocation(GoldenAgeCombat.MOD_ID, "attack_damage_modifier").toString(), newValue, AttributeModifier.Operation.ADDITION));
    }

    @SubscribeEvent
    public void onItemAttributeModifier$Reach(final ItemAttributeModifierEvent evt) {
        if (!GoldenAgeCombat.CONFIG.server().attributes.increasedAttackReach) return;
        if (evt.getSlotType() != EquipmentSlot.MAINHAND) return;
        ItemStack stack = evt.getItemStack();
        if (GoldenAgeCombat.CONFIG.server().attributes.attackReachOverrides.containsKey(stack.getItem())) {
            this.setReachAttribute(evt::addModifier, GoldenAgeCombat.CONFIG.server().attributes.attackReachOverrides.get(stack.getItem()));
        } else if (stack.getItem() instanceof TridentItem) {
            this.setReachAttribute(evt::addModifier, 1.0);
        } else if (stack.getItem() instanceof SwordItem || stack.getItem() instanceof HoeItem) {
            this.setReachAttribute(evt::addModifier, 0.5);
        } else if (stack.getItem() instanceof TieredItem) {
            this.setReachAttribute(evt::addModifier, 0.0);
        }
    }

    private void setReachAttribute(BiConsumer<Attribute, AttributeModifier> addModifier, double newValue) {
        addModifier.accept(ModRegistry.ATTACK_REACH_ATTRIBUTE.get(), new AttributeModifier(BASE_ATTACK_REACH_UUID, new ResourceLocation(GoldenAgeCombat.MOD_ID, "attack_reach_modifier").toString(), newValue, AttributeModifier.Operation.ADDITION));
    }
}
