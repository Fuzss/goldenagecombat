package com.fuzs.goldenagecombat;

import com.fuzs.goldenagecombat.element.ClassicCombatElement;
import com.fuzs.puzzleslib_gc.PuzzlesLib;
import com.fuzs.puzzleslib_gc.config.ConfigManager;
import com.fuzs.puzzleslib_gc.element.AbstractElement;
import com.fuzs.puzzleslib_gc.element.ElementRegistry;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(GoldenAgeCombat.MODID)
public class GoldenAgeCombat extends PuzzlesLib {

    public static final String MODID = "goldenagecombat";
    public static final String NAME = "Golden Age Combat";
    public static final Logger LOGGER = LogManager.getLogger(GoldenAgeCombat.NAME);

    public static final AbstractElement CLASSIC_COMBAT = register("classic_combat", ClassicCombatElement::new);

    public GoldenAgeCombat() {

        super();
        ElementRegistry.setup(MODID);
        ConfigManager.get().load();
        this.loadConfigCondition();
    }

}
