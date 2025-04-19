package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.CommonConfig;
import fuzs.goldenagecombat.handler.AttackDamageBonusProvider;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DiggerItem.class)
abstract class DiggerItemMixin extends Item implements AttackDamageBonusProvider {
    @Unique
    private float goldenagecombat$attackDamageBonus;

    public DiggerItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(ToolMaterial material, TagKey<Block> mineableBlocks, float attackDamage, float attackSpeed, Item.Properties properties, CallbackInfo callback) {
        this.goldenagecombat$attackDamageBonus = material.attackDamageBonus();
    }

    @Inject(method = "hurtEnemy", at = @At("HEAD"), cancellable = true)
    public void hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> callback) {
        if (!GoldenAgeCombat.CONFIG.get(CommonConfig.class).noItemDurabilityPenalty) return;
        ItemHelper.hurtAndBreak(itemStack, 1, attacker, EquipmentSlot.MAINHAND);
        callback.setReturnValue(true);
    }

    @Override
    public float goldenagecombat$getAttackDamageBonus() {
        return this.goldenagecombat$attackDamageBonus;
    }
}
