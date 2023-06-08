package fuzs.goldenagecombat.client.handler;

import fuzs.goldenagecombat.mixin.client.accessor.ItemInHandRendererAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ClientConfig;
import fuzs.goldenagecombat.handler.SwordBlockingHandler;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FirstPersonRenderingHandler {

    public static EventResult onRenderHand(Player player, InteractionHand hand, ItemStack stack, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float partialTick, float interpolatedPitch, float swingProgress, float equipProgress) {
        if (player.getUsedItemHand() == hand && SwordBlockingHandler.isActiveItemStackBlocking(player)) {
            Minecraft minecraft = Minecraft.getInstance();
            ItemInHandRenderer itemRenderer = minecraft.getEntityRenderDispatcher().getItemInHandRenderer();
            poseStack.pushPose();
            boolean mainHand = hand == InteractionHand.MAIN_HAND;
            HumanoidArm handSide = mainHand ? player.getMainArm() : player.getMainArm().getOpposite();
            boolean isHandSideRight = handSide == HumanoidArm.RIGHT;
            ((ItemInHandRendererAccessor) itemRenderer).goldenagecombat$callApplyItemArmTransform(poseStack, handSide, equipProgress);
            if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).animations.interactAnimations) {
                ((ItemInHandRendererAccessor) itemRenderer).goldenagecombat$callApplyItemArmAttackTransform(poseStack, handSide, swingProgress);
            }
            transformBlockFirstPerson(poseStack, handSide);
            itemRenderer.renderItem(player, stack, isHandSideRight ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !isHandSideRight, poseStack, multiBufferSource, packedLight);
            poseStack.popPose();
            return EventResult.INTERRUPT;
        }
        return EventResult.PASS;
    }

    private static void transformBlockFirstPerson(PoseStack matrixStack, HumanoidArm hand) {
        int direction = hand == HumanoidArm.RIGHT ? 1 : -1;
        // values taken from Minecraft snapshot 15w33b
        matrixStack.translate(direction * -0.14142136F, 0.08F, 0.14142136F);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-102.25F));
        matrixStack.mulPose(Axis.YP.rotationDegrees(direction * 13.365F));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(direction * 78.05F));
    }
}
