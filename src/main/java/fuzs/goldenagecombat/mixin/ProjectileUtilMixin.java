package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {
    @ModifyVariable(method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;", at = @At("STORE"), ordinal = 1)
    private static AABB getEntityHitResult$aabb(AABB oldAABB) {
        if (GoldenAgeCombat.CONFIG.server().adjustments.minHitboxSize) {
            final double minSize = 0.9F;
            if (oldAABB.getXsize() < minSize || oldAABB.getYsize() < minSize || oldAABB.getZsize() < minSize) {
                return oldAABB.inflate(Math.max(0.0, minSize - oldAABB.getXsize()) / 2.0, Math.max(0.0, minSize - oldAABB.getYsize()) / 2.0, Math.max(0.0, minSize - oldAABB.getZsize()) / 2.0);
            }
        }
        return oldAABB;
    }
}
