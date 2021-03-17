package com.fuzs.goldenagecombat.client.gui;

import com.fuzs.goldenagecombat.client.config.ClientConfigHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class HealthIngameGui {

    private final Minecraft mc = Minecraft.getInstance();
    private final Random rand = new Random();

    public void onRenderGameOverlay(final RenderGameOverlayEvent.Pre evt) {

        if (!ClientConfigHandler.ANIMATIONS_NO_FLASHING_HEARTS.get() || evt.getType() != RenderGameOverlayEvent.ElementType.HEALTH || !(this.mc.getRenderViewEntity() instanceof PlayerEntity)) {

            return;
        }

        evt.setCanceled(true);
        PlayerEntity playerentity = (PlayerEntity) this.mc.getRenderViewEntity();
        MatrixStack matrixStack = evt.getMatrixStack();
        boolean flag = playerentity.hurtResistantTime / 3 % 2 == 1;
        if (playerentity.hurtResistantTime <= 10) {

            // prevent flashing
            flag = false;
        }

        int i = MathHelper.ceil(playerentity.getHealth());
        int ticks = this.mc.ingameGUI.getTicks();
        this.rand.setSeed(ticks * 312871L);
        int i1 = evt.getWindow().getScaledWidth() / 2 - 91;
        int k1 = evt.getWindow().getScaledHeight() - 39;
        float f = (float) playerentity.getAttributeValue(Attributes.MAX_HEALTH);
        int l1 = MathHelper.ceil(playerentity.getAbsorptionAmount());
        int i2 = MathHelper.ceil((f + (float)l1) / 2.0F / 10.0F);
        int j2 = Math.max(10 - (i2 - 2), 3);
        int i3 = l1;
        int k3 = -1;
        if (playerentity.isPotionActive(Effects.REGENERATION)) {

            k3 = ticks % MathHelper.ceil(f + 5.0F);
        }

        this.mc.getProfiler().startSection("health");
        for (int l5 = MathHelper.ceil((f + (float)l1) / 2.0F) - 1; l5 >= 0; --l5) {

            int i6 = 16;
            if (playerentity.isPotionActive(Effects.POISON)) {

                i6 += 36;
            } else if (playerentity.isPotionActive(Effects.WITHER)) {

                i6 += 72;
            }

            int j4 = 0;
            if (flag) {

                j4 = 1;
            }

            int k4 = MathHelper.ceil((float)(l5 + 1) / 10.0F) - 1;
            int l4 = i1 + l5 % 10 * 8;
            int i5 = k1 - k4 * j2;
            if (i <= 4) {

                i5 += this.rand.nextInt(2);
            }

            if (i3 <= 0 && l5 == k3) {

                i5 -= 2;
            }

            int j5 = 0;
            if (playerentity.world.getWorldInfo().isHardcore()) {

                j5 = 5;
            }

            AbstractGui.blit(matrixStack, l4, i5, 16 + j4 * 9, 9 * j5, 9, 9, 256, 256);
            if (i3 > 0) {

                if (i3 == l1 && l1 % 2 == 1) {

                    AbstractGui.blit(matrixStack, l4, i5, i6 + 153, 9 * j5, 9, 9, 256, 256);
                    --i3;
                } else {

                    AbstractGui.blit(matrixStack, l4, i5, i6 + 144, 9 * j5, 9, 9, 256, 256);
                    i3 -= 2;
                }
            } else {

                if (l5 * 2 + 1 < i) {

                    AbstractGui.blit(matrixStack, l4, i5, i6 + 36, 9 * j5, 9, 9, 256, 256);
                }

                if (l5 * 2 + 1 == i) {

                    AbstractGui.blit(matrixStack, l4, i5, i6 + 45, 9 * j5, 9, 9, 256, 256);
                }
            }
        }

        this.mc.getProfiler().endSection();
    }

}
