package com.fuzs.goldenagecombat.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfigHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // features
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS;

    // animations
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS_ARMOR;
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS_PUNCHING;
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS_BLOCKHITTING;
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS_NO_FLASHING_HEARTS;

    // blocking
    public static final ForgeConfigSpec.EnumValue<BlockingPose> BLOCKING_POSE;

    static {

        BUILDER.push("_features");
        ANIMATIONS = BUILDER.comment("Old visuals and animations for various things.").define("Animations", true);
        BUILDER.pop();

        BUILDER.push("animations");
        ANIMATIONS_ARMOR = BUILDER.comment("Armor on entities turns red when they receive damage.").define("Armor", true);
        ANIMATIONS_PUNCHING = ClientConfigHandler.BUILDER.comment("Use a bow or eat food while punching at the same time.").define("Punching", true);
        ANIMATIONS_BLOCKHITTING = ClientConfigHandler.BUILDER.comment("Hit and block with your sword at the same time.").define("Block Hitting", true);
        ANIMATIONS_NO_FLASHING_HEARTS = ClientConfigHandler.BUILDER.comment("Hearts you lost no longer flash up.").define("No Flashing Hearts", false);
        BUILDER.pop();

        BUILDER.push("blocking");
        BLOCKING_POSE = BUILDER.comment("Third person pose when blocking, \"MODERN\" is from 1.8, \"LEGACY\" from before that.").defineEnum("Pose", BlockingPose.LEGACY);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    @SuppressWarnings("unused")
    public enum BlockingPose {

        LEGACY, MODERN
    }

}