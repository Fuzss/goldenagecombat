package fuzs.goldenagecombat;

import fuzs.goldenagecombat.config.ClientConfig;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.goldenagecombat.data.ModItemTagsProvider;
import fuzs.goldenagecombat.handler.AttackAttributeHandler;
import fuzs.goldenagecombat.handler.ClassicCombatHandler;
import fuzs.goldenagecombat.handler.CombatTestHandler;
import fuzs.goldenagecombat.handler.SwordBlockingHandler;
import fuzs.goldenagecombat.network.client.C2SSweepAttackMessage;
import fuzs.goldenagecombat.network.client.C2SSwingArmMessage;
import fuzs.goldenagecombat.registry.ModRegistry;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.config.ConfigHolderImpl;
import fuzs.puzzleslib.network.MessageDirection;
import fuzs.puzzleslib.network.NetworkHandler;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

    public static final NetworkHandler NETWORK = NetworkHandler.of(MOD_ID);
    @SuppressWarnings("Convert2MethodRef")
    public static final ConfigHolder<ClientConfig, ServerConfig> CONFIG = ConfigHolder.of(() -> new ClientConfig(), () -> new ServerConfig());

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ((ConfigHolderImpl<?, ?>) CONFIG).addConfigs(MOD_ID);
        ModRegistry.touch();
        registerMessages();
        registerHandlers();
    }

    private static void registerHandlers() {
        final ClassicCombatHandler classicCombatHandler = new ClassicCombatHandler();
        MinecraftForge.EVENT_BUS.addListener(classicCombatHandler::onAttackEntity);
        MinecraftForge.EVENT_BUS.addListener(classicCombatHandler::onThrowableImpact);
        MinecraftForge.EVENT_BUS.addListener(classicCombatHandler::onUseItemFinish);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, classicCombatHandler::onLivingKnockBack);
        MinecraftForge.EVENT_BUS.addListener(classicCombatHandler::onPlaySoundAtEntity);
        final SwordBlockingHandler swordBlockingHandler = new SwordBlockingHandler();
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, swordBlockingHandler::onRightClickItem);
        MinecraftForge.EVENT_BUS.addListener(swordBlockingHandler::onItemUseStart);
        MinecraftForge.EVENT_BUS.addListener(swordBlockingHandler::onLivingHurt);
        MinecraftForge.EVENT_BUS.addListener(swordBlockingHandler::onLivingKnockBack);
        final AttackAttributeHandler attackAttributeHandler = new AttackAttributeHandler();
        MinecraftForge.EVENT_BUS.addListener(attackAttributeHandler::onItemAttributeModifier$Damage);
        MinecraftForge.EVENT_BUS.addListener(attackAttributeHandler::onItemAttributeModifier$Reach);
        final CombatTestHandler combatTestHandler = new CombatTestHandler();
        MinecraftForge.EVENT_BUS.addListener(combatTestHandler::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(combatTestHandler::onItemUseStart);
        MinecraftForge.EVENT_BUS.addListener(combatTestHandler::onRightClickItem);
        MinecraftForge.EVENT_BUS.addListener(combatTestHandler::onLivingDamage);
        MinecraftForge.EVENT_BUS.addListener(combatTestHandler::onLivingHurt);
    }

    private static void registerMessages() {
        NETWORK.register(C2SSweepAttackMessage.class, C2SSweepAttackMessage::new, MessageDirection.TO_SERVER);
        NETWORK.register(C2SSwingArmMessage.class, C2SSwingArmMessage::new, MessageDirection.TO_SERVER);
    }

    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent evt) {
        CombatTestHandler.setMaxStackSize();
    }

    @SubscribeEvent
    public static void onEntityAttributeModification(final EntityAttributeModificationEvent evt) {
        evt.add(EntityType.PLAYER, ModRegistry.ATTACK_REACH_ATTRIBUTE.get(), 3.0);
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        DataGenerator generator = evt.getGenerator();
        final ExistingFileHelper existingFileHelper = evt.getExistingFileHelper();
        generator.addProvider(new ModItemTagsProvider(generator, existingFileHelper));
    }
}
