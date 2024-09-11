package fuzs.goldenagecombat.mixin;

import com.llamalad7.mixinextras.sugar.Cancellable;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
abstract class ItemStackMixin implements DataComponentHolder {

    @ModifyVariable(method = "addAttributeTooltips", at = @At("HEAD"), argsOnly = true)
    private Consumer<Component> addAttributeTooltips(Consumer<Component> tooltipAdder, @Share("tooltipLines") LocalRef<List<Component>> tooltipLinesRef, @Share("equipmentSlotGroups") LocalRef<Set<EquipmentSlotGroup>> equipmentSlotGroupsRef, @Share("tooltipAdder") LocalRef<Consumer<Component>> tooltipAdderRef, @Cancellable CallbackInfo callback) {
        if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).attributesStyle == ClientConfig.AttributesStyle.LEGACY) {
            // we replace the component consumer with our own list, so we can later perform actions on all attribute lines
            // without having to filter them from all tooltip lines
            equipmentSlotGroupsRef.set(EnumSet.noneOf(EquipmentSlotGroup.class));
            tooltipAdderRef.set(tooltipAdder);
            tooltipLinesRef.set(new ArrayList<>());
            return tooltipLinesRef.get()::add;
        } else if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).attributesStyle == ClientConfig.AttributesStyle.NONE) {
            callback.cancel();
        }

        return tooltipAdder;
    }

    @ModifyArg(method = "addAttributeTooltips", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V"))
    private BiConsumer<Holder<Attribute>, AttributeModifier> addAttributeTooltips(EquipmentSlotGroup equipmentSlotGroup, BiConsumer<Holder<Attribute>, AttributeModifier> action, @Share("equipmentSlotGroups") LocalRef<Set<EquipmentSlotGroup>> equipmentSlotGroupsRef) {
        return (Holder<Attribute> holder, AttributeModifier attributeModifier) -> {
            // just do nothing if this is the attack speed tooltip
            if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).removeAttackCooldown || !holder.is(
                    Attributes.ATTACK_SPEED)) {
                action.accept(holder, attributeModifier);
                Set<EquipmentSlotGroup> equipmentSlotGroups = equipmentSlotGroupsRef.get();
                if (equipmentSlotGroups != null) {
                    equipmentSlotGroups.add(equipmentSlotGroup);
                }
            }
        };
    }

    @Inject(method = "addAttributeTooltips", at = @At("TAIL"))
    private void addAttributeTooltips(Consumer<Component> tooltipAdder, @Nullable Player player, CallbackInfo callback, @Share("tooltipLines") LocalRef<List<Component>> tooltipLinesRef, @Share("equipmentSlotGroups") LocalRef<Set<EquipmentSlotGroup>> equipmentSlotGroupsRef, @Share("tooltipAdder") LocalRef<Consumer<Component>> tooltipAdderRef) {
        List<Component> tooltipLines = tooltipLinesRef.get();
        if (tooltipLines != null) {
            if (this.allMatchSameEquipmentSlot(equipmentSlotGroupsRef.get())) {
                tooltipLines.removeIf(component -> {
                    return component == CommonComponents.EMPTY || component.getStyle().getColor().serialize().equals(
                            ChatFormatting.GRAY.getName());
                });
                if (!tooltipLines.isEmpty()) {
                    tooltipLines.addFirst(CommonComponents.EMPTY);
                }
            }
            tooltipLines.forEach(tooltipAdderRef.get());
        }
    }

//    @Inject(method = "addAttributeTooltips", at = @At("HEAD"), cancellable = true)
//    private void addAttributeTooltips(Consumer<Component> tooltipAdder, @Nullable Player player, CallbackInfo callback) {
//        if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).attributesStyle == ClientConfig.AttributesStyle.MODERN) {
//            return;
//        } else {
//            callback.cancel();
//        }
//        if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).attributesStyle == ClientConfig.AttributesStyle.NONE) {
//            return;
//        }
//        ItemAttributeModifiers itemAttributeModifiers = this.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS,
//                ItemAttributeModifiers.EMPTY
//        );
//        if (itemAttributeModifiers.showInTooltip()) {
//            List<Component> tooltipLines = new ArrayList<>();
//            Set<EquipmentSlotGroup> equipmentSlotGroups = EnumSet.noneOf(EquipmentSlotGroup.class);
//            for (EquipmentSlotGroup equipmentSlotGroup : EquipmentSlotGroup.values()) {
//                MutableBoolean mutableBoolean = new MutableBoolean(true);
//                this.forEachModifier(equipmentSlotGroup,
//                        (Holder<Attribute> holder, AttributeModifier attributeModifier) -> {
//                            equipmentSlotGroups.add(equipmentSlotGroup);
//                            if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).removeAttackCooldown || !holder.is(
//                                    Attributes.ATTACK_SPEED)) {
//                                if (mutableBoolean.isTrue()) {
//                                    tooltipLines.add(CommonComponents.EMPTY);
//                                    tooltipLines.add(Component.translatable(
//                                                    "item.modifiers." + equipmentSlotGroup.getSerializedName())
//                                            .withStyle(ChatFormatting.GRAY));
//                                    mutableBoolean.setFalse();
//                                }
//
//                                this.addModifierTooltip(tooltipLines::add, player, holder, attributeModifier);
//                            }
//                        }
//                );
//            }
//            if (this.allMatchSameEquipmentSlot(equipmentSlotGroups)) {
//                tooltipLines.removeIf(component -> {
//                    return component == CommonComponents.EMPTY || component.getStyle().getColor().serialize().equals(
//                            ChatFormatting.GRAY.getName());
//                });
//                if (!tooltipLines.isEmpty()) {
//                    tooltipLines.addFirst(CommonComponents.EMPTY);
//                }
//            }
//
//            tooltipLines.forEach(tooltipAdder);
//        }
//    }

    private boolean allMatchSameEquipmentSlot(Collection<EquipmentSlotGroup> equipmentSlotGroups) {

        if (!equipmentSlotGroups.isEmpty()) {
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

    @Shadow
    public abstract void forEachModifier(EquipmentSlotGroup slotGroup, BiConsumer<Holder<Attribute>, AttributeModifier> action);

    @Shadow
    private void addModifierTooltip(Consumer<Component> tooltipAdder, @Nullable Player player, Holder<Attribute> attribute, AttributeModifier modifier) {
        throw new RuntimeException();
    }

    @ModifyVariable(method = "addModifierTooltip", at = @At("LOAD"), ordinal = 0)
    private boolean addModifierTooltip(boolean customAttributeFormat) {
        // block the green tooltip formatting style for legacy type
        return customAttributeFormat && GoldenAgeCombat.CONFIG.get(ClientConfig.class).attributesStyle !=
                ClientConfig.AttributesStyle.LEGACY;
    }
}
