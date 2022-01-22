package fuzs.goldenagecombat;

import fuzs.goldenagecombat.config.ClientConfig;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.goldenagecombat.data.ModItemTagsProvider;
import fuzs.goldenagecombat.data.ModRecipeProvider;
import fuzs.goldenagecombat.handler.ClassicCombatHandler;
import fuzs.goldenagecombat.handler.CombatAdjustmentsHandler;
import fuzs.goldenagecombat.registry.ModRegistry;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.config.ConfigHolderImpl;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(GoldenAgeCombat.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GoldenAgeCombat {
    public static final String MOD_ID = "goldenagecombat";
    public static final String MOD_NAME = "Golden Age Combat";
    public static final Logger LOGGER = LogManager.getLogger(GoldenAgeCombat.MOD_NAME);

    @SuppressWarnings("Convert2MethodRef")
    public static final ConfigHolder<ClientConfig, ServerConfig> CONFIG = ConfigHolder.of(() -> new ClientConfig(), () -> new ServerConfig());

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ((ConfigHolderImpl<?, ?>) CONFIG).addConfigs(MOD_ID);
        ModRegistry.touch();
        registerHandlers();
    }

    private static void registerHandlers() {
        final ClassicCombatHandler classicCombatHandler = new ClassicCombatHandler();
        MinecraftForge.EVENT_BUS.addListener(classicCombatHandler::onAttackEntity);
        MinecraftForge.EVENT_BUS.addListener(classicCombatHandler::onItemAttributeModifier);
        MinecraftForge.EVENT_BUS.addListener(classicCombatHandler::onThrowableImpact);
        MinecraftForge.EVENT_BUS.addListener(classicCombatHandler::onUseItemFinish);
        final CombatAdjustmentsHandler combatAdjustmentsHandler = new CombatAdjustmentsHandler();
        MinecraftForge.EVENT_BUS.addListener(combatAdjustmentsHandler::onCriticalHit);
        MinecraftForge.EVENT_BUS.addListener(combatAdjustmentsHandler::onPlaySoundAtEntity);
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        DataGenerator generator = evt.getGenerator();
        final ExistingFileHelper existingFileHelper = evt.getExistingFileHelper();
        generator.addProvider(new ModRecipeProvider(generator));
        generator.addProvider(new ModItemTagsProvider(generator, existingFileHelper));
    }
}
