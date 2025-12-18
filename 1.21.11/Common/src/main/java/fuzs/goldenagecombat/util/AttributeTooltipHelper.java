package fuzs.goldenagecombat.util;

import fuzs.goldenagecombat.world.item.component.LegacyItemAttributeModifiersDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class AttributeTooltipHelper {

    public static void addLegacyAttributeTooltips(ItemStack itemStack, Consumer<Component> tooltipAdder, TooltipDisplay tooltipDisplay, @Nullable Player player) {
        if (tooltipDisplay.shows(DataComponents.ATTRIBUTE_MODIFIERS)) {
            int shownEquipmentSlotGroups = getShownEquipmentSlotGroups(itemStack);
            for (EquipmentSlotGroup equipmentSlotGroup : EquipmentSlotGroup.values()) {
                MutableBoolean mutableBoolean = new MutableBoolean(true);
                itemStack.forEachModifier(equipmentSlotGroup,
                        (Holder<Attribute> holder, AttributeModifier attributeModifier, ItemAttributeModifiers.Display display) -> {
                            if (display != ItemAttributeModifiers.Display.hidden()) {
                                if (mutableBoolean.isTrue()) {
                                    tooltipAdder.accept(CommonComponents.EMPTY);
                                    if (shownEquipmentSlotGroups > 1) {
                                        tooltipAdder.accept(Component.translatable(
                                                        "item.modifiers." + equipmentSlotGroup.getSerializedName())
                                                .withStyle(ChatFormatting.GRAY));
                                    }
                                    mutableBoolean.setFalse();
                                }

                                LegacyItemAttributeModifiersDisplay.pick(display)
                                        .apply(tooltipAdder, player, holder, attributeModifier);
                            }
                        });
            }
        }
    }

    static int getShownEquipmentSlotGroups(ItemStack itemStack) {
        int shownEquipmentSlotGroups = 0;
        for (EquipmentSlotGroup equipmentSlotGroup : EquipmentSlotGroup.values()) {
            MutableBoolean mutableBoolean = new MutableBoolean();
            itemStack.forEachModifier(equipmentSlotGroup,
                    (Holder<Attribute> holder, AttributeModifier attributeModifier, ItemAttributeModifiers.Display display) -> {
                        if (display != ItemAttributeModifiers.Display.hidden()) {
                            mutableBoolean.setTrue();
                        }
                    });
            if (mutableBoolean.booleanValue()) {
                shownEquipmentSlotGroups++;
            }
        }

        return shownEquipmentSlotGroups;
    }
}
