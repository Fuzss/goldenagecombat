package com.fuzs.goldenagecombat.client.renderer;

import com.fuzs.goldenagecombat.client.config.ClientConfigHandler;
import com.fuzs.goldenagecombat.util.BlockingItemHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class FirstPersonBlockingRenderer {

    private final BlockingItemHelper blockingHelper = new BlockingItemHelper();
    private final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void onRenderHand(final RenderHandEvent evt) {

        ClientPlayerEntity player = this.mc.player;
        ItemStack stack = evt.getItemStack();
        if (player.getActiveHand() == evt.getHand() && this.blockingHelper.isActiveItemStackBlocking(player)) {

            FirstPersonRenderer itemRenderer = this.mc.getFirstPersonRenderer();
            MatrixStack matrixStack = evt.getMatrixStack();
            matrixStack.push();
            boolean isMainHand = evt.getHand() == Hand.MAIN_HAND;
            HandSide handSide = isMainHand ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
            boolean isHandSideRight = handSide == HandSide.RIGHT;

            itemRenderer.transformSideFirstPerson(matrixStack, handSide, evt.getEquipProgress());
            if (ClientConfigHandler.ANIMATIONS.get() && ClientConfigHandler.ANIMATIONS_BLOCKHITTING.get()) {

                itemRenderer.transformFirstPerson(matrixStack, handSide, evt.getSwingProgress());
            }
            this.transformBlockFirstPerson(matrixStack, handSide);

            itemRenderer.renderItemSide(player, stack, isHandSideRight ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND :
                    ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !isHandSideRight, matrixStack, evt.getBuffers(), evt.getLight());
            matrixStack.pop();
            evt.setCanceled(true);
        }
    }

    private void transformBlockFirstPerson(MatrixStack matrixStack, HandSide hand) {

        int sideSignum = hand == HandSide.RIGHT ? 1 : -1;
        // values taken from Minecraft snapshot 15w33b
        matrixStack.translate((float) sideSignum * -0.14142136F, 0.08F, 0.14142136F);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(-102.25F));
        matrixStack.rotate(Vector3f.YP.rotationDegrees((float) sideSignum * 13.365F));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees((float) sideSignum * 78.05F));
    }

}
