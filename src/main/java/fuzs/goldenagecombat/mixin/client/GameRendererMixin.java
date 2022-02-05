package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "pick", at = @At("HEAD"), cancellable = true)
    public void pick$head(float partialTicks, CallbackInfo callbackInfo) {
        if (!GoldenAgeCombat.CONFIG.server().adjustments.swingThroughGrass) return;
        Entity entity = this.minecraft.getCameraEntity();
        if (entity != null) {
            if (this.minecraft.level != null) {
                this.minecraft.getProfiler().push("pick");
                this.minecraft.crosshairPickEntity = null;
                final double maxBlockPickRange = 6.0;
                final double maxEntityPickRange = 3.0;
                Vec3 viewVector = entity.getViewVector(1.0F);
                Vec3 eyePosition = entity.getEyePosition(partialTicks);
                Vec3 pickVector = eyePosition.add(viewVector.x * maxEntityPickRange, viewVector.y * maxEntityPickRange, viewVector.z * maxEntityPickRange);

                AABB aabb = entity.getBoundingBox().expandTowards(viewVector.scale(maxEntityPickRange)).inflate(1.0D, 1.0D, 1.0D);
                EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(entity, eyePosition, pickVector, aabb, entity1 -> {
                    return !entity1.isSpectator() && entity1.isPickable();
                }, maxEntityPickRange * maxEntityPickRange);
                double pickRange = this.minecraft.gameMode.getPickRange();

                double entityPickRange = getEntityPickRange(entityhitresult, eyePosition, pickVector);
                if (entityhitresult != null && entityPickRange < pickRange) {
                    pickRange = entityPickRange;
                } else if (pickRange > maxBlockPickRange) {
                    pickRange = maxBlockPickRange;
                }

                HitResult outlineHitResult = entity.pick(pickRange, partialTicks, false);
                // when trying to pick an entity, pick a second time with collider context so we are able to avoid e.g. tall grass
                // doing this the other way around does not work, as e.g. fences will be picked from their collision box when an entity is standing behind them
                if (entityhitresult != null && outlineHitResult.getType() != HitResult.Type.MISS) {
                    HitResult colliderHitResult = pick(entity, pickRange, partialTicks);
                    if (colliderHitResult.getType() == HitResult.Type.MISS) {
                        outlineHitResult = colliderHitResult;
                    }
                }

                if (outlineHitResult.getType() != HitResult.Type.MISS) {
                    this.minecraft.hitResult = outlineHitResult;
                } else if (entityhitresult != null) {
                    this.minecraft.hitResult = entityhitresult;
                    this.minecraft.crosshairPickEntity = entityhitresult.getEntity();
                } else {
                    this.minecraft.hitResult = outlineHitResult;
                }

                this.minecraft.getProfiler().pop();
            }
        }
        callbackInfo.cancel();
    }

    private static double getEntityPickRange(EntityHitResult entityhitresult, Vec3 eyePosition, Vec3 pickVector) {
        if (entityhitresult != null) {
            AABB aabb2 = entityhitresult.getEntity().getBoundingBox().inflate(entityhitresult.getEntity().getPickRadius());
            Optional<Vec3> optional = aabb2.clip(eyePosition, pickVector);
            if (optional.isPresent()) {
                return Math.sqrt(eyePosition.distanceToSqr(optional.get()));
            }
        }
        return 0.0;
    }

    @Unique
    private static HitResult pick(Entity entity, double pickRange, float partialTicks) {
        Vec3 vec3 = entity.getEyePosition(partialTicks);
        Vec3 vec31 = entity.getViewVector(partialTicks);
        Vec3 vec32 = vec3.add(vec31.x * pickRange, vec31.y * pickRange, vec31.z * pickRange);
        return entity.level.clip(new ClipContext(vec3, vec32, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
    }
}
