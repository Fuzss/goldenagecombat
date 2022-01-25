package fuzs.goldenagecombat.world.food;

import fuzs.goldenagecombat.mixin.accessor.FoodDataAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public abstract class FoodDataExtension extends FoodData {
    int foodLevel = 20;
    float saturationLevel;
    float exhaustionLevel;
    int tickTimer;
    int lastFoodLevel = 20;
    
    @Override
    public final void tick(Player player) {
        this.syncVanillaValues();
        this.tickInternal(player);
        this.syncExtensionValues();
    }

    void syncVanillaValues() {
        this.foodLevel = ((FoodDataAccessor) this).getFoodLevel();
        this.saturationLevel = ((FoodDataAccessor) this).getSaturationLevel();
        this.exhaustionLevel = ((FoodDataAccessor) this).getExhaustionLevel();
        this.tickTimer = ((FoodDataAccessor) this).getTickTimer();
        this.lastFoodLevel = ((FoodDataAccessor) this).getLastFoodLevel();
    }

    void syncExtensionValues() {
        ((FoodDataAccessor) this).setFoodLevel(this.foodLevel);
        ((FoodDataAccessor) this).setSaturationLevel(this.saturationLevel);
        ((FoodDataAccessor) this).setExhaustionLevel(this.exhaustionLevel);
        ((FoodDataAccessor) this).setTickTimer(this.tickTimer);
        ((FoodDataAccessor) this).setLastFoodLevel(this.lastFoodLevel);
    }

    abstract void tickInternal(Player player);
}