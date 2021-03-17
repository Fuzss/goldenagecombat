package com.fuzs.goldenagecombat.client.element;

import com.fuzs.goldenagecombat.mixin.client.accessor.IFirstPersonRendererAccessor;
import com.fuzs.puzzleslib_gc.element.AbstractElement;
import com.fuzs.puzzleslib_gc.element.side.IClientElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.Random;

@SuppressWarnings({"ConstantConditions", "DuplicatedCode"})
public class LegacyAnimationsElement extends AbstractElement implements IClientElement {

    private final Minecraft mc = Minecraft.getInstance();
    private final Random rand = new Random();

    public boolean damageOnArmor;
    private boolean bowPunching;
    public boolean blockHitting;
    public boolean noFlashingHearts;

    @Override
    public String getDescription() {

        return "Legacy visuals and animations for miscellaneous things. Heavily inspired by the old \"Orange's 1.7 Animations Mod\".";
    }

    @Override
    public void setupClient() {

        this.addListener(this::onRenderHand, EventPriority.LOW);
        this.addListener(this::onRenderGameOverlay);
    }

    @Override
    public void setupClientConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Armor on entities turns red when they receive damage just like their body.").define("Render Damage On Armor", true), v -> this.damageOnArmor = v);
        addToConfig(builder.comment("Use a bow or eat food while punching at the same time.").define("Allow Bow Punching", true), v -> this.bowPunching = v);
        addToConfig(builder.comment("Hit and block with your sword at the same time.").define("Allow Block Hitting", true), v -> this.blockHitting = v);
        addToConfig(builder.comment("Lost hearts no longer flash when disappearing.").define("Disable Flashing Hearts", false), v -> this.noFlashingHearts = v);
    }

    private void onRenderHand(final RenderHandEvent evt) {

        ItemStack stack = evt.getItemStack();
        if (!this.bowPunching || stack.isEmpty() || stack.getItem() instanceof FilledMapItem) {

            return;
        }

        ClientPlayerEntity player = this.mc.player;
        if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == evt.getHand()) {

            evt.setCanceled(true);
            FirstPersonRenderer itemRenderer = this.mc.getFirstPersonRenderer();
            MatrixStack matrixStack = evt.getMatrixStack();
            float partialTicks = evt.getPartialTicks();
            float equippedProgress = evt.getEquipProgress();
            float swingProgress = evt.getSwingProgress();
            boolean isMainHand = evt.getHand() == Hand.MAIN_HAND;
            HandSide handSide = isMainHand ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
            boolean isHandSideRight = (isMainHand ? player.getPrimaryHand() : player.getPrimaryHand().opposite()) == HandSide.RIGHT;

            matrixStack.push();
            switch(stack.getUseAction()) {

                case NONE:
                case BLOCK:

                    ((IFirstPersonRendererAccessor) itemRenderer).callTransformSideFirstPerson(matrixStack, handSide, equippedProgress);
                    ((IFirstPersonRendererAccessor) itemRenderer).callTransformFirstPerson(matrixStack, handSide, swingProgress);
                    break;
                case EAT:

                    ((IFirstPersonRendererAccessor) itemRenderer).callTransformEatFirstPerson(matrixStack, partialTicks, handSide, stack);
                    ((IFirstPersonRendererAccessor) itemRenderer).callTransformSideFirstPerson(matrixStack, handSide, equippedProgress);
                    ((IFirstPersonRendererAccessor) itemRenderer).callTransformFirstPerson(matrixStack, handSide, swingProgress);
                    break;
                case DRINK:

                    // vanilla bug will cause a hit when using, screwing with the whole animation
                    ((IFirstPersonRendererAccessor) itemRenderer).callTransformEatFirstPerson(matrixStack, partialTicks, handSide, stack);
                    ((IFirstPersonRendererAccessor) itemRenderer).callTransformSideFirstPerson(matrixStack, handSide, equippedProgress);
                    break;
                case BOW:

                    ((IFirstPersonRendererAccessor) itemRenderer).callTransformSideFirstPerson(matrixStack, handSide, equippedProgress);
                    ((IFirstPersonRendererAccessor) itemRenderer).callTransformFirstPerson(matrixStack, handSide, swingProgress);
                    this.transformBowFirstPerson(matrixStack, partialTicks, handSide, stack);
                    break;
                case SPEAR:

                    ((IFirstPersonRendererAccessor) itemRenderer).callTransformSideFirstPerson(matrixStack, handSide, equippedProgress);
                    ((IFirstPersonRendererAccessor) itemRenderer).callTransformFirstPerson(matrixStack, handSide, swingProgress);
                    this.transformSpearFirstPerson(matrixStack, partialTicks, handSide, stack);
                    break;
                case CROSSBOW:

                    ((IFirstPersonRendererAccessor) itemRenderer).callTransformSideFirstPerson(matrixStack, handSide, equippedProgress);
                    ((IFirstPersonRendererAccessor) itemRenderer).callTransformFirstPerson(matrixStack, handSide, swingProgress);
                    this.transformCrossbowFirstPerson(matrixStack, partialTicks, handSide, stack);
                    break;
            }

            this.mc.getFirstPersonRenderer().renderItemSide(player, stack, isHandSideRight ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !isHandSideRight, matrixStack, evt.getBuffers(), evt.getLight());
            matrixStack.pop();
        }
    }

    private void transformBowFirstPerson(MatrixStack matrixStackIn, float partialTicks, HandSide handside, ItemStack stack) {

        int sideSignum = handside == HandSide.RIGHT ? 1 : -1;
        matrixStackIn.translate(sideSignum * -0.2785682F, 0.18344387F, 0.15731531F);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-13.935F));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(sideSignum * 35.3F));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(sideSignum * -9.785F));
        float f8 = stack.getUseDuration() - ((this.mc.player != null ? this.mc.player.getItemInUseCount() : 0.0F) - partialTicks + 1.0F);
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
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees(sideSignum * 45.0F));
    }

    private void transformSpearFirstPerson(MatrixStack matrixStackIn, float partialTicks, HandSide handside, ItemStack stack) {

        int sideSignum = handside == HandSide.RIGHT ? 1 : -1;
        matrixStackIn.translate(sideSignum * -0.5F, 0.7F, 0.1F);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-55.0F));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(sideSignum * 35.3F));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(sideSignum * -9.785F));
        float f7 = stack.getUseDuration() - ((this.mc.player != null ? this.mc.player.getItemInUseCount() : 0.0F) - partialTicks + 1.0F);
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
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees(sideSignum * 45.0F));
    }

    private void transformCrossbowFirstPerson(MatrixStack matrixStackIn, float partialTicks, HandSide handside, ItemStack stack) {

        int sideSignum = handside == HandSide.RIGHT ? 1 : -1;
        matrixStackIn.translate(sideSignum * -0.4785682F, -0.094387F, 0.05731531F);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-11.935F));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(sideSignum * 65.3F));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(sideSignum * -9.785F));
        float f9 = stack.getUseDuration() - (this.mc.player.getItemInUseCount() - partialTicks + 1.0F);
        float f13 = f9 / CrossbowItem.getChargeTime(stack);
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
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees(sideSignum * 45.0F));
    }

    private void onRenderGameOverlay(final RenderGameOverlayEvent.Pre evt) {

        if (!this.noFlashingHearts || evt.getType() != RenderGameOverlayEvent.ElementType.HEALTH || !(this.mc.getRenderViewEntity() instanceof PlayerEntity)) {

            return;
        }

        evt.setCanceled(true);
        this.mc.getProfiler().startSection("health");
        RenderSystem.enableBlend();

        PlayerEntity playerentity = (PlayerEntity) this.mc.getRenderViewEntity();
        MatrixStack matrixStack = evt.getMatrixStack();
        boolean raiseHeart = playerentity.hurtResistantTime / 3 % 2 == 1;
        if (playerentity.hurtResistantTime <= 10) {

            // prevent single flash
            raiseHeart = false;
        }

        int playerHealth = MathHelper.ceil(playerentity.getHealth());
        float maxHealth = (float) playerentity.getAttributeValue(Attributes.MAX_HEALTH);
        int playerAbsorption = MathHelper.ceil(playerentity.getAbsorptionAmount());
        int healthRows = MathHelper.ceil((maxHealth + playerAbsorption) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        int ticks = this.mc.ingameGUI.getTicks();
        this.rand.setSeed(ticks * 312871L);
        int renderStartX = evt.getWindow().getScaledWidth() / 2 - 91;
        int renderStartY = evt.getWindow().getScaledHeight() - ForgeIngameGui.left_height;
        ForgeIngameGui.left_height += (healthRows * rowHeight);
        if (rowHeight != 10) {

            ForgeIngameGui.left_height += 10 - rowHeight;
        }

        int i3 = playerAbsorption;
        int isRegenerating = -1;
        if (playerentity.isPotionActive(Effects.REGENERATION)) {

            isRegenerating = ticks % MathHelper.ceil(maxHealth + 5.0F);
        }

        for (int l5 = MathHelper.ceil((maxHealth + playerAbsorption) / 2.0F) - 1; l5 >= 0; --l5) {

            int potionTextureMargin = 16;
            if (playerentity.isPotionActive(Effects.POISON)) {

                potionTextureMargin += 36;
            } else if (playerentity.isPotionActive(Effects.WITHER)) {

                potionTextureMargin += 72;
            }

            int heartYOffset = raiseHeart ? 1 : 0;
            int k4 = MathHelper.ceil((l5 + 1) / 10.0F) - 1;
            int posX = renderStartX + l5 % 10 * 8;
            int posY = renderStartY - k4 * rowHeight;
            if (playerHealth <= 4) {

                posY += this.rand.nextInt(2);
            }

            if (i3 <= 0 && l5 == isRegenerating) {

                posY -= 2;
            }

            int hardcoreTextureMargin = playerentity.world.getWorldInfo().isHardcore() ? 5 : 0;
            AbstractGui.blit(matrixStack, posX, posY, 16 + heartYOffset * 9, 9 * hardcoreTextureMargin, 9, 9, 256, 256);
            if (i3 > 0) {

                if (i3 == playerAbsorption && playerAbsorption % 2 == 1) {

                    AbstractGui.blit(matrixStack, posX, posY, potionTextureMargin + 153, 9 * hardcoreTextureMargin, 9, 9, 256, 256);
                    --i3;
                } else {

                    AbstractGui.blit(matrixStack, posX, posY, potionTextureMargin + 144, 9 * hardcoreTextureMargin, 9, 9, 256, 256);
                    i3 -= 2;
                }
            } else {

                if (l5 * 2 + 1 < playerHealth) {

                    AbstractGui.blit(matrixStack, posX, posY, potionTextureMargin + 36, 9 * hardcoreTextureMargin, 9, 9, 256, 256);
                }

                if (l5 * 2 + 1 == playerHealth) {

                    AbstractGui.blit(matrixStack, posX, posY, potionTextureMargin + 45, 9 * hardcoreTextureMargin, 9, 9, 256, 256);
                }
            }
        }

        RenderSystem.disableBlend();
        this.mc.getProfiler().endSection();
    }

}
