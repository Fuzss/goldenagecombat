package fuzs.goldenagecombat.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ClientConfig implements ConfigCore {
    @Config(
            name = "disable_flashing_hearts",
            description = "Lost hearts no longer flash when disappearing, so it is easier to see how much health you currently have."
    )
    public boolean noFlashingHearts = false;
    @Config(
            name = "instant_eye_height_change",
            description = "Eye height changes instantly without any interpolation. Affects mainly sneaking and swimming."
    )
    public boolean instantEyeHeight = false;
    @Config(
            name = "full_interact_animations",
            description = "Allows block hitting, bow and food punching to render properly (meaning attacking and then using the item directly afterwards). The hitting animation is no longer consumed as in vanilla."
    )
    public boolean interactAnimations = true;
    @Config(
            description = {
                    "Choose a style for the attributes display on item tooltips.",
                    "REMOVE: No information regarding attributes is added.",
                    "LEGACY: The pre-1.9 attributes style, mainly recognizable for using blue text instead of green.",
                    "VANILLA: The current vanilla attribute style."
            }
    )
    public AttributesStyle attributesStyle = AttributesStyle.LEGACY;
    @Config(description = "Don't play the first person item reequip animation when beginning to use an item by holding the mouse button, like a shield or bow.")
    public boolean noReequipWhenUsing = true;

    public enum AttributesStyle {
        REMOVE,
        LEGACY,
        VANILLA
    }
}
