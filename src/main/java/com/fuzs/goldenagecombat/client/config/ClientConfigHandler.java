package com.fuzs.goldenagecombat.client.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

@OnlyIn(Dist.CLIENT)
public class ClientConfigHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // features
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS;
    public static final ForgeConfigSpec.BooleanValue TOOLTIP;

    // animations
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS_ARMOR;
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS_PUNCHING;
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS_BLOCKHITTING;
    public static final ForgeConfigSpec.BooleanValue ANIMATIONS_NO_FLASHING_HEARTS;

    // tooltip
    public static final ForgeConfigSpec.BooleanValue TOOLTIP_SPEED;
    public static final ForgeConfigSpec.BooleanValue TOOLTIP_TOUGHNESS;

    // blocking
    public static final ForgeConfigSpec.EnumValue<BlockingPose> BLOCKING_POSE;

    static {

        BUILDER.push("_features");
        ANIMATIONS = BUILDER.comment("Old visuals and animations for various things.").define("Animations", true);
        TOOLTIP = BUILDER.comment("Remove various attribute entries from item tooltips which are no longer relevant.").define("Tooltip", true);
        BUILDER.pop();

        BUILDER.push("animations");
        ANIMATIONS_ARMOR = BUILDER.comment("Armor on entities turns red when they receive damage.").define("Armor", true);
        ANIMATIONS_PUNCHING = BUILDER.comment("Use a bow or eat food while punching at the same time.").define("Punching", true);
        ANIMATIONS_BLOCKHITTING = BUILDER.comment("Hit and block with your sword at the same time.").define("Block Hitting", true);
        ANIMATIONS_NO_FLASHING_HEARTS = BUILDER.comment("Hearts you lost no longer flash up.").define("No Flashing Hearts", false);
        BUILDER.pop();

        BUILDER.push("tooltip");
        TOOLTIP_SPEED = BUILDER.comment("Remove \"Attack Speed\" attribute from tooltips.").define("Attack Speed", true);
        TOOLTIP_TOUGHNESS = BUILDER.comment("Remove \"Armor Toughness\" attribute from tooltips.").define("Armor Toughness", true);
        BUILDER.pop();

        BUILDER.push("blocking");
        BLOCKING_POSE = BUILDER.comment("Third person pose when blocking, \"MODERN\" is from 1.8, \"LEGACY\" from before that.").defineEnum("Pose", BlockingPose.LEGACY);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public enum BlockingPose {

        LEGACY, MODERN
    }

}