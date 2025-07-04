package fuzs.goldenagecombat.world.item.component;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Removes the green attribute modifier option.
 *
 * @see ItemAttributeModifiers.Display.Default
 */
public class LegacyItemAttributeModifiersDisplay implements ItemAttributeModifiers.Display {
    static final LegacyItemAttributeModifiersDisplay INSTANCE = new LegacyItemAttributeModifiersDisplay();

    public static ItemAttributeModifiers.Display pick(ItemAttributeModifiers.Display display) {
        return display == ItemAttributeModifiers.Display.attributeModifiers() ? INSTANCE : display;
    }

    @Override
    public ItemAttributeModifiers.Display.Type type() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void apply(Consumer<Component> output, @Nullable Player player, Holder<Attribute> attribute, AttributeModifier modifier) {
        double modifierAmount = modifier.amount();
        if (player != null) {
            if (modifier.is(Item.BASE_ATTACK_DAMAGE_ID)) {
                modifierAmount += player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
            } else if (modifier.is(Item.BASE_ATTACK_SPEED_ID)) {
                modifierAmount += player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
            }
        }

        double scaledModifierAmount;
        if (modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                || modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
            scaledModifierAmount = modifierAmount * 100.0;
        } else if (attribute.is(Attributes.KNOCKBACK_RESISTANCE)) {
            scaledModifierAmount = modifierAmount * 10.0;
        } else {
            scaledModifierAmount = modifierAmount;
        }

        if (modifierAmount > 0.0F) {
            output.accept(Component.translatable("attribute.modifier.plus." + modifier.operation().id(),
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(scaledModifierAmount),
                            Component.translatable(attribute.value().getDescriptionId()))
                    .withStyle(attribute.value().getStyle(true)));
        } else if (modifierAmount < 0.0F) {
            output.accept(Component.translatable("attribute.modifier.take." + modifier.operation().id(),
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(-scaledModifierAmount),
                            Component.translatable(attribute.value().getDescriptionId()))
                    .withStyle(attribute.value().getStyle(false)));
        }
    }
}
