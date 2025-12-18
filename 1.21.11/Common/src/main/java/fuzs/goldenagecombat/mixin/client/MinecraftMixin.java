package fuzs.goldenagecombat.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    @Final
    public Options options;
    @Shadow
    public MultiPlayerGameMode gameMode;
    @Shadow
    public ClientLevel level;
    @Shadow
    public LocalPlayer player;
    @Shadow
    public int missTime;
    @Shadow
    public HitResult hitResult;

    @Inject(method = "handleKeybinds",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z", ordinal = 0))
    public void handleKeybinds(CallbackInfo callback) {
        // required for enabling block breaking while e.g. sword blocking
        // it is actually enabled by a different patch below; this just makes sure breaking particles show correctly (which only works sometimes otherwise)
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).interactWhileUsing || !this.player.isUsingItem()) {
            return;
        }

        while (this.options.keyAttack.consumeClick()) {
            this.goldenagecombat$startBlockAttack();
        }
    }

    @Unique
    private void goldenagecombat$startBlockAttack() {
        if (this.missTime <= 0) {
            if (this.hitResult == null) {
                LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
                if (this.gameMode.hasMissTime()) {
                    this.missTime = 10;
                }
            } else if (this.player.getItemInHand(InteractionHand.MAIN_HAND).isItemEnabled(this.level.enabledFeatures())
                    && !this.player.isHandsBusy()) {
                if (this.hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockhitresult = (BlockHitResult) this.hitResult;
                    BlockPos blockpos = blockhitresult.getBlockPos();
                    if (!this.level.isEmptyBlock(blockpos)) {
                        this.gameMode.startDestroyBlock(blockpos, blockhitresult.getDirection());
                        return;
                    }

                    this.player.swing(InteractionHand.MAIN_HAND);
                }
            }
        }
    }

    @ModifyExpressionValue(method = "continueAttack",
                           at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    public boolean continueAttack(boolean isUsingItem) {
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).interactWhileUsing) {
            return false;
        } else {
            return isUsingItem;
        }
    }

    @ModifyExpressionValue(method = "startUseItem",
                           at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isDestroying()Z"))
    public boolean startUseItem(boolean isDestroying) {
        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).interactWhileUsing) {
            return false;
        } else {
            return isDestroying;
        }
    }
}
