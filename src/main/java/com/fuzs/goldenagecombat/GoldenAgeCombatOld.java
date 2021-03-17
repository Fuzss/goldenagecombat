package com.fuzs.goldenagecombat;

import com.fuzs.goldenagecombat.client.config.ClientConfigHandler;
import com.fuzs.goldenagecombat.client.gui.HealthIngameGui;
import com.fuzs.goldenagecombat.client.renderer.BlockingPlayerRenderer;
import com.fuzs.goldenagecombat.client.renderer.FirstPersonBlockingRenderer;
import com.fuzs.goldenagecombat.client.renderer.FirstPersonPunchRenderer;
import com.fuzs.goldenagecombat.config.CommonConfigHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
public class GoldenAgeCombatOld {

    public static final String MODID = "goldenagecombat";
    public static final String NAME = "Golden Age Combat";
    public static final Logger LOGGER = LogManager.getLogger(GoldenAgeCombatOld.NAME);

    private void onClientSetup(final FMLClientSetupEvent evt) {

        if (CommonConfigHandler.BLOCKING.get()) {

            MinecraftForge.EVENT_BUS.register(new FirstPersonBlockingRenderer());
            new BlockingPlayerRenderer();
        }

        if (ClientConfigHandler.ANIMATIONS.get()) {

            MinecraftForge.EVENT_BUS.register(new HealthIngameGui());
            MinecraftForge.EVENT_BUS.register(new FirstPersonPunchRenderer());
        }
    }

}
