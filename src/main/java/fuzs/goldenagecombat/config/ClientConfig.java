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
        @Config(name = "render_damage_on_armor", description = "Armor on entities turns red when they receive damage just like their body.")
        public boolean damageOnArmor = true;
        @Config(name = "attack_while_using", description = "Allow using the \"Attack\" button while the \"Use Item\" button is held. Enables block hitting, also bow and food punching.")
        public boolean attackWhileUsing = true;
        @Config(name = "disable_flashing_hearts", description = "Lost hearts no longer flash when disappearing.")
        public boolean noFlashingHearts = false;
        @Config(name = "instant_eye_height_change", description = "Eye height changes instantly without any interpolation. Affects mainly sneaking and swimming.")
        public boolean instantEyeHeight = false;

        public AnimationsConfig() {
            super("legacy_animations");
        }
    }

    public static class TooltipConfig extends AbstractConfig {
        @Config(name = "remove_all_attributes", description = "Remove all information regarding attributes from item tooltips.")
        public boolean removeAllAttributes = false;
        @Config(name = "old_attributes_style", description = "Use the pre-1.13 renderer for attributes on item tooltips.")
        public boolean oldAttributes = true;
        @Config(name = "remove_attack_speed", description = "Remove \"Attack Speed\" attribute from item tooltips.")
        public boolean removeAttackSpeed = true;

        public TooltipConfig() {
            super("attributes_tooltip");
        }
    }
}
