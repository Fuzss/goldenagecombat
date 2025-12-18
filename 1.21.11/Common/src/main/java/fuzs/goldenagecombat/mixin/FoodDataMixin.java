package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
abstract class FoodDataMixin {
    @Shadow
    private int foodLevel = 20;
    @Shadow
    private float saturationLevel;
    @Shadow
    private float exhaustionLevel;
    @Shadow
    private int tickTimer;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(ServerPlayer serverPlayer, CallbackInfo callback) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).legacyFoodMechanics) return;
        Difficulty difficulty = serverPlayer.level().getDifficulty();
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }
        boolean flag = serverPlayer.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        if (flag && this.foodLevel >= 18 && serverPlayer.isHurt()) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                serverPlayer.heal(1.0F);
                this.addExhaustion(3.0F);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                if (serverPlayer.getHealth() > 10.0F || difficulty == Difficulty.HARD
                        || serverPlayer.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    serverPlayer.hurt(serverPlayer.damageSources().starve(), 1.0F);
                }
                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }
        callback.cancel();
    }

    @Shadow
    public abstract void addExhaustion(float amount);
}
