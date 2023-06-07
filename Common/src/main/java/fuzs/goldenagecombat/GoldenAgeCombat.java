package fuzs.goldenagecombat;

import fuzs.goldenagecombat.config.ClientConfig;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.goldenagecombat.handler.AttackAttributeHandler;
import fuzs.goldenagecombat.handler.ClassicCombatHandler;
import fuzs.goldenagecombat.handler.CombatTestHandler;
import fuzs.goldenagecombat.handler.SwordBlockingHandler;
import fuzs.goldenagecombat.init.ModRegistry;
import fuzs.goldenagecombat.network.client.ServerboundSweepAttackMessage;
import fuzs.goldenagecombat.network.client.ServerboundSwingArmMessage;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.entity.ProjectileImpactCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.ItemAttributeModifiersCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingHurtCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingKnockBackCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.UseItemEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerTickEvents;
import fuzs.puzzleslib.api.event.v1.level.PlayLevelSoundEvents;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoldenAgeCombat implements ModConstructor {
    public static final String MOD_ID = "goldenagecombat";
    public static final String MOD_NAME = "Golden Age Combat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandlerV3 NETWORK = NetworkHandlerV3.builder(MOD_ID).registerServerbound(ServerboundSweepAttackMessage.class).registerServerbound(ServerboundSwingArmMessage.class);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).client(ClientConfig.class).server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
        CONFIG.getHolder(ServerConfig.class).accept(CombatTestHandler::setMaxStackSize);
        registerHandlers();
    }

    private static void registerHandlers() {
        UseItemEvents.START.register(SwordBlockingHandler::onUseItemStart);
        PlayerInteractEvents.USE_ITEM.register(SwordBlockingHandler::onUseItem);
        LivingHurtCallback.EVENT.register(SwordBlockingHandler::onLivingHurt);
        PlayerInteractEvents.USE_ITEM.register(CombatTestHandler::onUseItem);
        UseItemEvents.START.register(CombatTestHandler::onUseItemStart);
        PlayerTickEvents.START.register(CombatTestHandler::onStartPlayerTick);
        LivingHurtCallback.EVENT.register(CombatTestHandler::onLivingHurt);
        PlayLevelSoundEvents.POSITION.register(ClassicCombatHandler::onPlaySoundAtPosition);
        UseItemEvents.FINISH.register(ClassicCombatHandler::onUseItemFinish);
        LivingKnockBackCallback.EVENT.register(SwordBlockingHandler::onLivingKnockBack);
        LivingKnockBackCallback.EVENT.register(ClassicCombatHandler::onLivingKnockBack);
        ProjectileImpactCallback.EVENT.register(ClassicCombatHandler::onProjectileImpact);
        ItemAttributeModifiersCallback.EVENT.register(AttackAttributeHandler::onItemAttributeModifiers$0);
        ItemAttributeModifiersCallback.EVENT.register(AttackAttributeHandler::onItemAttributeModifiers$1);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
