package com.fuzs.goldenagecombat.client.renderer;

import com.fuzs.goldenagecombat.client.config.ClientConfigHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("DuplicatedCode")
public class FirstPersonPunchRenderer {

    private final Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings({"unused", "deprecation"})
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderHand(final RenderHandEvent evt) {

        ClientPlayerEntity player = this.mc.player;
        ItemStack stack = evt.getItemStack();

        if (!ClientConfigHandler.ANIMATIONS_PUNCHING.get() || stack.isEmpty() || stack.getItem() instanceof FilledMapItem || player == null) {

            return;
        }

        if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == evt.getHand()) {

            FirstPersonRenderer firstPersonRenderer = this.mc.getFirstPersonRenderer();

            float equippedProgress = evt.getEquipProgress();
            float partialTicks = evt.getPartialTicks();
            float swingProgress = evt.getSwingProgress();
            MatrixStack matrixStack = evt.getMatrixStack();

            boolean flag = evt.getHand() == Hand.MAIN_HAND;
            HandSide handside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
            boolean flag3 = (flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite()) == HandSide.RIGHT;

            matrixStack.push();
            switch(stack.getUseAction()) {
                case NONE:
                case BLOCK:
                    firstPersonRenderer.transformSideFirstPerson(matrixStack, handside, equippedProgress);
                    firstPersonRenderer.transformFirstPerson(matrixStack, handside, swingProgress);
                    break;
                case EAT:
                case DRINK:
                    firstPersonRenderer.transformEatFirstPerson(matrixStack, partialTicks, handside, stack);
                    firstPersonRenderer.transformSideFirstPerson(matrixStack, handside, equippedProgress);
//                    firstPersonRenderer.transformFirstPerson(matrixStack, handside, swingProgress);
                    break;
                case BOW:
                    firstPersonRenderer.transformSideFirstPerson(matrixStack, handside, equippedProgress);
                    firstPersonRenderer.transformFirstPerson(matrixStack, handside, swingProgress);
                    this.transformBowFirstPerson(matrixStack, partialTicks, handside, stack);
                    break;
                case SPEAR:
                    firstPersonRenderer.transformSideFirstPerson(matrixStack, handside, equippedProgress);
                    firstPersonRenderer.transformFirstPerson(matrixStack, handside, swingProgress);
                    this.transformSpearFirstPerson(matrixStack, partialTicks, handside, stack);
                    break;
                case CROSSBOW:
                    firstPersonRenderer.transformSideFirstPerson(matrixStack, handside, equippedProgress);
                    firstPersonRenderer.transformFirstPerson(matrixStack, handside, swingProgress);
                    this.transformCrossbowFirstPerson(matrixStack, partialTicks, handside, stack);
            }

            this.mc.getFirstPersonRenderer().renderItemSide(player, stack, flag3 ? net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND :
                    net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag3, matrixStack, evt.getBuffers(), evt.getLight());
            matrixStack.pop();

            evt.setCanceled(true);
        }
    }

    private void transformBowFirstPerson(MatrixStack matrixStackIn, float partialTicks, HandSide handside, ItemStack stack) {

        int i = handside == HandSide.RIGHT ? 1 : -1;
        matrixStackIn.translate((float)i * -0.2785682F, 0.18344387F, 0.15731531F);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-13.935F));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)i * 35.3F));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)i * -9.785F));
        float f8 = (float)stack.getUseDuration() - ((this.mc.player != null ? (float)this.mc.player.getItemInUseCount() : 0.0F) - partialTicks + 1.0F);
        float f12 = f8 / 20.0F;
        f12 = (f12 * f12 + f12 * 2.0F) / 3.0F;
        if (f12 > 1.0F) {
            f12 = 1.0F;
        }

        if (f12 > 0.1F) {
            float f15 = MathHelper.sin((f8 - 0.1F) * 1.3F);
            float f18 = f12 - 0.1F;
            float f20 = f15 * f18;
            matrixStackIn.translate(f20 * 0.0F, f20 * 0.004F, f20 * 0.0F);
        }

        matrixStackIn.translate(f12 * 0.0F, f12 * 0.0F, f12 * 0.04F);
        matrixStackIn.scale(1.0F, 1.0F, 1.0F + f12 * 0.2F);
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees((float)i * 45.0F));
    }

    private void transformSpearFirstPerson(MatrixStack matrixStackIn, float partialTicks, HandSide handside, ItemStack stack) {

        int i = handside == HandSide.RIGHT ? 1 : -1;
        matrixStackIn.translate((float)i * -0.5F, 0.7F, 0.1F);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-55.0F));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)i * 35.3F));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)i * -9.785F));
        float f7 = (float)stack.getUseDuration() - ((this.mc.player != null ? (float)this.mc.player.getItemInUseCount() : 0.0F) - partialTicks + 1.0F);
        float f11 = f7 / 10.0F;
        if (f11 > 1.0F) {
            f11 = 1.0F;
        }

        if (f11 > 0.1F) {
            float f14 = MathHelper.sin((f7 - 0.1F) * 1.3F);
            float f17 = f11 - 0.1F;
            float f19 = f14 * f17;
            matrixStackIn.translate(f19 * 0.0F, f19 * 0.004F, f19 * 0.0F);
        }

        matrixStackIn.translate(0.0D, 0.0D, f11 * 0.2F);
        matrixStackIn.scale(1.0F, 1.0F, 1.0F + f11 * 0.2F);
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees((float)i * 45.0F));
    }

    private void transformCrossbowFirstPerson(MatrixStack matrixStackIn, float partialTicks, HandSide handside, ItemStack stack) {

        int i = handside == HandSide.RIGHT ? 1 : -1;
        matrixStackIn.translate((float)i * -0.4785682F, -0.094387F, 0.05731531F);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-11.935F));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)i * 65.3F));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)i * -9.785F));
        float f9 = (float)stack.getUseDuration() - ((this.mc.player != null ? (float)this.mc.player.getItemInUseCount() : 0.0F) - partialTicks + 1.0F);
        float f13 = f9 / (float) CrossbowItem.getChargeTime(stack);
        if (f13 > 1.0F) {
            f13 = 1.0F;
        }

        if (f13 > 0.1F) {
            float f16 = MathHelper.sin((f9 - 0.1F) * 1.3F);
            float f3 = f13 - 0.1F;
            float f4 = f16 * f3;
            matrixStackIn.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
        }

        matrixStackIn.translate(f13 * 0.0F, f13 * 0.0F, f13 * 0.04F);
        matrixStackIn.scale(1.0F, 1.0F, 1.0F + f13 * 0.2F);
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees((float)i * 45.0F));
    }

}
