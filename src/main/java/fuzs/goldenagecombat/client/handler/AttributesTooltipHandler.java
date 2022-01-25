package fuzs.goldenagecombat.client.handler;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AttributesTooltipHandler {
    protected static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    @SubscribeEvent
    public void onItemTooltip(final ItemTooltipEvent evt) {
        final List<Component> list = evt.getToolTip();
        if (GoldenAgeCombat.CONFIG.client().tooltip.removeAllAttributes || GoldenAgeCombat.CONFIG.client().tooltip.oldAttributes) {
            final int startIndex = this.removeAllAttributes(list);
            if (!GoldenAgeCombat.CONFIG.client().tooltip.removeAllAttributes && startIndex != -1 && GoldenAgeCombat.CONFIG.client().tooltip.oldAttributes) {
                this.addOldStyleAttributes(list, evt.getItemStack(), evt.getPlayer(), startIndex);
            }
        } else if (GoldenAgeCombat.CONFIG.client().tooltip.removeAttackSpeed) {
            this.removeAttackSpeedAttribute(list);
        }
    }

    private void removeAttackSpeedAttribute(List<Component> list) {
        list.removeIf(component -> component.toString().contains("attribute.name.generic.attack_speed"));
    }

    private int removeAllAttributes(List<Component> list) {
        int startIndex = this.findAttributesStart(list);
        if (startIndex > 0) {
            startIndex--;
            final int endIndex = this.findAttributesEnd(list);
            if (endIndex != -1 && endIndex > startIndex) {
                // remove start to end, both inclusive, also empty line above attributes
                for (int i = 0; i < endIndex - startIndex + 1; i++) {
                    // also remove empty line above
                    list.remove(startIndex);
                }
                return startIndex;
            }
        }
        return -1;
    }

    private void addOldStyleAttributes(List<Component> list, ItemStack stack, Player player, int startIndex) {
        final Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> map = this.getSlotToAttributeMap(stack);
        if (map.size() == 1) {
            List<Component> tmpList = Lists.newArrayList();
            this.addAttributesToTooltip(tmpList, player, stack, map.values().iterator().next());
            if (!tmpList.isEmpty()) {
                tmpList.add(0, TextComponent.EMPTY);
                this.addListToTooltip(list, tmpList, startIndex);
            }
        } else if (map.size() > 1) {
            int lastSize = 0;
            for (Map.Entry<EquipmentSlot, Multimap<Attribute, AttributeModifier>> entry : map.entrySet()) {
                List<Component> tmpList = Lists.newArrayList();
                this.addAttributesToTooltip(tmpList, player, stack, entry.getValue());
                if (!tmpList.isEmpty()) {
                    tmpList.add(0, TextComponent.EMPTY);
                    tmpList.add(1, new TranslatableComponent("item.modifiers." + entry.getKey().getName()).withStyle(ChatFormatting.GRAY));
                    lastSize += tmpList.size();
                    this.addListToTooltip(list, tmpList, startIndex + lastSize);
                }
            }
        }
    }

    private void addListToTooltip(List<Component> list, List<Component> tmpList, int startIndex) {
        if (startIndex < list.size()) {
            list.addAll(startIndex, tmpList);
        } else {
            list.addAll(tmpList);
        }
    }

    private int findAttributesStart(List<Component> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof TranslatableComponent component && component.getKey().startsWith("item.modifiers.")) {
                return i;
            }
        }
        return -1;
    }

    private int findAttributesEnd(List<Component> list) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            final Component component = list.get(i);
            TranslatableComponent translatableComponent = null;
            if (component instanceof TranslatableComponent translatableComponent1) {
                translatableComponent = translatableComponent1;
            } else if (component instanceof TextComponent textComponent && textComponent.getText().equals(" ")) {
                if (!textComponent.getSiblings().isEmpty() && textComponent.getSiblings().get(0) instanceof TranslatableComponent translatableComponent1) {
                    translatableComponent = translatableComponent1;
                }
            }
            if (translatableComponent != null && translatableComponent.getKey().startsWith("attribute.modifier.")) {
                index = i;
            }
        }
        return index;
    }

    private Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> getSlotToAttributeMap(ItemStack stack) {
        final Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> map = Maps.newHashMap();
        for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            Multimap<Attribute, AttributeModifier> multimap = stack.getAttributeModifiers(equipmentslot);
            if (!multimap.isEmpty()) {
                map.put(equipmentslot, multimap);
            }
        }
        return map;
    }

    private void addAttributesToTooltip(List<Component> list, Player player, ItemStack stack, Multimap<Attribute, AttributeModifier> multimap) {
        for (Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
            if (GoldenAgeCombat.CONFIG.client().tooltip.removeAttackSpeed && entry.getKey().equals(Attributes.ATTACK_SPEED)) continue;
            AttributeModifier attributemodifier = entry.getValue();
            double d0 = attributemodifier.getAmount();
            boolean flag = false;
            if (player != null) {
                if (attributemodifier.getId().equals(BASE_ATTACK_DAMAGE_UUID)) {
                    d0 += player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                    d0 += EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
                    flag = true;
                } else if (attributemodifier.getId().equals(BASE_ATTACK_SPEED_UUID)) {
                    d0 += player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                    flag = true;
                }
            }
            double d1;
            if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                    d1 = d0 * 10.0D;
                } else {
                    d1 = d0;
                }
            } else {
                d1 = d0 * 100.0D;
            }
            if (flag || d0 > 0.0D) {
                list.add((new TranslatableComponent("attribute.modifier.plus." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslatableComponent(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.BLUE));
            } else if (d0 < 0.0D) {
                d1 *= -1.0D;
                list.add((new TranslatableComponent("attribute.modifier.take." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslatableComponent(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.RED));
            }
        }
    }
}
