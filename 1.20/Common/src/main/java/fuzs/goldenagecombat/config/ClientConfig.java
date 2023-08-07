package fuzs.goldenagecombat.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ClientConfig implements ConfigCore {
    @Config(name = "legacy_animations")
    public AnimationsConfig animations = new AnimationsConfig();
    @Config(name = "attributes_tooltip")
    public TooltipConfig tooltip = new TooltipConfig();

    public static class AnimationsConfig implements ConfigCore {
        @Config(name = "disable_flashing_hearts", description = "Lost hearts no longer flash when disappearing, so it is easier to see how much health you currently have.")
        public boolean noFlashingHearts = false;
        @Config(name = "instant_eye_height_change", description = "Eye height changes instantly without any interpolation. Affects mainly sneaking and swimming.")
        public boolean instantEyeHeight = false;
        @Config(name = "alternative_swing_animation", description = "Improved arm swing animation to emphasize the rhythm of the attacks from combat test snapshots.")
        public boolean swingAnimation = false;
        @Config(name = "full_interact_animations", description = "Allows block hitting, bow and food punching to render properly (meaning attacking and then using the item directly afterwards). The hitting animation is no longer consumed as in vanilla.")
        public boolean interactAnimations = true;
    }

    public static class TooltipConfig implements ConfigCore {
        @Config(name = "remove_all_attributes", description = "Remove all information regarding attributes from item tooltips.")
        public boolean removeAllAttributes = false;
        @Config(name = "old_attributes_style", description = "Use the pre-1.9 renderer for attributes on item tooltips.")
        public boolean oldAttributes = true;
        @Config(name = "special_armor_attributes", description = {"Render armor attributes with green text instead of blue one and include the entity's base armor value, just like with tools and weapons.", "Does not apply when old style attributes renderer is used."})
        public boolean specialArmorAttributes = true;
    }
}
