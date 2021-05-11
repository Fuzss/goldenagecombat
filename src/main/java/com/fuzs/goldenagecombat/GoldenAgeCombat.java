package com.fuzs.goldenagecombat;

import com.fuzs.goldenagecombat.client.element.LegacyAnimationsElement;
import com.fuzs.goldenagecombat.element.ClassicCombatElement;
import com.fuzs.goldenagecombat.element.CombatAdjustmentsElement;
import com.fuzs.goldenagecombat.element.SwordBlockingElement;
import com.fuzs.puzzleslib_gc.PuzzlesLib;
import com.fuzs.puzzleslib_gc.config.ConfigManager;
import com.fuzs.puzzleslib_gc.element.AbstractElement;
import com.fuzs.puzzleslib_gc.element.ElementRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess", "unused", "Convert2MethodRef"})
@Mod(GoldenAgeCombat.MODID)
public class GoldenAgeCombat extends PuzzlesLib {

    public static final String MODID = "goldenagecombat";
    public static final String NAME = "Golden Age Combat";
    public static final Logger LOGGER = LogManager.getLogger(GoldenAgeCombat.NAME);

    public static final AbstractElement CLASSIC_COMBAT = register("classic_combat", ClassicCombatElement::new);
    public static final AbstractElement SWORD_BLOCKING = register("sword_blocking", SwordBlockingElement::new);
    public static final AbstractElement COMBAT_ADJUSTMENTS = register("combat_adjustments", CombatAdjustmentsElement::new);
    public static final AbstractElement LEGACY_ANIMATIONS = register("legacy_animations", () -> new LegacyAnimationsElement(), Dist.CLIENT);

    public GoldenAgeCombat() {

        super();
        ElementRegistry.setup(MODID);
        ConfigManager.get().load();
        this.loadConfigCondition();
    }

}
