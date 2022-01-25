package fuzs.goldenagecombat.mixin.accessor;

import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FoodData.class)
public interface FoodDataAccessor {
    @Accessor
    int getFoodLevel();

    @Accessor
    float getSaturationLevel();

    @Accessor
    float getExhaustionLevel();

    @Accessor
    int getTickTimer();

    @Accessor
    int getLastFoodLevel();

    @Accessor
    void setFoodLevel(int foodLevel);

    @Accessor
    void setSaturationLevel(float saturationLevel);

    @Accessor
    void setExhaustionLevel(float exhaustionLevel);

    @Accessor
    void setTickTimer(int tickTimer);

    @Accessor
    void setLastFoodLevel(int lastFoodLevel);
}
