package fuzs.goldenagecombat.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import fuzs.goldenagecombat.mixin.client.accessor.ItemInHandRendererAccessor;
import com.fuzs.puzzleslib_gc.element.AbstractElement;
import com.fuzs.puzzleslib_gc.element.side.IClientElement;
import com.mojang.blaze3d.matrix.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.Mth;
import net.minecraft.util.math.Mth;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

@SuppressWarnings({"ConstantConditions", "DuplicatedCode"})
public class LegacyAnimationsRenderer {

    private final Minecraft minecraft = Minecraft.getInstance();
    private final Random random = new Random();

    @SubscribeEvent
    public void onRenderHand(final RenderHandEvent evt) {

        ItemStack stack = evt.getItemStack();
        if (!this.attackWhileUsing || stack.isEmpty() || stack.getItem() instanceof FilledMapItem) {

            return;
        }

        Player player = this.minecraft.player;
        if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == evt.getHand()) {

            evt.setCanceled(true);
            ItemInHandRenderer itemRenderer = this.minecraft.getItemInHandRenderer();
            PoseStack matrixStack = evt.getPoseStack();
            float partialTicks = evt.getPartialTicks();
            float equippedProgress = evt.getEquipProgress();
            float swingProgress = evt.getSwingProgress();
            boolean isMainHand = evt.getHand() == InteractionHand.MAIN_HAND;
            HumanoidArm handSide = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();
            boolean isHandSideRight = (isMainHand ? player.getPrimaryHand() : player.getPrimaryHand().opposite()) == HandSide.RIGHT;

            matrixStack.pushPose();
            switch(stack.getUseAnimation()) {

                case NONE:
                case BLOCK:

                    ((ItemInHandRendererAccessor) itemRenderer).callApplyItemArmTransform(matrixStack, handSide, equippedProgress);
                    ((ItemInHandRendererAccessor) itemRenderer).callApplyItemArmAttackTransform(matrixStack, handSide, swingProgress);
                    break;
                case EAT:

                    ((ItemInHandRendererAccessor) itemRenderer).callApplyEatTransform(matrixStack, partialTicks, handSide, stack);
                    ((ItemInHandRendererAccessor) itemRenderer).callApplyItemArmTransform(matrixStack, handSide, equippedProgress);
                    ((ItemInHandRendererAccessor) itemRenderer).callApplyItemArmAttackTransform(matrixStack, handSide, swingProgress);
                    break;
                case DRINK:

                    // vanilla bug will cause a hit when using, screwing with the whole animation
                    ((ItemInHandRendererAccessor) itemRenderer).callApplyEatTransform(matrixStack, partialTicks, handSide, stack);
                    ((ItemInHandRendererAccessor) itemRenderer).callApplyItemArmTransform(matrixStack, handSide, equippedProgress);
                    break;
                case BOW:

                    ((ItemInHandRendererAccessor) itemRenderer).callApplyItemArmTransform(matrixStack, handSide, equippedProgress);
                    ((ItemInHandRendererAccessor) itemRenderer).callApplyItemArmAttackTransform(matrixStack, handSide, swingProgress);
                    this.transformBowFirstPerson(matrixStack, partialTicks, handSide, stack);
                    break;
                case SPEAR:

                    ((ItemInHandRendererAccessor) itemRenderer).callApplyItemArmTransform(matrixStack, handSide, equippedProgress);
                    ((ItemInHandRendererAccessor) itemRenderer).callApplyItemArmAttackTransform(matrixStack, handSide, swingProgress);
                    this.transformSpearFirstPerson(matrixStack, partialTicks, handSide, stack);
                    break;
                case CROSSBOW:

                    ((ItemInHandRendererAccessor) itemRenderer).callApplyItemArmTransform(matrixStack, handSide, equippedProgress);
                    ((ItemInHandRendererAccessor) itemRenderer).callApplyItemArmAttackTransform(matrixStack, handSide, swingProgress);
                    this.transformCrossbowFirstPerson(matrixStack, partialTicks, handSide, stack);
                    break;
            }

            this.minecraft.getFirstPersonRenderer().renderItemSide(player, stack, isHandSideRight ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !isHandSideRight, matrixStack, evt.getBuffers(), evt.getLight());
            matrixStack.pop();
        }
    }

    private void transformBowFirstPerson(PoseStack matrixStackIn, float partialTicks, HumanoidArm handside, ItemStack stack) {

        int sideSignum = handside == HumanoidArm.RIGHT ? 1 : -1;
        matrixStackIn.translate(sideSignum * -0.2785682F, 0.18344387F, 0.15731531F);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-13.935F));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(sideSignum * 35.3F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(sideSignum * -9.785F));
        float f8 = stack.getUseDuration() - ((this.minecraft.player != null ? this.minecraft.player.getItemInUseCount() : 0.0F) - partialTicks + 1.0F);
        float f12 = f8 / 20.0F;
        f12 = (f12 * f12 + f12 * 2.0F) / 3.0F;
        if (f12 > 1.0F) {

            f12 = 1.0F;
        }

        if (f12 > 0.1F) {

            float f15 = Mth.sin((f8 - 0.1F) * 1.3F);
            float f18 = f12 - 0.1F;
            float f20 = f15 * f18;
            matrixStackIn.translate(f20 * 0.0F, f20 * 0.004F, f20 * 0.0F);
        }

        matrixStackIn.translate(f12 * 0.0F, f12 * 0.0F, f12 * 0.04F);
        matrixStackIn.scale(1.0F, 1.0F, 1.0F + f12 * 0.2F);
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(sideSignum * 45.0F));
    }

    private void transformSpearFirstPerson(PoseStack matrixStackIn, float partialTicks, HandSide handside, ItemStack stack) {

        int sideSignum = handside == HandSide.RIGHT ? 1 : -1;
        matrixStackIn.translate(sideSignum * -0.5F, 0.7F, 0.1F);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-55.0F));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(sideSignum * 35.3F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(sideSignum * -9.785F));
        float f7 = stack.getUseDuration() - ((this.minecraft.player != null ? this.minecraft.player.getItemInUseCount() : 0.0F) - partialTicks + 1.0F);
        float f11 = f7 / 10.0F;
        if (f11 > 1.0F) {

            f11 = 1.0F;
        }

        if (f11 > 0.1F) {

            float f14 = Mth.sin((f7 - 0.1F) * 1.3F);
            float f17 = f11 - 0.1F;
            float f19 = f14 * f17;
            matrixStackIn.translate(f19 * 0.0F, f19 * 0.004F, f19 * 0.0F);
        }

        matrixStackIn.translate(0.0D, 0.0D, f11 * 0.2F);
        matrixStackIn.scale(1.0F, 1.0F, 1.0F + f11 * 0.2F);
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(sideSignum * 45.0F));
    }

    private void transformCrossbowFirstPerson(PoseStack matrixStackIn, float partialTicks, HandSide handside, ItemStack stack) {

        int sideSignum = handside == HandSide.RIGHT ? 1 : -1;
        matrixStackIn.translate(sideSignum * -0.4785682F, -0.094387F, 0.05731531F);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-11.935F));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(sideSignum * 65.3F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(sideSignum * -9.785F));
        float f9 = stack.getUseDuration() - (this.minecraft.player.getUseItemRemainingTicks() - partialTicks + 1.0F);
        float f13 = f9 / CrossbowItem.getChargeDuration(stack);
        if (f13 > 1.0F) {

            f13 = 1.0F;
        }

        if (f13 > 0.1F) {

            float f16 = Mth.sin((f9 - 0.1F) * 1.3F);
            float f3 = f13 - 0.1F;
            float f4 = f16 * f3;
            matrixStackIn.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
        }

        matrixStackIn.translate(f13 * 0.0F, f13 * 0.0F, f13 * 0.04F);
        matrixStackIn.scale(1.0F, 1.0F, 1.0F + f13 * 0.2F);
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(sideSignum * 45.0F));
    }

    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent.Pre evt) {

        if (!this.noFlashingHearts || evt.getType() != RenderGameOverlayEvent.ElementType.HEALTH || !(this.minecraft.getRenderViewEntity() instanceof PlayerEntity)) {

            return;
        }

        evt.setCanceled(true);
        this.minecraft.getProfiler().startSection("health");
        RenderSystem.enableBlend();

        Player player = (Player) this.minecraft.getRenderViewEntity();
        PoseStack matrixStack = evt.getPoseStack();
        boolean raiseHeart = player.hurtResistantTime / 3 % 2 == 1;
        if (player.hurtResistantTime <= 10) {

            // prevent single flash
            raiseHeart = false;
        }

        int playerHealth = Mth.ceil(player.getHealth());
        float maxHealth = (float) player.getAttributeValue(Attributes.MAX_HEALTH);
        int playerAbsorption = Mth.ceil(player.getAbsorptionAmount());
        int healthRows = Mth.ceil((maxHealth + playerAbsorption) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        int ticks = this.minecraft.gui.getGuiTicks();
        this.random.setSeed(ticks * 312871L);
        int renderStartX = evt.getWindow().getScaledWidth() / 2 - 91;
        int renderStartY = evt.getWindow().getScaledHeight() - ForgeIngameGui.left_height;
        ForgeIngameGui.left_height += (healthRows * rowHeight);
        if (rowHeight != 10) {

            ForgeIngameGui.left_height += 10 - rowHeight;
        }

        int i3 = playerAbsorption;
        int isRegenerating = -1;
        if (player.isPotionActive(MobEffects.REGENERATION)) {

            isRegenerating = ticks % Mth.ceil(maxHealth + 5.0F);
        }

        for (int l5 = Mth.ceil((maxHealth + playerAbsorption) / 2.0F) - 1; l5 >= 0; --l5) {

            int potionTextureMargin = 16;
            if (player.isPotionActive(MobEffects.POISON)) {

                potionTextureMargin += 36;
            } else if (player.hasEffect(MobEffects.WITHER)) {

                potionTextureMargin += 72;
            }

            int heartYOffset = raiseHeart ? 1 : 0;
            int k4 = Mth.ceil((l5 + 1) / 10.0F) - 1;
            int posX = renderStartX + l5 % 10 * 8;
            int posY = renderStartY - k4 * rowHeight;
            if (playerHealth <= 4) {

                posY += this.random.nextInt(2);
            }

            if (i3 <= 0 && l5 == isRegenerating) {

                posY -= 2;
            }

            int hardcoreTextureMargin = player.level.getLevelData().isHardcore() ? 5 : 0;
            GuiComponent.blit(matrixStack, posX, posY, 16 + heartYOffset * 9, 9 * hardcoreTextureMargin, 9, 9, 256, 256);
            if (i3 > 0) {

                if (i3 == playerAbsorption && playerAbsorption % 2 == 1) {

                    GuiComponent.blit(matrixStack, posX, posY, potionTextureMargin + 153, 9 * hardcoreTextureMargin, 9, 9, 256, 256);
                    --i3;
                } else {

                    GuiComponent.blit(matrixStack, posX, posY, potionTextureMargin + 144, 9 * hardcoreTextureMargin, 9, 9, 256, 256);
                    i3 -= 2;
                }
            } else {

                if (l5 * 2 + 1 < playerHealth) {

                    GuiComponent.blit(matrixStack, posX, posY, potionTextureMargin + 36, 9 * hardcoreTextureMargin, 9, 9, 256, 256);
                }

                if (l5 * 2 + 1 == playerHealth) {

                    GuiComponent.blit(matrixStack, posX, posY, potionTextureMargin + 45, 9 * hardcoreTextureMargin, 9, 9, 256, 256);
                }
            }
        }

        RenderSystem.disableBlend();
        this.minecraft.getProfiler().pop();
    }

}
