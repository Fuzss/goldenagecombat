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
    public static final ForgeConfigSpec.BooleanValue GOLDEN_APPLE;
    public static final ForgeConfigSpec.BooleanValue SOUNDS;

    // combat
    public static final ForgeConfigSpec.BooleanValue COMBAT_DAMAGE_VALUES;

    // blocking
    public static final ForgeConfigSpec.ConfigValue<List<String>> BLOCKING_EXCLUDE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> BLOCKING_INCLUDE;

    // food
    public static final ForgeConfigSpec.IntValue FOOD_REGEN_DELAY;
    public static final ForgeConfigSpec.IntValue FOOD_REGEN_THRESHOLD;
    public static final ForgeConfigSpec.BooleanValue FOOD_DRAIN_FOOD;

    // golden apple
    public static final ForgeConfigSpec.BooleanValue GOLDEN_APPLE_EFFECTS;
    public static final ForgeConfigSpec.BooleanValue GOLDEN_APPLE_RECIPE;

    // sounds
    public static final ForgeConfigSpec.BooleanValue SOUNDS_CRIT;
    public static final ForgeConfigSpec.BooleanValue SOUNDS_KNOCKBACK;
    public static final ForgeConfigSpec.BooleanValue SOUNDS_NODAMAGE;
    public static final ForgeConfigSpec.BooleanValue SOUNDS_STRONG;
    public static final ForgeConfigSpec.BooleanValue SOUNDS_SWEEP;
    public static final ForgeConfigSpec.BooleanValue SOUNDS_WEAK;

    // classic combat
    public static final ForgeConfigSpec.BooleanValue REMOVE_ATTACK_COOLDOWN;
    public static final ForgeConfigSpec.BooleanValue HIDE_ATTACK_INDICATOR;
    public static final ForgeConfigSpec.BooleanValue BOOST_SHARPNESS;
    public static final ForgeConfigSpec.BooleanValue MORE_SPRINTING;
    public static final ForgeConfigSpec.BooleanValue SWEEPING_REQUIRED;
    public static final ForgeConfigSpec.BooleanValue OLD_FISHING_ROD;

    static {

        BUILDER.push("_features");
        COOLDOWN = CommonConfigHandler.BUILDER.comment("Remove cooldown mechanic and make fast clicking effective again.").define("Cooldown", true);
        BLOCKING = CommonConfigHandler.BUILDER.comment("Re-adds sword blocking in a very configurable way.").define("Blocking", true);
        FOOD = CommonConfigHandler.BUILDER.comment("Changes the way the player heals from food, mainly disabling almost instant regeneration.").define("Food", true);
        GOLDEN_APPLE = CommonConfigHandler.BUILDER.comment("Revert various aspects of enchanted golden apples.").define("Golden Apple", true);
        SOUNDS = CommonConfigHandler.BUILDER.comment("Don't play various attack sounds added in more recent versions. Set individual sounds to \"true\" to disable.").define("Sounds", true);
        BUILDER.pop();

        BUILDER.push("cooldown");
        COMBAT_DAMAGE_VALUES = CommonConfigHandler.BUILDER.comment("Revert weapon and tool attack damage to old values.").define("Old Damage Values", true);
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

        BUILDER.push("golden_apple");
        GOLDEN_APPLE_EFFECTS = CommonConfigHandler.BUILDER.comment("Give Regeneration V and Absorption I instead of Regeneration II and Absorption IV after consuming.").define("Effects", true);
        GOLDEN_APPLE_RECIPE = CommonConfigHandler.BUILDER.comment("Enchanted golden apples can be crafted from a single apple and eight gold blocks.").define("Recipe", true);
        BUILDER.pop();

        BUILDER.push("sounds");
        SOUNDS_CRIT = CommonConfigHandler.BUILDER.comment("Plays when the player lands a critical strike.").define("Critical Attack", false);
        SOUNDS_KNOCKBACK = CommonConfigHandler.BUILDER.comment("Plays when the player deals knockback to their victim.").define("Knockback Attack", false);
        SOUNDS_NODAMAGE = CommonConfigHandler.BUILDER.comment("Plays when an attack is unable to cause damage.").define("No Damage Attack", true);
        SOUNDS_STRONG = CommonConfigHandler.BUILDER.comment("Plays when the attack meter was at least 90% recharged.").define("Strong Attack", true);
        SOUNDS_SWEEP = CommonConfigHandler.BUILDER.comment("Plays together with performing a sweep attack.").define("Sweep Attack", false);
        SOUNDS_WEAK = CommonConfigHandler.BUILDER.comment("Plays when the attack meter was recharged less than 90%.").define("Weak Attack", true);
        BUILDER.pop();




        BUILDER.comment("Restores pre-Combat Update combat mechanics.");
        BUILDER.push("classic_combat");
        REMOVE_ATTACK_COOLDOWN = CommonConfigHandler.BUILDER.comment("Completely remove the attack cooldown as if it never even existed in the first place.").define("Remove Attack Cooldown", true);
        HIDE_ATTACK_INDICATOR = CommonConfigHandler.BUILDER.comment("Prevent attack indicator from showing regardless of what's been set in \"Video Settings\".").define("Disable Attack Indicator", true);
        BOOST_SHARPNESS = CommonConfigHandler.BUILDER.comment("Boost sharpness enchantment to add +1.0 attack damage per level instead of +0.5 damage.").define("Boost Sharpness", true);
        MORE_SPRINTING = CommonConfigHandler.BUILDER.comment("Sprinting and attacking no longer interfere, so you won't stop and critical hits are always possible.").define("Sprint While Attacking", true);
        SWEEPING_REQUIRED = CommonConfigHandler.BUILDER.comment("Is the sweeping edge enchantment required to perform a sweep attack.").define("Require Sweeping Edge", true);
        OLD_FISHING_ROD = CommonConfigHandler.BUILDER.comment("Fishing bobbers deal knockback upon hitting an entity, also entities being pulled in are slightly propelled upwards.").define("Old Fishing Rod", true);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

}