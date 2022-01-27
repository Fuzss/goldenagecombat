package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin extends Projectile {
    public FishingHookMixin(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @Inject(method = "onHitEntity", at = @At("TAIL"))
    protected void onHitEntity$tail(EntityHitResult raytraceresult, CallbackInfo callbackInfo) {
        if (!GoldenAgeCombat.CONFIG.server().classic.fishingRodKnockback) return;
        // won't really do anything for players as attacks with an amount of 0 are ignored, this is patched elsewhere
        raytraceresult.getEntity().hurt(DamageSource.thrown(this, this.getPlayerOwner()), 0.0F);
    }

    @Shadow
    public abstract Player getPlayerOwner();

    @Redirect(method = "pullEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 pullEntity$getDeltaMovement(Entity entity) {
        Vec3 motion = entity.getDeltaMovement();
        if (GoldenAgeCombat.CONFIG.server().classic.fishingRodLaunch) {
            // values taken from Minecraft 1.8
            double x = motion.x() * 10.0, y = motion.y() * 10.0, z = motion.z() * 10.0;
            return motion.add(0.0, Math.pow(x * x + y * y + z * z, 0.25) * 0.08, 0.0);
        }
        return motion;
    }

    @Inject(method = "retrieve", at = @At("RETURN"), cancellable = true)
    public void retrieve$return(ItemStack stack, CallbackInfoReturnable<Integer> callbackInfo) {
        if (!GoldenAgeCombat.CONFIG.server().classic.fishingRodSlowerBreaking) return;
        if (callbackInfo.getReturnValueI() == 5) {
            callbackInfo.setReturnValue(3);
        }
    }
}
