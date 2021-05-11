package com.fuzs.goldenagecombat.mixin;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.element.ClassicCombatElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@SuppressWarnings("unused")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityTypeIn, World worldIn) {

        super(entityTypeIn, worldIn);
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 180.0F), slice = @Slice(to = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;swingProgress:F")))
    public float getBackwardsRotation(float oldRotation) {

        ClassicCombatElement element = (ClassicCombatElement) GoldenAgeCombat.CLASSIC_COMBAT;
        return element.isEnabled() && element.backwardsWalking ? 0.0F : oldRotation;
    }

}
