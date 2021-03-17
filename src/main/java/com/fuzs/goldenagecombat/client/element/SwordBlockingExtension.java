package com.fuzs.goldenagecombat.client.element;

import com.fuzs.goldenagecombat.element.SwordBlockingElement;
import com.fuzs.goldenagecombat.mixin.client.accessor.IFirstPersonRendererAccessor;
import com.fuzs.goldenagecombat.util.BlockingHelper;
import com.fuzs.puzzleslib_gc.element.extension.ElementExtension;
import com.fuzs.puzzleslib_gc.element.side.IClientElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.ForgeConfigSpec;

public class SwordBlockingExtension extends ElementExtension<SwordBlockingElement> implements IClientElement {

    private final Minecraft mc = Minecraft.getInstance();
    private final BlockingHelper blockingHelper = new BlockingHelper();

    public BlockingPose blockingPose;
    private boolean blockHitting;

    public SwordBlockingExtension(SwordBlockingElement parent) {

        super(parent);
    }

    @Override
    public void setupClient() {

        this.addListener(this::onRenderHand);
    }

    @Override
    public void setupClientConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Third-person pose when blocking, \"MODERN\" is from Minecraft 1.8, \"LEGACY\" from game versions before that.").defineEnum("Third-Person Blocking Pose", BlockingPose.LEGACY), v -> this.blockingPose = v);
        addToConfig(builder.comment("Hit and block with your sword at the same time.").define("Allow Block Hitting", true), v -> this.blockHitting = v);
    }

    private void onRenderHand(final RenderHandEvent evt) {

        ClientPlayerEntity player = this.mc.player;
        ItemStack stack = evt.getItemStack();
        if (player.getActiveHand() == evt.getHand() && this.blockingHelper.isActiveItemStackBlocking(player)) {

            evt.setCanceled(true);
            FirstPersonRenderer itemRenderer = this.mc.getFirstPersonRenderer();
            MatrixStack matrixStack = evt.getMatrixStack();

            matrixStack.push();
            boolean isMainHand = evt.getHand() == Hand.MAIN_HAND;
            HandSide handSide = isMainHand ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
            boolean isHandSideRight = handSide == HandSide.RIGHT;

            ((IFirstPersonRendererAccessor) itemRenderer).callTransformSideFirstPerson(matrixStack, handSide, evt.getEquipProgress());
            if (this.blockHitting) {

                ((IFirstPersonRendererAccessor) itemRenderer).callTransformFirstPerson(matrixStack, handSide, evt.getSwingProgress());
            }

            this.transformBlockFirstPerson(matrixStack, handSide);
            itemRenderer.renderItemSide(player, stack, isHandSideRight ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND :
                    ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !isHandSideRight, matrixStack, evt.getBuffers(), evt.getLight());
            matrixStack.pop();
        }
    }

    private void transformBlockFirstPerson(MatrixStack matrixStack, HandSide hand) {

        int sideSignum = hand == HandSide.RIGHT ? 1 : -1;
        // values taken from Minecraft snapshot 15w33b
        matrixStack.translate(sideSignum * -0.14142136F, 0.08F, 0.14142136F);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(-102.25F));
        matrixStack.rotate(Vector3f.YP.rotationDegrees(sideSignum * 13.365F));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(sideSignum * 78.05F));
    }

    public enum BlockingPose {

        LEGACY, MODERN
    }

}
