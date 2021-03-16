package com.fuzs.goldenagecombat.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class CommonConfigHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // features
    public static final ForgeConfigSpec.BooleanValue COOLDOWN;
    public static final ForgeConfigSpec.BooleanValue BLOCKING;
//    public static final ForgeConfigSpec.BooleanValue FISHING_ROD;
    public static final ForgeConfigSpec.BooleanValue FOOD;

    // blocking
    public static final ForgeConfigSpec.ConfigValue<List<String>> BLOCKING_EXCLUDE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> BLOCKING_INCLUDE;

    // food
    public static final ForgeConfigSpec.IntValue FOOD_REGEN_DELAY;
    public static final ForgeConfigSpec.IntValue FOOD_REGEN_THRESHOLD;
    public static final ForgeConfigSpec.BooleanValue FOOD_DRAIN_FOOD;

    // classic combat
    public static final ForgeConfigSpec.BooleanValue REMOVE_ATTACK_COOLDOWN;
    public static final ForgeConfigSpec.BooleanValue HIDE_ATTACK_INDICATOR;
    public static final ForgeConfigSpec.BooleanValue MORE_SPRINTING;
    public static final ForgeConfigSpec.BooleanValue OLD_FISHING_ROD;

    static {

        BUILDER.push("_features");
        COOLDOWN = CommonConfigHandler.BUILDER.comment("Remove cooldown mechanic and make fast clicking effective again.").define("Cooldown", true);
        BLOCKING = CommonConfigHandler.BUILDER.comment("Re-adds sword blocking in a very configurable way.").define("Blocking", true);
        FOOD = CommonConfigHandler.BUILDER.comment("Changes the way the player heals from food, mainly disabling almost instant regeneration.").define("Food", true);
        BUILDER.pop();

        BUILDER.push("blocking");
        BLOCKING_EXCLUDE = CommonConfigHandler.BUILDER.comment("Swords to exclude from blocking. Intended for modded swords that already have their own right-click function.", "Format for every entry is \"<namespace>:<path>\". Path may use single asterisk as wildcard parameter.").define("Blocking Exclusion List", Lists.newArrayList());
        BLOCKING_INCLUDE = CommonConfigHandler.BUILDER.comment("Items to include for blocking. Intended for modded swords that don't extend vanilla swords.", "Format for every entry is \"<namespace>:<path>\". Path may use single asterisk as wildcard parameter.").define("Blocking Inclusion List", Lists.newArrayList());
        BUILDER.pop();

        BUILDER.push("food");
        FOOD_REGEN_DELAY = CommonConfigHandler.BUILDER.comment("Amount of ticks between regenerating when enough food is present.").defineInRange("Regeneration Delay", 80, 0, 72000);
        FOOD_REGEN_THRESHOLD = CommonConfigHandler.BUILDER.comment("Food level required to be able to regenerate health.").defineInRange("Regeneration Food Level", 18, 0, 20);
        FOOD_DRAIN_FOOD = CommonConfigHandler.BUILDER.comment("Drain food instead of saturation when regenerating.").define("Regenerate From Food", false);
        BUILDER.pop();

        BUILDER.comment("Restores pre-Combat Update combat mechanics.");
        BUILDER.push("classic_combat");
        REMOVE_ATTACK_COOLDOWN = CommonConfigHandler.BUILDER.comment("Completely remove the attack cooldown as if it never even existed in the first place.").define("Remove Attack Cooldown", true);
        HIDE_ATTACK_INDICATOR = CommonConfigHandler.BUILDER.comment("Prevent attack indicator from showing regardless of what's been set in \"Video Settings\".").define("Disable Attack Indicator", true);
        MORE_SPRINTING = CommonConfigHandler.BUILDER.comment("Sprinting and attacking no longer interfere, so you won't stop and critical hits are always possible.").define("Sprint While Attacking", true);
        OLD_FISHING_ROD = CommonConfigHandler.BUILDER.comment("Fishing bobbers deal knockback upon hitting an entity, also entities being pulled in are slightly propelled upwards.").define("Old Fishing Rod", true);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

}