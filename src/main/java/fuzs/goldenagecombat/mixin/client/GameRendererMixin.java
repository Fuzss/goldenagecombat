package fuzs.goldenagecombat.mixin.client;

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
    public void pick(float partialTicks, CallbackInfo callbackInfo) {
        Entity entity = this.minecraft.getCameraEntity();
        if (entity != null) {
            if (this.minecraft.level != null) {
                this.minecraft.getProfiler().push("pick");
                this.minecraft.crosshairPickEntity = null;
                double maxPickRange = 6.0;
                Vec3 viewVector = entity.getViewVector(1.0F);
                Vec3 eyePosition = entity.getEyePosition(partialTicks);
                Vec3 pickVector = eyePosition.add(viewVector.x * maxPickRange, viewVector.y * maxPickRange, viewVector.z * maxPickRange);

                AABB aabb = entity.getBoundingBox().expandTowards(viewVector.scale(maxPickRange)).inflate(1.0D, 1.0D, 1.0D);
                EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(entity, eyePosition, pickVector, aabb, entity1 -> {
                    return !entity1.isSpectator() && entity1.isPickable();
                }, maxPickRange * maxPickRange);
                double pickRange = this.minecraft.gameMode.getPickRange();

                double entityPickRange = getEntityPickRange(entityhitresult, eyePosition, pickVector);
                System.out.println("entity pick range " + entityPickRange + " normal pick range " + pickRange);
                if (entityhitresult != null && entityPickRange < pickRange) {
                    pickRange = entityPickRange;
                    System.out.println("entity");
                } else if (pickRange > maxPickRange) {
                    pickRange = maxPickRange;
                    System.out.println("block");
                }

                HitResult hitResult;
                if (entityhitresult != null) {
                    hitResult = pick(entity, pickRange, partialTicks);
                    System.out.println("entity hut result");
                } else {
                    hitResult = entity.pick(pickRange, partialTicks, false);
                    System.out.println("block hut result");
                }

                if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
                    this.minecraft.hitResult = hitResult;
                    System.out.println("picked block result 1");
                } else if (entityhitresult != null) {
                    this.minecraft.hitResult = entityhitresult;
                    this.minecraft.crosshairPickEntity = entityhitresult.getEntity();
                    System.out.println("picked entity result");
                } else {
                    this.minecraft.hitResult = hitResult;
                    System.out.println("picked block result 2");
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
