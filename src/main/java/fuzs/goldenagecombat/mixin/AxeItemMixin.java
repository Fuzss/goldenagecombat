package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin extends DiggerItem {
    public AxeItemMixin(float p_150810_, float p_150811_, Tier p_150812_, TagKey<Block> p_150813_, Properties p_150814_) {
        super(p_150810_, p_150811_, p_150812_, p_150813_, p_150814_);
    }

    @Override
    public boolean hurtEnemy(ItemStack p_40994_, LivingEntity p_40995_, LivingEntity p_40996_) {
        if (!GoldenAgeCombat.CONFIG.server().combatTests.noAxeAttackPenalty) return super.hurtEnemy(p_40994_, p_40995_, p_40996_);
        p_40994_.hurtAndBreak(1, p_40996_, (p_41007_) -> {
            p_41007_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }
}
