package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.client.element.LegacyAnimationsRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(BipedArmorLayer.class)
public abstract class BipedArmorLayerMixin<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends LayerRenderer<T, M> {

    public BipedArmorLayerMixin(IEntityRenderer<T, M> entityRendererIn) {

        super(entityRendererIn);
    }

    // for default forge environment
    @Redirect(method = "func_241739_a_", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/BipedArmorLayer;func_241738_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IZLnet/minecraft/client/renderer/entity/model/BipedModel;FFFLnet/minecraft/util/ResourceLocation;)V", remap = false), require = 0)
    private void renderModel1(BipedArmorLayer<T, M, A> armorLayer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean glintIn, A modelIn, float red, float green, float blue, ResourceLocation armorResource, MatrixStack matrixStackIn2, IRenderTypeBuffer bufferIn2, T entityIn) {

        this.renderArmor(armorLayer, matrixStackIn, bufferIn, packedLightIn, glintIn, modelIn, red, green, blue, armorResource, matrixStackIn2, bufferIn2, entityIn);
    }

    // optifine patches this class as well, this patch will be used then
    @Redirect(method = "func_241739_a_", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/BipedArmorLayer;renderModel(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IZLnet/minecraft/client/renderer/entity/model/BipedModel;FFFLnet/minecraft/util/ResourceLocation;)V", remap = false), require = 0)
    private void renderModel2(BipedArmorLayer<T, M, A> armorLayer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean glintIn, A modelIn, float red, float green, float blue, ResourceLocation armorResource, MatrixStack matrixStackIn2, IRenderTypeBuffer bufferIn2, T entityIn) {

        this.renderArmor(armorLayer, matrixStackIn, bufferIn, packedLightIn, glintIn, modelIn, red, green, blue, armorResource, matrixStackIn2, bufferIn2, entityIn);
    }

    private void renderArmor(BipedArmorLayer<T, M, A> armorLayer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean glintIn, A modelIn, float red, float green, float blue, ResourceLocation armorResource, MatrixStack matrixStackIn2, IRenderTypeBuffer bufferIn2, T entityIn) {

        IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(bufferIn, RenderType.getEntityCutoutNoCull(armorResource), false, glintIn);
        LegacyAnimationsRenderer element = (LegacyAnimationsRenderer) GoldenAgeCombat.LEGACY_ANIMATIONS;
        modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, element.isEnabled() && element.damageOnArmor ? LivingRenderer.getPackedOverlay(entityIn, 0.0F) : OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
    }

}
