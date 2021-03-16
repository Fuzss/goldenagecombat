package com.fuzs.goldenagecombat.mixin.client.accessor;

import net.minecraft.client.renderer.FirstPersonRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FirstPersonRenderer.class)
public interface IFirstPersonRendererAccessor {

    @Accessor
    float getEquippedProgressMainHand();

    @Accessor
    float getPrevEquippedProgressMainHand();

    @Accessor
    float getEquippedProgressOffHand();

    @Accessor
    float getPrevEquippedProgressOffHand();

    @Accessor
    void setEquippedProgressMainHand(float equippedProgressMainHand);

    @Accessor
    void setPrevEquippedProgressMainHand(float prevEquippedProgressMainHand);

    @Accessor
    void setEquippedProgressOffHand(float equippedProgressOffHand);

    @Accessor
    void setPrevEquippedProgressOffHand(float prevEquippedProgressOffHand);
    
}
