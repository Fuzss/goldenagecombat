package fuzs.goldenagecombat;

import fuzs.goldenagecombat.config.ClientConfig;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.goldenagecombat.handler.AttackAttributeHandler;
import fuzs.goldenagecombat.handler.ClassicCombatHandler;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.entity.ProjectileImpactCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.ItemAttributeModifiersCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingKnockBackCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.UseItemEvents;
import fuzs.puzzleslib.api.event.v1.level.PlayLevelSoundEvents;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoldenAgeCombat implements ModConstructor {
    public static final String MOD_ID = "goldenagecombat";
    public static final String MOD_NAME = "Golden Age Combat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).client(ClientConfig.class).server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        PlayLevelSoundEvents.POSITION.register(ClassicCombatHandler::onPlaySoundAtPosition);
        UseItemEvents.FINISH.register(ClassicCombatHandler::onUseItemFinish);
        LivingKnockBackCallback.EVENT.register(ClassicCombatHandler::onLivingKnockBack);
        ProjectileImpactCallback.EVENT.register(ClassicCombatHandler::onProjectileImpact);
        ItemAttributeModifiersCallback.EVENT.register(AttackAttributeHandler::onItemAttributeModifiers);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
