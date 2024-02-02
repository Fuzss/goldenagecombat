package fuzs.goldenagecombat.client.handler;

import com.google.common.collect.Multimap;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ClientConfig;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class AttributesTooltipHandler {

    public static void onItemTooltip(ItemStack stack, @Nullable Player player, List<Component> lines, TooltipFlag context) {

        if (!GoldenAgeCombat.CONFIG.getHolder(ServerConfig.class).isAvailable()) return;

        if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).attributesStyle != ClientConfig.AttributesStyle.MODERN) {
            int startIndex = AttributeTooltipHelper.removeAllAttributes(lines);
            if (startIndex != -1 && GoldenAgeCombat.CONFIG.get(ClientConfig.class).attributesStyle == ClientConfig.AttributesStyle.LEGACY) {
                addOldStyleAttributes(lines, stack, player, startIndex);
            }
        }

        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).removeAttackCooldown) {
            lines.removeIf(component -> AttributeTooltipHelper.matchesAttributeComponent(component, Attributes.ATTACK_SPEED, null));
        }
    }

    private static void addOldStyleAttributes(List<Component> lines, ItemStack stack, @Nullable Player player, int startIndex) {
        Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributesBySlot = AttributeTooltipHelper.getAttributesBySlot(stack);
        for (Map.Entry<EquipmentSlot, Multimap<Attribute, AttributeModifier>> entry : attributesBySlot.entrySet()) {
            List<Component> tmpList = Lists.newArrayList();
            addAttributesToTooltip(tmpList, player, stack, entry.getValue());
            if (!tmpList.isEmpty()) {
                tmpList.add(0, CommonComponents.EMPTY);
                if (attributesBySlot.size() > 1) {
                    tmpList.add(1, Component.translatable("item.modifiers." + entry.getKey().getName()).withStyle(ChatFormatting.GRAY));
                }
                lines.addAll(startIndex, tmpList);
                startIndex += tmpList.size();
            }
        }
    }

    private static void addAttributesToTooltip(List<Component> lines, @Nullable Player player, ItemStack stack, Multimap<Attribute, AttributeModifier> multimap) {

        // iterate like this to preserve order
        for (Attribute attribute : multimap.keySet()) {

            double attributeValue = AttributeTooltipHelper.calculateAttributeValue(player, attribute, multimap.get(attribute));

            if (attribute == Attributes.ATTACK_DAMAGE) attributeValue += EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
            if (attribute instanceof RangedAttribute rangedAttribute && rangedAttribute.getMaxValue() < 10.0) attributeValue *= 10.0 / rangedAttribute.getMaxValue();

            if (attributeValue > 0.0) {
                lines.add(Component.translatable("attribute.modifier.plus.0", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(attributeValue), Component.translatable(attribute.getDescriptionId())).withStyle(ChatFormatting.BLUE));
            } else if (attributeValue < 0.0) {
                // make value positive, adding a minus sign is handled by the translation string
                attributeValue *= -1.0;
                lines.add(Component.translatable("attribute.modifier.take.0", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(attributeValue), Component.translatable(attribute.getDescriptionId())).withStyle(ChatFormatting.RED));
            }
        }
    }
}
