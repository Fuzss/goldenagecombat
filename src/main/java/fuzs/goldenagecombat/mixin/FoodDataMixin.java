package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @Shadow
    private int foodLevel = 20;
    @Shadow
    private float saturationLevel;
    @Shadow
    private float exhaustionLevel;
    @Shadow
    private int tickTimer;
    @Shadow
    private int lastFoodLevel = 20;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(Player player, CallbackInfo callbackInfo) {
        switch (GoldenAgeCombat.CONFIG.server().classic.foodMechanics) {
            case COMBAT_UPDATE -> {
                this.tickCombatTest(player);
                callbackInfo.cancel();
            }
            case LEGACY -> {
                this.tickLegacy(player);
                callbackInfo.cancel();
            }
        }
    }

    @Shadow
    public abstract void addExhaustion(float amount);

    @Unique
    private void tickCombatTest(Player player) {
        Difficulty difficulty = player.level.getDifficulty();
        this.lastFoodLevel = this.foodLevel;
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }
        boolean flag = player.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        if (flag && this.foodLevel > 6 && player.isHurt()) {
            ++this.tickTimer;
            if (this.tickTimer >= 40) {
                player.heal(1.0F);
                if (player.getRandom().nextBoolean()) {
                    this.foodLevel = Math.max(this.foodLevel - 1, 0);
                }
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= 40) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.hurt(DamageSource.STARVE, 1.0F);
                }
                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }
    }

    @Unique
    private void tickLegacy(Player player) {
        Difficulty difficulty = player.level.getDifficulty();
        this.lastFoodLevel = this.foodLevel;
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }
        boolean flag = player.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        if (flag && this.foodLevel >= 18 && player.isHurt()) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                player.heal(1.0F);
                this.addExhaustion(3.0F);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.hurt(DamageSource.STARVE, 1.0F);
                }
                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }
    }
}
