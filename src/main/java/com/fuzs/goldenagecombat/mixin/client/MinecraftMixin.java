package com.fuzs.goldenagecombat.mixin.client;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.client.element.LegacyAnimationsElement;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
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
    public GameSettings gameSettings;
    @Shadow
    public PlayerController playerController;
    @Shadow
    public ClientWorld world;
    @Shadow
    public ClientPlayerEntity player;
    @Shadow
    protected int leftClickCounter;
    @Shadow
    public RayTraceResult objectMouseOver;

    @Inject(method = "processKeyBinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;isHandActive()Z", ordinal = 0))
    public void processKeyBinds$isHandActive(CallbackInfo callbackInfo) {
        // required for enabling block breaking while e.g. sword blocking
        // it is actually enabled by a different patch below, this just makes sure breaking particles show correctly (which only works sometimes otherwise)
        LegacyAnimationsElement element = (LegacyAnimationsElement) GoldenAgeCombat.LEGACY_ANIMATIONS;
        if (!element.isEnabled() || !element.attackWhileUsing || !this.player.isHandActive()) return;
        while (this.gameSettings.keyBindAttack.isPressed()) {
            this.clickMouseBlock();
        }
    }

    @Unique
    private void clickMouseBlock() {
        if (this.leftClickCounter <= 0) {
            if (this.objectMouseOver == null) {
                LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
                if (this.playerController.isNotCreative()) {
                    this.leftClickCounter = 10;
                }
            } else if (!this.player.isRowingBoat()) {
                net.minecraftforge.client.event.InputEvent.ClickInputEvent inputEvent = net.minecraftforge.client.ForgeHooksClient.onClickInput(0, this.gameSettings.keyBindAttack, Hand.MAIN_HAND);
                if (!inputEvent.isCanceled() && this.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
                    BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) this.objectMouseOver;
                    BlockPos blockpos = blockraytraceresult.getPos();
                    if (!this.world.isAirBlock(blockpos)) {
                        this.playerController.clickBlock(blockpos, blockraytraceresult.getFace());
                        return;
                    }
                    if (inputEvent.shouldSwingHand()) {
                        this.player.swingArm(Hand.MAIN_HAND);
                    }
                }
            }
        }
    }

    @Redirect(method = "sendClickBlockToController", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;isHandActive()Z"))
    public boolean sendClickBlockToController$isHandActive(ClientPlayerEntity player) {
        LegacyAnimationsElement element = (LegacyAnimationsElement) GoldenAgeCombat.LEGACY_ANIMATIONS;
        if (!element.isEnabled() || !element.attackWhileUsing) return player.isHandActive();
        return false;
    }

    @Redirect(method = "rightClickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerController;getIsHittingBlock()Z"))
    public boolean rightClickMouse$getIsHittingBlock(PlayerController playerController) {
        LegacyAnimationsElement element = (LegacyAnimationsElement) GoldenAgeCombat.LEGACY_ANIMATIONS;
        if (!element.isEnabled() || !element.attackWhileUsing) return playerController.getIsHittingBlock();
        return false;
    }
}
