package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Final
    public Options options;
    @Shadow
    public MultiPlayerGameMode gameMode;
    @Shadow
    public LocalPlayer player;

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z", ordinal = 0))
    public boolean handleKeybinds$isUsingItem(LocalPlayer player) {
        if (!GoldenAgeCombat.CONFIG.client().animations.attackWhileUsing) return player.isUsingItem();
        if (player.isUsingItem()) {
            if (!this.options.keyUse.isDown()) {
                this.gameMode.releaseUsingItem(this.player);
            }
            while (this.options.keyUse.consumeClick()) {
            }
        }
        return false;
    }

    @Redirect(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    public boolean continueAttack$isUsingItem(LocalPlayer player) {
        if (!GoldenAgeCombat.CONFIG.client().animations.attackWhileUsing) return player.isUsingItem();
        return false;
    }
}
