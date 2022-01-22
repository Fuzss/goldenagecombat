package fuzs.goldenagecombat.mixin;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.handler.ClassicCombatHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * can't access constructor of {@link net.minecraft.entity.projectile.ProjectileEntity} as it's package-private,
 * so we extend the next class in the class hierarchy
 */
@SuppressWarnings("unused")
@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends Entity {

    public FishingBobberEntityMixin(EntityType<?> entityTypeIn, World worldIn) {

        super(entityTypeIn, worldIn);
    }

    @Inject(method = "onEntityHit", at = @At("TAIL"))
    protected void onEntityHit(EntityRayTraceResult raytraceresult, CallbackInfo callbackInfo) {

        ClassicCombatHandler element = (ClassicCombatHandler) GoldenAgeCombat.CLASSIC_COMBAT;
        if (element.isEnabled() && element.fishingRodKnockback) {

            // won't really do anything as attacks with an amount of 0 are ignored, this is patched elsewhere
            raytraceresult.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234606_i_()), 0.0F);
        }
    }

    // getAngler
    @Shadow
    public abstract PlayerEntity func_234606_i_();

    @Redirect(method = "bringInHookedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getMotion()Lnet/minecraft/util/math/vector/Vector3d;"))
    public Vector3d getMotion(Entity entity) {

        Vector3d motion = entity.getMotion();
        ClassicCombatHandler element = (ClassicCombatHandler) GoldenAgeCombat.CLASSIC_COMBAT;
        if (element.isEnabled() && element.fishingRodLaunch) {

            // values taken from Minecraft 1.8
            double x = motion.getX() * 10.0, y = motion.getY() * 10.0, z = motion.getZ() * 10.0;
            return motion.add(0.0, Math.pow(x * x + y * y + z * z, 0.25) * 0.08, 0.0);
        }
        
        return motion;
    }

}
