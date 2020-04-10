package com.fuzs.goldenagecombat.handler;

import com.fuzs.goldenagecombat.config.CommonConfigHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FoodRegenHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onEntityJoinWorld(final EntityJoinWorldEvent evt) {

        // replace food stats with one having some tweaks in the tick method
        if (evt.getEntity() instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) evt.getEntity();
            player.foodStats = new CombatFoodStats(player.foodStats);
        }
    }

    private static class CombatFoodStats extends FoodStats {

        private CombatFoodStats(FoodStats oldStats) {

            this.foodLevel = oldStats.foodLevel;
            this.foodSaturationLevel = oldStats.foodSaturationLevel;
            this.foodExhaustionLevel = oldStats.foodExhaustionLevel;
            this.foodTimer = oldStats.foodTimer;
        }

        @Override
        public void tick(PlayerEntity player) {

            Difficulty difficulty = player.world.getDifficulty();
            if (this.foodExhaustionLevel > 4.0F) {

                this.foodExhaustionLevel -= 4.0F;
                if (this.foodSaturationLevel > 0.0F) {

                    this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
                } else if (difficulty != Difficulty.PEACEFUL) {

                    this.foodLevel = Math.max(this.foodLevel - 1, 0);
                }
            }

            boolean naturalRegen = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
            if (naturalRegen && this.foodLevel >= CommonConfigHandler.FOOD_REGEN_THRESHOLD.get() && player.shouldHeal()) {

                ++this.foodTimer;
                if (this.foodTimer >= CommonConfigHandler.FOOD_REGEN_DELAY.get()) {

                    player.heal(1.0F);
                    if (CommonConfigHandler.FOOD_DRAIN_FOOD.get()) {

                        this.foodLevel = Math.max(this.foodLevel - 1, 0);
                    } else {

                        this.addExhaustion(3.0F);
                    }
                    this.foodTimer = 0;
                }
            } else if (this.foodLevel <= 0) {

                ++this.foodTimer;
                if (this.foodTimer >= CommonConfigHandler.FOOD_REGEN_DELAY.get()) {

                    if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {

                        player.attackEntityFrom(DamageSource.STARVE, 1.0F);
                    }

                    this.foodTimer = 0;
                }
            } else {

                this.foodTimer = 0;
            }
        }

    }

}
