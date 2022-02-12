package fuzs.goldenagecombat.mixin.accessor;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor
    void setAttackStrengthTicker(int attackStrengthTicker);

    @Accessor
    int getAttackStrengthTicker();
}
