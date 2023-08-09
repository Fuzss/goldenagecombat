package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ClientConfig;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
abstract class GuiMixin {

    @ModifyVariable(method = "renderHearts", at = @At("HEAD"), ordinal = 5)
    public int renderHearts(int lastHealth) {
        if (!GoldenAgeCombat.CONFIG.get(ClientConfig.class).noFlashingHearts) return lastHealth;
        return 0;
    }
}
