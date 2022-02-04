package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @ModifyVariable(method = "renderHearts", at = @At("HEAD"), ordinal = 5)
    public int renderHearts$lastHealth(int lastHealth) {
        if (!GoldenAgeCombat.CONFIG.client().animations.noFlashingHearts) return lastHealth;
        return 0;
    }
}
