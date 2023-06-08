package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.client.helper.GameRendererPickHelper;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "pick", at = @At("HEAD"), cancellable = true)
    public void pick(float partialTicks, CallbackInfo callback) {
        if (!GoldenAgeCombat.CONFIG.getHolder(ServerConfig.class).isAvailable() || !GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.swingThroughGrass && !GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackRange) return;
        Entity entity = this.minecraft.getCameraEntity();
        if (entity != null && this.minecraft.level != null) {

            this.minecraft.getProfiler().push("pick");
            this.minecraft.crosshairPickEntity = null;

            double maxPickRange = this.minecraft.gameMode.getPickRange();
            double maxEntityPickRange = entity == this.minecraft.player && GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackRange ? GameRendererPickHelper.getCurrentAttackReach(this.minecraft.gameMode, this.minecraft.player) : 3.0;
            Vec3 viewVector = entity.getViewVector(1.0F);
            Vec3 eyePosition = entity.getEyePosition(partialTicks);
            Vec3 pickVector = eyePosition.add(viewVector.x * maxEntityPickRange, viewVector.y * maxEntityPickRange, viewVector.z * maxEntityPickRange);
            AABB aabb = entity.getBoundingBox().expandTowards(viewVector.scale(maxEntityPickRange)).inflate(1.0D, 1.0D, 1.0D);
            EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(entity, eyePosition, pickVector, aabb, entity1 -> {
                return !entity1.isSpectator() && entity1.isPickable();
            }, maxEntityPickRange * maxEntityPickRange);

            double entityPickRange = GameRendererPickHelper.getEntityPickRange(entityHitResult, eyePosition, pickVector);
            if (entityHitResult != null && entityPickRange < maxPickRange) {
                maxPickRange = entityPickRange;
            }
            if (maxPickRange > 6.0) {
                maxPickRange = 6.0;
            }

            HitResult hitResult = entity.pick(maxPickRange, partialTicks, false);
            // when trying to pick an entity, pick a second time with collider context, so we are able to avoid e.g. tall grass
            // doing this the other way around does not work, as e.g. fences will be picked from their collision box when an entity is standing behind them
            if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).combatTests.swingThroughGrass && entityHitResult != null && hitResult.getType() != HitResult.Type.MISS) {
                HitResult colliderHitResult = GameRendererPickHelper.pick(entity, maxPickRange, partialTicks);
                if (colliderHitResult.getType() == HitResult.Type.MISS) {
                    hitResult = colliderHitResult;
                }
            }

            if (hitResult.getType() != HitResult.Type.MISS) {
                this.minecraft.hitResult = hitResult;
            } else if (entityHitResult != null) {
                this.minecraft.hitResult = entityHitResult;
                this.minecraft.crosshairPickEntity = entityHitResult.getEntity();
            } else {
                this.minecraft.hitResult = hitResult;
            }

            this.minecraft.getProfiler().pop();
        }

        callback.cancel();
    }
}
