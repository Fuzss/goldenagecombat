package fuzs.goldenagecombat.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.handler.SwordBlockingHandler;
import fuzs.goldenagecombat.mixin.client.accessor.ItemInHandRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SwordBlockingRenderer {
    @SubscribeEvent
    public void onRenderHand(final RenderHandEvent evt) {
        final Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player.getUsedItemHand() == evt.getHand() && SwordBlockingHandler.isActiveItemStackBlocking(player)) {
            ItemInHandRenderer itemRenderer = minecraft.getItemInHandRenderer();
            PoseStack matrixStack = evt.getPoseStack();
            matrixStack.pushPose();
            boolean isMainHand = evt.getHand() == InteractionHand.MAIN_HAND;
            HumanoidArm handSide = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();
            boolean isHandSideRight = handSide == HumanoidArm.RIGHT;
            ((ItemInHandRendererAccessor) itemRenderer).callApplyItemArmTransform(matrixStack, handSide, evt.getEquipProgress());
            if (GoldenAgeCombat.CONFIG.client().animations.attackWhileUsing) {
                ((ItemInHandRendererAccessor) itemRenderer).callApplyItemArmAttackTransform(matrixStack, handSide, evt.getSwingProgress());
            }
            this.transformBlockFirstPerson(matrixStack, handSide);
            itemRenderer.renderItem(player, evt.getItemStack(), isHandSideRight ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !isHandSideRight, matrixStack, evt.getMultiBufferSource(), evt.getPackedLight());
            matrixStack.popPose();
            evt.setCanceled(true);
        }
    }

    private void transformBlockFirstPerson(PoseStack matrixStack, HumanoidArm hand) {
        int signum = hand == HumanoidArm.RIGHT ? 1 : -1;
        // values taken from Minecraft snapshot 15w33b
        matrixStack.translate(signum * -0.14142136F, 0.08F, 0.14142136F);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-102.25F));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(signum * 13.365F));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(signum * 78.05F));
    }
}
