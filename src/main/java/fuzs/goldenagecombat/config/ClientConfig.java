package fuzs.goldenagecombat.config;

import fuzs.puzzleslib.config.AbstractConfig;
import fuzs.puzzleslib.config.annotation.Config;

public class ClientConfig extends AbstractConfig {
    @Config
    public AnimationsConfig animations = new AnimationsConfig();
    @Config
    public TooltipConfig tooltip = new TooltipConfig();

    public ClientConfig() {
        super("");
    }

    public static class AnimationsConfig extends AbstractConfig {
        // TODO finish implementation
//        @Config(name = "sword_blocking_with_shield", description = {"When holding a shield in your offhand, hide it and show a sword block when actively blocking instead.", "Mainly useful on some servers that enable sword blocking by temporarily giving you a shield."})
        public boolean swordBlockingWithShield = false;
        @Config(name = "simple_blocking_pose", description = "Use the much simpler third-person pose when blocking with a sword from Minecaft 1.8 instead of the default one from before that.")
        public boolean simpleBlockingPose = false;
        @Config(name = "disable_flashing_hearts", description = "Lost hearts no longer flash when disappearing, so it is easier to see how much health you currently have.")
        public boolean noFlashingHearts = false;
        @Config(name = "instant_eye_height_change", description = "Eye height changes instantly without any interpolation. Affects mainly sneaking and swimming.")
        public boolean instantEyeHeight = false;
        @Config(name = "alternative_swing_animation", description = "Improved arm swing animation to emphasize the rhythm of the attacks from combat test snapshots.")
        public boolean swingAnimation = false;

        public AnimationsConfig() {
            super("legacy_animations");
        }
    }

    public static class TooltipConfig extends AbstractConfig {
        @Config(name = "remove_all_attributes", description = "Remove all information regarding attributes from item tooltips.")
        public boolean removeAllAttributes = false;
        @Config(name = "old_attributes_style", description = "Use the pre-1.13 renderer for attributes on item tooltips.")
        public boolean oldAttributes = true;

        public TooltipConfig() {
            super("attributes_tooltip");
        }
    }
}
