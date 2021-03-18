package com.fuzs.goldenagecombat.mixin.client;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.client.element.LegacyAnimationsElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.ArmorLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(ArmorLayer.class)
public abstract class ArmorLayerMixin<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends LayerRenderer<T, M> {

    public ArmorLayerMixin(IEntityRenderer<T, M> entityRendererIn) {

        super(entityRendererIn);
    }

    // make this optional, not sure what optifine's up to in 1.15
    @Redirect(method = "renderArmorPart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/ArmorLayer;renderArmor(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IZLnet/minecraft/client/renderer/entity/model/BipedModel;FFFLnet/minecraft/util/ResourceLocation;)V", remap = false), require = 0)
    private void renderArmor(ArmorLayer<T, M, A> armorLayer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean glintIn, A modelIn, float red, float green, float blue, ResourceLocation armorResource, MatrixStack matrixStackIn2, IRenderTypeBuffer bufferIn2, T entityLivingBaseIn) {

        IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(bufferIn, RenderType.getEntityCutoutNoCull(armorResource), false, glintIn);
        LegacyAnimationsElement element = (LegacyAnimationsElement) GoldenAgeCombat.LEGACY_ANIMATIONS;
        modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, element.isEnabled() && element.damageOnArmor ? LivingRenderer.getPackedOverlay(entityLivingBaseIn, 0.0F) : OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
    }

}
