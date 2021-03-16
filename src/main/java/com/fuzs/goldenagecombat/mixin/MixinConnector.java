package com.fuzs.goldenagecombat.mixin;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

@SuppressWarnings("unused")
public class MixinConnector implements IMixinConnector {

    @Override
    public void connect() {

        Mixins.addConfiguration("META-INF/" + GoldenAgeCombat.MODID + ".mixins.json");
    }

}
