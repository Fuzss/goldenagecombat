package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.client.element.LegacyAnimationsRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends RecursiveEventLoop<Runnable> {

    public MinecraftMixin(String name) {

        super(name);
    }

    @Redirect(method = "rightClickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerController;getIsHittingBlock()Z"))
    public boolean getIsHittingBlock(PlayerController playerController) {

        LegacyAnimationsRenderer element = (LegacyAnimationsRenderer) GoldenAgeCombat.LEGACY_ANIMATIONS;
        if (element.isEnabled() && element.attackWhileUsing) {

            return false;
        }

        return playerController.getIsHittingBlock();
    }

    @Redirect(method = "sendClickBlockToController", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;isHandActive()Z"))
    public boolean isHandActive(ClientPlayerEntity player) {

        LegacyAnimationsRenderer element = (LegacyAnimationsRenderer) GoldenAgeCombat.LEGACY_ANIMATIONS;
        if (element.isEnabled() && element.attackWhileUsing) {

            return false;
        }

        return player.isHandActive();
    }

}
