package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageEnchantment.class)
abstract class DamageEnchantmentMixin extends Enchantment {
    @Shadow
    @Final
    public int type;

    protected DamageEnchantmentMixin(Rarity rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot[] equipmentSlots) {
        super(rarity, enchantmentCategory, equipmentSlots);
    }

    @Inject(method = "getDamageBonus", at = @At("HEAD"), cancellable = true)
    public void getDamageBonus(int level, MobType creatureType, CallbackInfoReturnable<Float> callback) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).boostSharpness) return;
        if (this.type == 0) callback.setReturnValue(level * 1.25F);
    }
}
