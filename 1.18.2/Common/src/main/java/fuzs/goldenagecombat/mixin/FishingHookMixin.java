package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
abstract class FishingHookMixin extends Entity {

    public FishingHookMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onHitEntity", at = @At("TAIL"))
    protected void onHitEntity(EntityHitResult hitResult, CallbackInfo callback) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).fishingRodKnockback) return;
        // won't really do anything for players as attacks with an amount of 0 are ignored, this is patched elsewhere
        hitResult.getEntity().hurt(DamageSource.thrown(this, this.getPlayerOwner()), 0.0F);
    }

    @Shadow
    public abstract Player getPlayerOwner();

    @Inject(method = "pullEntity", at = @At("HEAD"), cancellable = true)
    protected void pullEntity(Entity entity, CallbackInfo callback) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).fishingRodLaunch) return;
        Entity owner = Projectile.class.cast(this).getOwner();
        if (owner != null) {
            Vec3 vec3 = new Vec3(owner.getX() - this.getX(), owner.getY() - this.getY(), owner.getZ() - this.getZ()).scale(0.1);
            Vec3 deltaMovement = entity.getDeltaMovement();
            // values taken from Minecraft 1.8
            double x = deltaMovement.x() * 10.0, y = deltaMovement.y() * 10.0, z = deltaMovement.z() * 10.0;
            deltaMovement = deltaMovement.add(0.0, Math.pow(x * x + y * y + z * z, 0.25) * 0.08, 0.0);
            entity.setDeltaMovement(deltaMovement.add(vec3));
            callback.cancel();
        }
    }

    @Inject(method = "retrieve", at = @At("RETURN"), cancellable = true)
    public void retrieve(ItemStack stack, CallbackInfoReturnable<Integer> callback) {
        if (!GoldenAgeCombat.CONFIG.get(ServerConfig.class).fishingRodSlowerBreaking) return;
        if (callback.getReturnValueI() == 5) callback.setReturnValue(3);
    }
}
