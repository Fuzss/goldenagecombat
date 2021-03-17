package com.fuzs.goldenagecombat.client.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

@OnlyIn(Dist.CLIENT)
public class ClientConfigHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // features
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS;

    // animations
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS_ARMOR;
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS_NO_FLASHING_HEARTS;

    // blocking
    public static final ForgeConfigSpec.EnumValue<BlockingPose> BLOCKING_POSE;

    static {

        BUILDER.push("_features");
        ANIMATIONS = BUILDER.comment("Old visuals and animations for miscellaneous things.").define("Legacy Animations", true);
        BUILDER.pop();

        BUILDER.push("animations");
        ANIMATIONS_ARMOR = BUILDER.comment("Armor on entities turns red when they receive damage just like their body.").define("Render Damage On Armor", true);
        ANIMATIONS_NO_FLASHING_HEARTS = BUILDER.comment("Hearts you just lost no longer flash when disappearing.").define("Disable Flashing Hearts", false);
        BUILDER.pop();

        BUILDER.push("blocking");
        BLOCKING_POSE = BUILDER.comment("Third person pose when blocking, \"MODERN\" is from Minecraft 1.8, \"LEGACY\" from game versions before that.").defineEnum("Blocking Pose", BlockingPose.LEGACY);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public enum BlockingPose {

        LEGACY, MODERN
    }

}