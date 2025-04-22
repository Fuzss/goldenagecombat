package fuzs.goldenagecombat.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ClientConfig;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
abstract class ItemStackMixin implements DataComponentHolder {

    @WrapMethod(method = "addAttributeTooltips")
    private void addAttributeTooltips(Consumer<Component> tooltipAdder, TooltipDisplay tooltipDisplay, @Nullable Player player, Operation<Void> operation, @Share(
            "equipmentSlotGroups"
    ) LocalRef<Set<EquipmentSlotGroup>> equipmentSlotGroupsRef) {
        if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).attributesStyle == ClientConfig.AttributesStyle.LEGACY) {
            equipmentSlotGroupsRef.set(EnumSet.noneOf(EquipmentSlotGroup.class));
            List<Component> tooltipLines = new ArrayList<>();
            // we replace the component consumer with our own list, so we can later perform actions on all attribute lines
            // without having to filter them from all tooltip lines
            operation.call((Consumer<Component>) tooltipLines::add, tooltipDisplay, player);
            // this removes the equipment slot group lines when there are only attributes for a single group,
            // like attack damage and speed for the main hand
            if (this.goldenagecombat$allMatchSameEquipmentSlot(equipmentSlotGroupsRef.get())) {
                // remove all equipment slot group lines as well as the empty line above
                tooltipLines.removeIf((Component component) -> {
                    if (component == CommonComponents.EMPTY) {
                        return true;
                    } else {
                        TextColor color = component.getStyle().getColor();
                        return color != null && color.serialize().equals(ChatFormatting.GRAY.getName());
                    }
                });
                // add back one single empty line above all attributes
                if (!tooltipLines.isEmpty()) {
                    tooltipLines.addFirst(CommonComponents.EMPTY);
                }
            }
            tooltipLines.forEach(tooltipAdder);
        } else if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).attributesStyle ==
                ClientConfig.AttributesStyle.VANILLA) {
            operation.call(tooltipAdder, tooltipDisplay, player);
        }
    }

    @ModifyArg(
            method = "addAttributeTooltips",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V"
            )
    )
    private BiConsumer<Holder<Attribute>, AttributeModifier> addAttributeTooltips(EquipmentSlotGroup equipmentSlotGroup, BiConsumer<Holder<Attribute>, AttributeModifier> action, @Share(
            "equipmentSlotGroups"
    ) LocalRef<Set<EquipmentSlotGroup>> equipmentSlotGroupsRef) {
        return (Holder<Attribute> holder, AttributeModifier attributeModifier) -> {
            // just do nothing if this is the attack speed tooltip
            if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).removeAttackCooldown ||
                    !holder.is(Attributes.ATTACK_SPEED)) {
                action.accept(holder, attributeModifier);
                Set<EquipmentSlotGroup> equipmentSlotGroups = equipmentSlotGroupsRef.get();
                if (equipmentSlotGroups != null) {
                    equipmentSlotGroups.add(equipmentSlotGroup);
                }
            }
        };
    }

    @Unique
    private boolean goldenagecombat$allMatchSameEquipmentSlot(Collection<EquipmentSlotGroup> equipmentSlotGroups) {
        if (!equipmentSlotGroups.isEmpty()) {
            // test if there is an equipment slot that all groups match,
            // e.g. groups main hand, any, hand all match slot main hand
            $1:
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                for (EquipmentSlotGroup equipmentSlotGroup : equipmentSlotGroups) {
                    if (!equipmentSlotGroup.test(equipmentSlot)) {
                        continue $1;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @ModifyVariable(method = "addModifierTooltip", at = @At("LOAD"), ordinal = 0)
    private boolean addModifierTooltip(boolean isBaseAttributeModifierId) {
        // block the green tooltip formatting style for legacy type
        return isBaseAttributeModifierId &&
                GoldenAgeCombat.CONFIG.get(ClientConfig.class).attributesStyle != ClientConfig.AttributesStyle.LEGACY;
    }
}
