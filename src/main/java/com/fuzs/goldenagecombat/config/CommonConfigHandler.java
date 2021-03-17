package com.fuzs.goldenagecombat.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class CommonConfigHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // features
    public static final ForgeConfigSpec.BooleanValue BLOCKING;

    // blocking
    public static final ForgeConfigSpec.ConfigValue<List<String>> BLOCKING_EXCLUDE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> BLOCKING_INCLUDE;

    static {

        BUILDER.push("_features");
        BLOCKING = CommonConfigHandler.BUILDER.comment("Re-adds sword blocking in a very configurable way.").define("Blocking", true);
        BUILDER.pop();

        BUILDER.push("blocking");
        BLOCKING_EXCLUDE = CommonConfigHandler.BUILDER.comment("Swords to exclude from blocking. Intended for modded swords that already have their own right-click function.", "Format for every entry is \"<namespace>:<path>\". Path may use single asterisk as wildcard parameter.").define("Blocking Exclusion List", Lists.newArrayList());
        BLOCKING_INCLUDE = CommonConfigHandler.BUILDER.comment("Items to include for blocking. Intended for modded swords that don't extend vanilla swords.", "Format for every entry is \"<namespace>:<path>\". Path may use single asterisk as wildcard parameter.").define("Blocking Inclusion List", Lists.newArrayList());
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

}