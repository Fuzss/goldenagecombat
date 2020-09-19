package com.fuzs.goldenagecombat.client.renderer.entity.layers;

import com.fuzs.goldenagecombat.client.config.ClientConfigHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RedArmorLayer {

    public static <T extends LivingEntity, M extends BipedModel<T>> void renderArmor(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean glintIn, M modelIn, float red, float green, float blue, ResourceLocation armorResource, T entity) {

        IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(bufferIn, RenderType.getEntityCutoutNoCull(armorResource), false, glintIn);
        modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, ClientConfigHandler.ANIMATIONS.get() && ClientConfigHandler.ANIMATIONS_ARMOR.get() ? LivingRenderer.getPackedOverlay(entity, 0.0F) : OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
    }

}
