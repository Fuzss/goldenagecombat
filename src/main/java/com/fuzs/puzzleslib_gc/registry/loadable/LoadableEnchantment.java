package com.fuzs.puzzleslib_gc.registry.loadable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

/**
 * a basic enchantment extension which can be unloaded depending on a configuration state
 */
@SuppressWarnings("NullableProblems")
public abstract class LoadableEnchantment extends Enchantment {

    /**
     * vanilla constructor for passing through values
     */
    public LoadableEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {

        super(rarityIn, typeIn, slots);
    }

    /**
     * @return is this enchantment enabled
     */
    protected abstract boolean isEnabled();

    @Override
    public boolean canApply(ItemStack stack) {

        return this.isEnabled() && super.canApply(stack);
    }

    @Override
    public boolean canVillagerTrade() {

        return this.isEnabled() && super.canVillagerTrade();
    }

    @Override
    public boolean canGenerateInLoot() {

        return this.isEnabled() && super.canGenerateInLoot();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        return this.isEnabled() && super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public boolean isAllowedOnBooks() {

        return this.isEnabled() && super.isAllowedOnBooks();
    }

}
