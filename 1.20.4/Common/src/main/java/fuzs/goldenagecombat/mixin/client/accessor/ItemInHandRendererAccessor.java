package fuzs.goldenagecombat.mixin.client.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemInHandRenderer.class)
public interface ItemInHandRendererAccessor {

    @Invoker("applyEatTransform")
    void goldenagecombat$callApplyEatTransform(PoseStack matrixStackIn, float partialTicks, HumanoidArm handIn, ItemStack stack);

    @Invoker("applyItemArmAttackTransform")
    void goldenagecombat$callApplyItemArmAttackTransform(PoseStack matrixStackIn, HumanoidArm handIn, float swingProgress);

    @Invoker("applyItemArmTransform")
    void goldenagecombat$callApplyItemArmTransform(PoseStack matrixStackIn, HumanoidArm handIn, float equippedProg);
}
