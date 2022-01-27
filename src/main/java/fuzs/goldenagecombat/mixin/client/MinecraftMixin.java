package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
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
    protected int missTime;
    @Shadow
    public HitResult hitResult;

    @Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z", ordinal = 0))
    public void handleKeybinds$isUsingItem(CallbackInfo callbackInfo) {
        // required for enabling block breaking while e.g. sword blocking
        // it is actually enabled by a different patch below, this just makes sure breaking particles show correctly (which only works sometimes otherwise)
        if (!GoldenAgeCombat.CONFIG.server().classic.attackWhileUsing || !this.player.isUsingItem()) return;
        while (this.options.keyAttack.consumeClick()) {
            this.startBlockAttack();
        }
    }

    @Unique
    private void startBlockAttack() {
        if (this.missTime <= 0) {
            if (this.hitResult == null) {
                LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
                if (this.gameMode.hasMissTime()) {
                    this.missTime = 10;
                }
            } else if (!this.player.isHandsBusy()) {
                net.minecraftforge.client.event.InputEvent.ClickInputEvent inputEvent = net.minecraftforge.client.ForgeHooksClient.onClickInput(0, this.options.keyAttack, InteractionHand.MAIN_HAND);
                if (!inputEvent.isCanceled() && this.hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockhitresult = (BlockHitResult) this.hitResult;
                    BlockPos blockpos = blockhitresult.getBlockPos();
                    if (!this.level.isEmptyBlock(blockpos)) {
                        this.gameMode.startDestroyBlock(blockpos, blockhitresult.getDirection());
                        return;
                    }
                    if (inputEvent.shouldSwingHand()) {
                        this.player.swing(InteractionHand.MAIN_HAND);
                    }
                }
            }
        }
    }

    @Redirect(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    public boolean continueAttack$isUsingItem(LocalPlayer player) {
        if (!GoldenAgeCombat.CONFIG.server().classic.attackWhileUsing) return player.isUsingItem();
        return false;
    }

    @Redirect(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isDestroying()Z"))
    public boolean startUseItem$isDestroying(MultiPlayerGameMode gameMode) {
        if (!GoldenAgeCombat.CONFIG.server().classic.attackWhileUsing) return gameMode.isDestroying();
        return false;
    }
}