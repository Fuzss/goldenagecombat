package fuzs.goldenagecombat.config;

import fuzs.puzzleslib.config.AbstractConfig;
import fuzs.puzzleslib.config.annotation.Config;

public class ClientConfig extends AbstractConfig {
    @Config
    public AdjustmentsConfig adjustments = new AdjustmentsConfig();

    public ClientConfig() {
        super("");
    }

    public static class AdjustmentsConfig extends AbstractConfig {
        @Config(name = "Remove Attack Speed Tooltip", description = "Remove \"Attack Speed\" attribute from inventory tooltips.")
        public boolean noAttackSpeedTooltip;

        public AdjustmentsConfig() {
            super("combat_adjustments");
        }
    }
}
