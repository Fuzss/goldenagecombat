package com.fuzs.goldenagecombat.client.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

@OnlyIn(Dist.CLIENT)
public class ClientConfigHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // animations
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS_NO_FLASHING_HEARTS;

    static {

        BUILDER.push("animations");
        ANIMATIONS_NO_FLASHING_HEARTS = BUILDER.comment("Hearts you just lost no longer flash when disappearing.").define("Disable Flashing Hearts", false);
        BUILDER.pop();
    }

}