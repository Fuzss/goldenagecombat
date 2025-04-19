package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.handler.AttackDamageBonusProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SwordItem.class)
abstract class SwordItemMixin extends Item implements AttackDamageBonusProvider {
    @Unique
    private float goldenagecombat$attackDamageBonus;

    public SwordItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties properties, CallbackInfo callback) {
        this.goldenagecombat$attackDamageBonus = material.attackDamageBonus();
    }

    @Override
    public float goldenagecombat$getAttackDamageBonus() {
        return this.goldenagecombat$attackDamageBonus;
    }
}
