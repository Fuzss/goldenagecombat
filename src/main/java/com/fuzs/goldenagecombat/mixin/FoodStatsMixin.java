package com.fuzs.goldenagecombat.mixin;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.element.ClassicCombatElement;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings("unused")
@Mixin(FoodStats.class)
public abstract class FoodStatsMixin {

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 20))
    public int getRegenLevel(int regenLevel) {

        ClassicCombatElement element = (ClassicCombatElement) GoldenAgeCombat.CLASSIC_COMBAT;
        if (element.isEnabled() && element.noFastRegen) {

            return Integer.MAX_VALUE;
        }

        return regenLevel;
    }

}
