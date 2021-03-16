package com.fuzs.goldenagecombat.mixin.accessor;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface ILivingEntityAccessor {

    @Accessor
    int getTicksSinceLastSwing();

    @Accessor
    void setTicksSinceLastSwing(int ticksSinceLastSwing);

}
