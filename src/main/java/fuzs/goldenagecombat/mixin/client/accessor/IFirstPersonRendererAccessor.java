package fuzs.goldenagecombat.mixin.client.accessor;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FirstPersonRenderer.class)
public interface IFirstPersonRendererAccessor {

    @Invoker
    void callTransformEatFirstPerson(MatrixStack matrixStackIn, float partialTicks, HandSide handIn, ItemStack stack);

    @Invoker
    void callTransformFirstPerson(MatrixStack matrixStackIn, HandSide handIn, float swingProgress);

    @Invoker
    void callTransformSideFirstPerson(MatrixStack matrixStackIn, HandSide handIn, float equippedProg);
    
}
