package fuzs.goldenagecombat.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
abstract class ItemInHandRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "itemUsed", at = @At("HEAD"), cancellable = true)
    public void itemUsed(InteractionHand interactionHand, CallbackInfo callback) {
        if (!GoldenAgeCombat.CONFIG.get(ClientConfig.class).noReequipWhenUsing) {
            return;
        }

        // don't play the re-equip animation when beginning to use an item, like shield or bow
        if (this.minecraft.player.isUsingItem() && this.minecraft.player.getUsedItemHand() == interactionHand) {
            callback.cancel();
        }
    }

    @Inject(method = "renderArmWithItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V",
                    shift = At.Shift.AFTER))
    private void renderArmWithItem(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand interactionHand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int combinedLight, CallbackInfo callback) {
        if (!GoldenAgeCombat.CONFIG.get(ClientConfig.class).interactAnimations) {
            return;
        }
        
        if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0
                && player.getUsedItemHand() == interactionHand) {
            HumanoidArm humanoidArm = interactionHand == InteractionHand.MAIN_HAND ? player.getMainArm() :
                    player.getMainArm().getOpposite();
            this.applyItemArmAttackTransform(poseStack, humanoidArm, swingProgress);
        }
    }

    @Shadow
    private void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm hand, float swingProgress) {
        throw new RuntimeException();
    }
}
