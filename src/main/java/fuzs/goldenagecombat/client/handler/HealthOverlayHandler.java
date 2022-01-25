package fuzs.goldenagecombat.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class HealthOverlayHandler {

    private final Minecraft minecraft = Minecraft.getInstance();
    private final Random random = new Random();

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
