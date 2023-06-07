package fuzs.goldenagecombat.client.handler;

import fuzs.goldenagecombat.mixin.client.accessor.ItemInHandRendererAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ClientConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemInHandHandler {
    
    public static EventResult onRenderHand(Player player, InteractionHand hand, ItemStack stack, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float partialTick, float interpolatedPitch, float swingProgress, float equipProgress) {
        if (!GoldenAgeCombat.CONFIG.get(ClientConfig.class).animations.interactAnimations || stack.isEmpty() || stack.is(Items.FILLED_MAP)) return EventResult.PASS;
        if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == hand) {
            Minecraft minecraft = Minecraft.getInstance();
            ItemInHandRenderer itemRenderer = minecraft.gameRenderer.itemInHandRenderer;
            boolean mainHand = hand == InteractionHand.MAIN_HAND;
            HumanoidArm humanoidArm = mainHand ? player.getMainArm() : player.getMainArm().getOpposite();
            boolean rightArm = (mainHand ? player.getMainArm() : player.getMainArm().getOpposite()) == HumanoidArm.RIGHT;
            poseStack.pushPose();
            switch (stack.getUseAnimation()) {
                case NONE, BLOCK -> {
                    ((ItemInHandRendererAccessor) itemRenderer).goldenagecombat$callApplyItemArmTransform(poseStack, humanoidArm, equipProgress);
                    ((ItemInHandRendererAccessor) itemRenderer).goldenagecombat$callApplyItemArmAttackTransform(poseStack, humanoidArm, swingProgress);
                }
                case EAT, DRINK -> {
                    ((ItemInHandRendererAccessor) itemRenderer).goldenagecombat$callApplyEatTransform(poseStack, partialTick, humanoidArm, stack);
                    ((ItemInHandRendererAccessor) itemRenderer).goldenagecombat$callApplyItemArmTransform(poseStack, humanoidArm, equipProgress);
                    ((ItemInHandRendererAccessor) itemRenderer).goldenagecombat$callApplyItemArmAttackTransform(poseStack, humanoidArm, swingProgress);
                }
                case BOW -> {
                    ((ItemInHandRendererAccessor) itemRenderer).goldenagecombat$callApplyItemArmTransform(poseStack, humanoidArm, equipProgress);
                    ((ItemInHandRendererAccessor) itemRenderer).goldenagecombat$callApplyItemArmAttackTransform(poseStack, humanoidArm, swingProgress);
                    applyBowTransform(poseStack, partialTick, humanoidArm, stack, player);
                }
                case SPEAR -> {
                    ((ItemInHandRendererAccessor) itemRenderer).goldenagecombat$callApplyItemArmTransform(poseStack, humanoidArm, equipProgress);
                    ((ItemInHandRendererAccessor) itemRenderer).goldenagecombat$callApplyItemArmAttackTransform(poseStack, humanoidArm, swingProgress);
                    applyTridentTransform(poseStack, partialTick, humanoidArm, stack, player);
                }
                case CROSSBOW -> {
                    ((ItemInHandRendererAccessor) itemRenderer).goldenagecombat$callApplyItemArmTransform(poseStack, humanoidArm, equipProgress);
                    ((ItemInHandRendererAccessor) itemRenderer).goldenagecombat$callApplyItemArmAttackTransform(poseStack, humanoidArm, swingProgress);
                    applyCrossbowTransform(poseStack, partialTick, humanoidArm, stack, player);
                }
            }
            itemRenderer.renderItem(player, stack, rightArm ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !rightArm, poseStack, multiBufferSource, packedLight);
            poseStack.popPose();
            return EventResult.INTERRUPT;
        }
        return EventResult.PASS;
    }

    private static void applyBowTransform(PoseStack poseStack, float partialTick, HumanoidArm humanoidArm, ItemStack stack, Player player) {
        int direction = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(direction * -0.2785682F, 0.18344387F, 0.15731531F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-13.935F));
        poseStack.mulPose(Axis.YP.rotationDegrees(direction * 35.3F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(direction * -9.785F));
        float f8 = stack.getUseDuration() - (player.getUseItemRemainingTicks() - partialTick + 1.0F);
        float f12 = f8 / 20.0F;
        f12 = (f12 * f12 + f12 * 2.0F) / 3.0F;
        if (f12 > 1.0F) {
            f12 = 1.0F;
        }
        if (f12 > 0.1F) {
            float f15 = Mth.sin((f8 - 0.1F) * 1.3F);
            float f18 = f12 - 0.1F;
            float f20 = f15 * f18;
            poseStack.translate(f20 * 0.0F, f20 * 0.004F, f20 * 0.0F);
        }
        poseStack.translate(f12 * 0.0F, f12 * 0.0F, f12 * 0.04F);
        poseStack.scale(1.0F, 1.0F, 1.0F + f12 * 0.2F);
        poseStack.mulPose(Axis.YN.rotationDegrees(direction * 45.0F));
    }

    private static void applyTridentTransform(PoseStack poseStack, float partialTick, HumanoidArm humanoidArm, ItemStack stack, Player player) {
        int direction = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(direction * -0.5F, 0.7F, 0.1F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-55.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(direction * 35.3F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(direction * -9.785F));
        float f7 = stack.getUseDuration() - (player.getUseItemRemainingTicks() - partialTick + 1.0F);
        float f11 = f7 / 10.0F;
        if (f11 > 1.0F) {
            f11 = 1.0F;
        }
        if (f11 > 0.1F) {
            float f14 = Mth.sin((f7 - 0.1F) * 1.3F);
            float f17 = f11 - 0.1F;
            float f19 = f14 * f17;
            poseStack.translate(f19 * 0.0F, f19 * 0.004F, f19 * 0.0F);
        }
        poseStack.translate(0.0D, 0.0D, f11 * 0.2F);
        poseStack.scale(1.0F, 1.0F, 1.0F + f11 * 0.2F);
        poseStack.mulPose(Axis.YN.rotationDegrees(direction * 45.0F));
    }

    private static void applyCrossbowTransform(PoseStack poseStack, float partialTick, HumanoidArm humanoidArm, ItemStack stack, Player player) {
        int direction = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(direction * -0.4785682F, -0.094387F, 0.05731531F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-11.935F));
        poseStack.mulPose(Axis.YP.rotationDegrees(direction * 65.3F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(direction * -9.785F));
        float f9 = stack.getUseDuration() - (player.getUseItemRemainingTicks() - partialTick + 1.0F);
        float f13 = f9 / CrossbowItem.getChargeDuration(stack);
        if (f13 > 1.0F) {
            f13 = 1.0F;
        }
        if (f13 > 0.1F) {
            float f16 = Mth.sin((f9 - 0.1F) * 1.3F);
            float f3 = f13 - 0.1F;
            float f4 = f16 * f3;
            poseStack.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
        }
        poseStack.translate(f13 * 0.0F, f13 * 0.0F, f13 * 0.04F);
        poseStack.scale(1.0F, 1.0F, 1.0F + f13 * 0.2F);
        poseStack.mulPose(Axis.YN.rotationDegrees(direction * 45.0F));
    }
}
