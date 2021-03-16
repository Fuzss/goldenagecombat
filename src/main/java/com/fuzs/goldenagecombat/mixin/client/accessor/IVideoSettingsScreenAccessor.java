package com.fuzs.goldenagecombat.mixin.client.accessor;

import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VideoSettingsScreen.class)
public interface IVideoSettingsScreenAccessor {

    @Accessor
    OptionsRowList getOptionsRowList();

}
