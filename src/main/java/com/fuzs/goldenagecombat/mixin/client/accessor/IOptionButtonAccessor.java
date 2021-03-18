package com.fuzs.goldenagecombat.mixin.client.accessor;

import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.settings.AbstractOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionButton.class)
public interface IOptionButtonAccessor {

    @Accessor
    AbstractOption getEnumOptions();

}
