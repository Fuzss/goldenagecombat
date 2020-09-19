package com.fuzs.goldenagecombat;

import com.fuzs.goldenagecombat.client.config.ClientConfigHandler;
import com.fuzs.goldenagecombat.client.gui.HealthIngameGui;
import com.fuzs.goldenagecombat.client.handler.NoCooldownHandler;
import com.fuzs.goldenagecombat.client.item.AttributeItemTooltip;
import com.fuzs.goldenagecombat.client.renderer.BlockingPlayerRenderer;
import com.fuzs.goldenagecombat.client.renderer.FirstPersonBlockingRenderer;
import com.fuzs.goldenagecombat.client.renderer.FirstPersonPunchRenderer;
import com.fuzs.goldenagecombat.config.CommonConfigHandler;
import com.fuzs.goldenagecombat.handler.*;
import com.fuzs.materialmaster.api.PropertyProviderUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(GoldenAgeCombat.MODID)
public class GoldenAgeCombat {

    public static final String MODID = "goldenagecombat";
    public static final String NAME = "Golden Age Combat";
    public static final Logger LOGGER = LogManager.getLogger(GoldenAgeCombat.NAME);

    @SuppressWarnings("Convert2Lambda")
    public GoldenAgeCombat() {

        // general setup
        PropertyProviderUtils.registerModProvider();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        // config setup
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfigHandler.SPEC);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> new Runnable() {

            @Override
            public void run() {

                // general setup
                FMLJavaModLoadingContext.get().getModEventBus().addListener(GoldenAgeCombat.this::onClientSetup);
                // config setup
                ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfigHandler.SPEC);
            }

        });
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

        if (CommonConfigHandler.BLOCKING.get()) MinecraftForge.EVENT_BUS.register(new InitiateBlockHandler());
        if (CommonConfigHandler.FOOD.get()) MinecraftForge.EVENT_BUS.register(new FoodRegenHandler());
        if (CommonConfigHandler.GOLDEN_APPLE.get()) MinecraftForge.EVENT_BUS.register(new GoldenAppleHandler());
        if (CommonConfigHandler.SOUNDS.get()) MinecraftForge.EVENT_BUS.register(new SoundEventHandler());

        // classic combat
        MinecraftForge.EVENT_BUS.register(new ClassicCombatHandler());
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        if (ClientConfigHandler.TOOLTIP.get()) MinecraftForge.EVENT_BUS.register(new AttributeItemTooltip());

        if (CommonConfigHandler.BLOCKING.get()) {

            MinecraftForge.EVENT_BUS.register(new FirstPersonBlockingRenderer());
            new BlockingPlayerRenderer();
        }

        if (ClientConfigHandler.ANIMATIONS.get()) {

            MinecraftForge.EVENT_BUS.register(new HealthIngameGui());
            MinecraftForge.EVENT_BUS.register(new FirstPersonPunchRenderer());
        }

        // classic combat
        MinecraftForge.EVENT_BUS.register(new NoCooldownHandler());
    }

}
