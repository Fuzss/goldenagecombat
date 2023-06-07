package fuzs.goldenagecombat.client.handler;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ClientConfig;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.goldenagecombat.core.CommonAbstractions;
import fuzs.goldenagecombat.handler.AttackAttributeHandler;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AttributesTooltipHandler {
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    protected static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    public static void onItemTooltip(ItemStack stack, @Nullable Player player, List<Component> lines, TooltipFlag context) {
        if (!GoldenAgeCombat.CONFIG.getHolder(ServerConfig.class).isAvailable()) return;
        if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).tooltip.removeAllAttributes || GoldenAgeCombat.CONFIG.get(ClientConfig.class).tooltip.oldAttributes) {
            final int startIndex = removeAllAttributes(lines);
            if (!GoldenAgeCombat.CONFIG.get(ClientConfig.class).tooltip.removeAllAttributes && startIndex != -1 && GoldenAgeCombat.CONFIG.get(ClientConfig.class).tooltip.oldAttributes) {
                addOldStyleAttributes(lines, stack, player, startIndex);
            }
            return;
        }
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).classic.removeCooldown) {
            removeAttribute(lines, Attributes.ATTACK_SPEED);
        }
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackReach) {
            replaceOrAddDefaultAttribute(lines, AttackAttributeHandler.BASE_ATTACK_REACH_UUID, stack, player);
        } else {
            removeAttribute(lines, CommonAbstractions.INSTANCE.getAttackRangeAttribute());
        }
        if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).tooltip.specialArmorAttributes && stack.getItem() instanceof ArmorItem item) {
            replaceOrAddDefaultAttribute(lines, ARMOR_MODIFIER_UUID_PER_SLOT[item.getType().getSlot().getIndex()], stack, player);
        }
    }

    private static void removeAttribute(List<Component> list, Attribute attribute) {
        list.removeIf(component -> compareToAttributeComponent(attribute, null, component));
    }

    private static boolean compareToAttributeComponent(Attribute attribute, @Nullable AttributeModifier attributemodifier, Component component) {
        TranslatableContents translatableComponent = null;
        if (component.getContents() instanceof TranslatableContents translatableContents) {
            translatableComponent = translatableContents;
        } else if (component instanceof MutableComponent mutableComponent && !mutableComponent.getSiblings().isEmpty() && mutableComponent.getSiblings().get(0).getContents() instanceof TranslatableContents translatableContents) {
            translatableComponent = translatableContents;
        }
        if (translatableComponent != null) {
            double scaledAmount = 0.0;
            String translationKey = null;
            if (attributemodifier != null) {
                double attributeAmount = attributemodifier.getAmount();
                scaledAmount = getScaledAttributeAmount(attributeAmount, attribute, attributemodifier);
                if (attributeAmount > 0.0D) {
                    translationKey = "attribute.modifier.plus." + attributemodifier.getOperation().toValue();
                } else if (attributeAmount < 0.0D) {
                    scaledAmount *= -1.0D;
                    translationKey = "attribute.modifier.take." + attributemodifier.getOperation().toValue();
                }
            }
            if ((attributemodifier == null || translationKey != null && translatableComponent.getKey().equals(translationKey)) && translatableComponent.getArgs().length >= 2) {
                final Object[] args = translatableComponent.getArgs();
                if ((attributemodifier == null || args[0].equals(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(scaledAmount))) && args[1] instanceof Component component1 && component1.getContents() instanceof TranslatableContents translatableComponent1) {
                    return translatableComponent1.getKey().equals(attribute.getDescriptionId());
                }
            }
        }
        return false;
    }

    private static void replaceOrAddDefaultAttribute(List<Component> list, UUID attributeId, ItemStack stack, @Nullable Player player) {
        final Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> map = getSlotToAttributeMap(stack);
        for (Map.Entry<EquipmentSlot, Multimap<Attribute, AttributeModifier>> slotToAttributeMap : map.entrySet()) {
            List<Map.Entry<Attribute, AttributeModifier>> attributeModifier = Lists.newArrayList();
            for (Map.Entry<Attribute, AttributeModifier> attributeToModifier : slotToAttributeMap.getValue().entries()) {
                if (attributeToModifier.getValue().getId().equals(attributeId)) {
                    attributeModifier.add(attributeToModifier);
                }
            }
            for (Map.Entry<Attribute, AttributeModifier> entry : attributeModifier) {
                final double attributeBaseAmount = entry.getValue().getAmount();
                double attributeAmount = attributeBaseAmount;
                if (player != null) {
                    attributeAmount += player.getAttributeBaseValue(entry.getKey());
                }
                if (attributeAmount == 0.0) continue;
                double scaledAmount = getScaledAttributeAmount(attributeAmount, entry.getKey(), entry.getValue());
                for (int i = 0; i < list.size(); i++) {
                    final Component component = list.get(i);
                    if (attributeBaseAmount != 0.0) {
                        if (compareToAttributeComponent(entry.getKey(), entry.getValue(), component)) {
                            list.set(i, Component.literal(" ").append(Component.translatable("attribute.modifier.equals." + entry.getValue().getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(scaledAmount), Component.translatable(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
                            break;
                        }
                    } else {
                        if (component.getContents() instanceof TranslatableContents translatableComponent && translatableComponent.getKey().equals("item.modifiers." + slotToAttributeMap.getKey().getName())) {
                            list.add(++i, Component.literal(" ").append(Component.translatable("attribute.modifier.equals." + entry.getValue().getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(scaledAmount), Component.translatable(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
                            break;
                        }
                    }
                }
            }
        }
    }

    private static double getScaledAttributeAmount(double attributeAmount, Attribute attribute, AttributeModifier attributemodifier) {
        double d1;
        if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
            if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) {
                d1 = attributeAmount * 10.0D;
            } else {
                d1 = attributeAmount;
            }
        } else {
            d1 = attributeAmount * 100.0D;
        }
        return d1;
    }

    private static int removeAllAttributes(List<Component> list) {
        int startIndex = findAttributesStart(list);
        if (startIndex > 0) {
            startIndex--;
            final int endIndex = findAttributesEnd(list);
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

    private static void addOldStyleAttributes(List<Component> list, ItemStack stack, @Nullable Player player, int startIndex) {
        final Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> map = getSlotToAttributeMap(stack);
        if (map.size() == 1) {
            List<Component> tmpList = Lists.newArrayList();
            addAttributesToTooltip(tmpList, player, stack, map.values().iterator().next());
            if (!tmpList.isEmpty()) {
                tmpList.add(0, CommonComponents.EMPTY);
                addListToTooltip(list, tmpList, startIndex);
            }
        } else if (map.size() > 1) {
            int lastSize = 0;
            for (Map.Entry<EquipmentSlot, Multimap<Attribute, AttributeModifier>> entry : map.entrySet()) {
                List<Component> tmpList = Lists.newArrayList();
                addAttributesToTooltip(tmpList, player, stack, entry.getValue());
                if (!tmpList.isEmpty()) {
                    tmpList.add(0, CommonComponents.EMPTY);
                    tmpList.add(1, Component.translatable("item.modifiers." + entry.getKey().getName()).withStyle(ChatFormatting.GRAY));
                    lastSize += tmpList.size();
                    addListToTooltip(list, tmpList, startIndex + lastSize);
                }
            }
        }
    }

    private static void addListToTooltip(List<Component> list, List<Component> tmpList, int startIndex) {
        if (startIndex < list.size()) {
            list.addAll(startIndex, tmpList);
        } else {
            list.addAll(tmpList);
        }
    }

    private static int findAttributesStart(List<Component> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getContents() instanceof TranslatableContents contents && contents.getKey().startsWith("item.modifiers.")) {
                return i;
            }
        }
        return -1;
    }

    private static int findAttributesEnd(List<Component> list) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            final Component component = list.get(i);
            TranslatableContents translatableComponent = null;
            if (component.getContents() instanceof TranslatableContents translatableComponent1) {
                translatableComponent = translatableComponent1;
            } else if (component.getContents() instanceof LiteralContents textComponent && textComponent.text().equals(" ")) {
                if (!component.getSiblings().isEmpty() && component.getSiblings().get(0).getContents() instanceof TranslatableContents translatableComponent1) {
                    translatableComponent = translatableComponent1;
                }
            }
            if (translatableComponent != null && translatableComponent.getKey().startsWith("attribute.modifier.")) {
                index = i;
            }
        }
        return index;
    }

    private static Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> getSlotToAttributeMap(ItemStack stack) {
        final Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> map = Maps.newHashMap();
        for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            Multimap<Attribute, AttributeModifier> multimap = stack.getAttributeModifiers(equipmentslot);
            if (!multimap.isEmpty()) {
                map.put(equipmentslot, multimap);
            }
        }
        return map;
    }

    private static void addAttributesToTooltip(List<Component> list, @Nullable Player player, ItemStack stack, Multimap<Attribute, AttributeModifier> multimap) {
        for (Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
            if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).classic.removeCooldown && entry.getKey().equals(Attributes.ATTACK_SPEED)) continue;
            if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackReach && entry.getKey().equals(CommonAbstractions.INSTANCE.getAttackRangeAttribute())) continue;
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
                } else if (attributemodifier.getId().equals(AttackAttributeHandler.BASE_ATTACK_REACH_UUID)) {
                    d0 += player.getAttributeBaseValue(CommonAbstractions.INSTANCE.getAttackRangeAttribute());
                    if (!ModLoaderEnvironment.INSTANCE.isForge()) d0 += 3.0;
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
            if (flag && d0 != 0.0 || d0 > 0.0D) {
                list.add(Component.translatable("attribute.modifier.plus." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.BLUE));
            } else if (d0 < 0.0D) {
                d1 *= -1.0D;
                list.add(Component.translatable("attribute.modifier.take." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.RED));
            }
        }
    }
}
