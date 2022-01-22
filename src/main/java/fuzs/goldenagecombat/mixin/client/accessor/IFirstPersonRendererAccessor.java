package fuzs.goldenagecombat.mixin.client.accessor;

import com.mojang.blaze3d.matrix.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemInHandRenderer.class)
public interface IFirstPersonRendererAccessor {
    @Invoker
    void callTransformEatFirstPerson(PoseStack matrixStackIn, float partialTicks, HandSide handIn, ItemStack stack);

    @Invoker
    void callTransformFirstPerson(PoseStack matrixStackIn, HandSide handIn, float swingProgress);

    @Invoker
    void callTransformSideFirstPerson(PoseStack matrixStackIn, HandSide handIn, float equippedProg);
    
}
