package fuzs.goldenagecombat.client.handler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A small helper class with a few utility methods for handling attribute related lines on an item tooltip.
 */
public final class AttributeTooltipHelper {

    private AttributeTooltipHelper() {
        // NO-OP
    }

    /**
     * Collect all {@link AttributeModifier}s on an {@link ItemStack} into a map separated by {@link EquipmentSlot}.
     *
     * @param itemStack the item stack
     * @return the map
     */
    public static Map<EquipmentSlot, Multimap<Holder<Attribute>, AttributeModifier>> getAttributesBySlot(ItemStack itemStack) {
        Map<EquipmentSlot, Multimap<Holder<Attribute>, AttributeModifier>> map = new LinkedHashMap<>();
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemAttributeModifiers itemAttributeModifiers = itemStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS,
                    ItemAttributeModifiers.EMPTY
            );
            itemAttributeModifiers.forEach(equipmentSlot, (holder, attributeModifier) -> {
                map.computeIfAbsent(equipmentSlot, $ -> HashMultimap.create()).put(holder, attributeModifier);
            });
        }
        return map;
    }

    /**
     * Tests if a component describes a given attribute and potential modifier.
     *
     * @param component         the component to compare to the provided attribute values
     * @param attribute         the attribute to compare to
     * @param attributeModifier a potential attribute modifier in case checking for a specific modifier is desired
     * @return does the component describe the given attribute and potential modifier
     */
    public static boolean matchesAttributeComponent(Component component, Holder<Attribute> attribute, @Nullable AttributeModifier attributeModifier) {
        TranslatableContents translatableContents = null;
        if (component.getContents() instanceof TranslatableContents translatableContents1) {
            translatableContents = translatableContents1;
        } else if (component instanceof MutableComponent mutableComponent && !mutableComponent.getSiblings().isEmpty() && mutableComponent.getSiblings().get(0).getContents() instanceof TranslatableContents translatableContents1) {
            translatableContents = translatableContents1;
        }
        if (translatableContents != null) {
            double scaledAmount = 0.0;
            String translationKey = null;
            if (attributeModifier != null) {
                scaledAmount = getScaledAttributeAmount(attribute, attributeModifier);
                if (attributeModifier.amount() > 0.0D) {
                    translationKey = "attribute.modifier.plus." + attributeModifier.operation().id();
                } else if (attributeModifier.amount() < 0.0D) {
                    scaledAmount *= -1.0D;
                    translationKey = "attribute.modifier.take." + attributeModifier.operation().id();
                }
            }
            Object[] args = translatableContents.getArgs();
            if ((attributeModifier == null || translationKey != null && translatableContents.getKey().equals(translationKey)) && args.length >= 2) {
                if (attributeModifier == null || args[0].equals(ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(scaledAmount))) {
                    if (args[1] instanceof Component component1 && component1.getContents() instanceof TranslatableContents translatableComponent1) {
                        return translatableComponent1.getKey().equals(attribute.value().getDescriptionId());
                    }
                }
            }
        }
        return false;
    }

    /**
     * Adjusts a given value for an attribute modifier for the tooltip just like vanilla does it.
     *
     * @param attribute         the attribute the value is for
     * @param attributeModifier the modifier where the value comes from
     * @return the adjusted value, potentially still the original input
     */
    private static double getScaledAttributeAmount(Holder<Attribute> attribute, AttributeModifier attributeModifier) {
        // apply same scaling to attribute value as is done by vanilla for the tooltip
        double attributeAmount = attributeModifier.amount();
        if (attributeModifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && attributeModifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
            if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) {
                return attributeAmount * 10.0D;
            } else {
                return attributeAmount;
            }
        } else {
            return attributeAmount * 100.0D;
        }
    }

    /**
     * Remove all tooltip lines related to attributes, as indicated by {@link #findAttributesStart(List)} and {@link #findAttributesEnd(List)}.
     *
     * @param lines tooltip lines to edit
     * @return the start index from where on attribute lines have been removed in the original tooltip
     */
    public static int removeAllAttributes(List<Component> lines) {
        int startIndex = findAttributesStart(lines);
        if (startIndex >= 0) {
            int endIndex = findAttributesEnd(lines);
            if (startIndex < endIndex) {
                // remove start to end, both inclusive, therefore +1
                for (int i = 0; i < endIndex - startIndex + 1; i++) {
                    lines.remove(startIndex);
                }
                // return start index when removal was successful for further processing
                return startIndex;
            }
        }
        return -1;
    }

    /**
     * Finds the index of the first attributes related line which is usually a blank line, otherwise returns <code>-1</code>.
     *
     * @param lines tooltip lines to analyze
     * @return the found index
     */
    public static int findAttributesStart(List<Component> lines) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).getContents() instanceof TranslatableContents contents && contents.getKey().startsWith("item.modifiers.")) {
                // attributes have a blank line above, we try to include that
                if (--i >= 0 && lines.get(i).getContents() == PlainTextContents.EMPTY) {
                    return i;
                } else {
                    return ++i;
                }
            }
        }
        return -1;
    }

    /**
     * Finds the index of the last attributes related line, otherwise returns <code>-1</code>.
     *
     * @param lines tooltip lines to analyze
     * @return the found index
     */
    public static int findAttributesEnd(List<Component> lines) {
        int index = -1;
        for (int i = 0; i < lines.size(); i++) {
            final Component component = lines.get(i);
            TranslatableContents translatableComponent = null;
            if (component.getContents() instanceof TranslatableContents translatableComponent1) {
                translatableComponent = translatableComponent1;
            } else if (component.getContents() instanceof PlainTextContents.LiteralContents textComponent && textComponent.text().equals(" ")) {
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

    /**
     * Calculates the total value of an {@link Attribute} from a list of {@link AttributeModifier}s.
     * <p>This method is copied from {@link AttributeInstance#calculateValue()}.
     *
     * @param player    a player to get a base value from, otherwise <code>0.0</code> is used
     * @param attribute the attribute to calculate
     * @param modifiers all modifiers for that attribute
     * @return the total attribute value
     */
    public static double calculateAttributeValue(@Nullable Player player, Holder<Attribute> attribute, Collection<AttributeModifier> modifiers) {

        double baseValue = player != null && player.getAttributes().hasAttribute(attribute) ? player.getAttributeBaseValue(attribute) : 0.0;
        Map<AttributeModifier.Operation, List<AttributeModifier>> modifiersByOperation = modifiers.stream().collect(Collectors.groupingBy(AttributeModifier::operation));

        for (AttributeModifier attributeModifier : modifiersByOperation.getOrDefault(AttributeModifier.Operation.ADD_VALUE, List.of())) {
            baseValue += attributeModifier.amount();
        }

        double multipliedValue = baseValue;

        for (AttributeModifier attributeModifier : modifiersByOperation.getOrDefault(AttributeModifier.Operation.ADD_MULTIPLIED_BASE, List.of())) {
            multipliedValue += baseValue * attributeModifier.amount();
        }

        for (AttributeModifier attributeModifier : modifiersByOperation.getOrDefault(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, List.of())) {
            multipliedValue *= 1.0D + attributeModifier.amount();
        }

        return attribute.value().sanitizeValue(multipliedValue);
    }
}
