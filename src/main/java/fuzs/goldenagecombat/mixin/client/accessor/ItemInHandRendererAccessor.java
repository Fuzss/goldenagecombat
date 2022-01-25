package fuzs.goldenagecombat.mixin.client.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemInHandRenderer.class)
public interface ItemInHandRendererAccessor {
    @Invoker
    void callApplyEatTransform(PoseStack matrixStackIn, float partialTicks, HumanoidArm handIn, ItemStack stack);

    @Invoker
    void callApplyItemArmAttackTransform(PoseStack matrixStackIn, HumanoidArm handIn, float swingProgress);

    @Invoker
    void callApplyItemArmTransform(PoseStack matrixStackIn, HumanoidArm handIn, float equippedProg);
}
