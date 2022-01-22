package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.handler.ClassicCombatHandler;
import net.minecraft.util.MovementInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings("unused")
@Mixin(MovementInput.class)
public abstract class MovementInputMixin {

    @ModifyConstant(method = "isMovingForward", constant = @Constant(floatValue = 1.0E-5F))
    public float getMinMovementAmount(float oldAmount) {

        ClassicCombatHandler element = (ClassicCombatHandler) GoldenAgeCombat.CLASSIC_COMBAT;
        return element.isEnabled() && element.extension.quickSlowdown ? 0.8F : oldAmount;
    }

}
