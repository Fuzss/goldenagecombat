package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.handler.ToolMaterials;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToolMaterial.class)
abstract class ToolMaterialMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(TagKey<Block> incorrectBlocksForDrops, int durability, float speed, float attackDamageBonus, int enchantmentValue, TagKey<Item> repairItems, CallbackInfo callback) {
        ToolMaterials.registerToolMaterial(ToolMaterial.class.cast(this));
    }
}
