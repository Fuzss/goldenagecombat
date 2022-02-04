package fuzs.goldenagecombat.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.mixin.accessor.ItemAccessor;
import fuzs.goldenagecombat.mixin.accessor.LivingEntityAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ClassicCombatHandler {
    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent evt) {
        // reset cooldown right before every attack
        if (GoldenAgeCombat.CONFIG.server().classic.removeCooldown) {
            disableCooldownPeriod(evt.getPlayer());
        }
    }

    @SubscribeEvent
    public void onItemAttributeModifier(final ItemAttributeModifierEvent evt) {
        if (!GoldenAgeCombat.CONFIG.server().classic.oldAttackDamage) return;
        if (evt.getSlotType() != EquipmentSlot.MAINHAND) return;
        ItemStack stack = evt.getItemStack();
        // don't change items whose attributes have already been changed via the nbt tag
        if (!stack.hasTag() || !stack.getTag().contains("AttributeModifiers", 9)) {
            if (GoldenAgeCombat.CONFIG.server().classic.attackDamageOverrides.containsKey(stack.getItem())) {
                this.replaceDamageAttribute(evt::removeAttribute, evt::addModifier, GoldenAgeCombat.CONFIG.server().classic.attackDamageOverrides.get(stack.getItem()));
            } else if (stack.getItem() instanceof TieredItem) {
                // always one less to account for base value of 1.0
                if (stack.getItem() instanceof SwordItem) {
                    this.replaceDamageAttribute(evt::removeAttribute, evt::addModifier, (TieredItem) stack.getItem(), 4.0);
                } else if (stack.getItem() instanceof AxeItem) {
                    this.replaceDamageAttribute(evt::removeAttribute, evt::addModifier, (TieredItem) stack.getItem(), 3.0);
                } else if (stack.getItem() instanceof PickaxeItem) {
                    this.replaceDamageAttribute(evt::removeAttribute, evt::addModifier, (TieredItem) stack.getItem(), 2.0);
                } else if (stack.getItem() instanceof ShovelItem) {
                    this.replaceDamageAttribute(evt::removeAttribute, evt::addModifier, (TieredItem) stack.getItem(), 1.0);
                } else if (stack.getItem() instanceof HoeItem) {
                    this.replaceDamageAttribute(evt::removeAttribute, evt::addModifier, (TieredItem) stack.getItem(), 0.0);
                }
            }
        }
    }

    private void replaceDamageAttribute(Consumer<Attribute> removeAttribute, BiConsumer<Attribute, AttributeModifier> addModifier, TieredItem item, double damageBonus) {
        this.replaceDamageAttribute(removeAttribute, addModifier, item.getTier().getAttackDamageBonus() + damageBonus);
    }

    private void replaceDamageAttribute(Consumer<Attribute> removeAttribute, BiConsumer<Attribute, AttributeModifier> addModifier, double newValue) {
        removeAttribute.accept(Attributes.ATTACK_DAMAGE);
        addModifier.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(ItemAccessor.getBaseAttackDamageUUID(), new ResourceLocation(GoldenAgeCombat.MOD_ID, "attack_damage_modifier").toString(), newValue, AttributeModifier.Operation.ADDITION));
    }

    @SubscribeEvent
    public void onThrowableImpact(final ProjectileImpactEvent evt) {
        if (!GoldenAgeCombat.CONFIG.server().classic.weakPlayerKnockback) return;
        final Projectile projectile = evt.getProjectile();
        if (evt.getRayTraceResult().getType() == HitResult.Type.ENTITY && projectile.getOwner() == null) {
            // enable knockback for item projectiles fired from dispensers by making shooter not be null
            // something similar is already done in AbstractArrowEntity::onEntityHit to account for arrows fired from dispensers
            projectile.setOwner(projectile);
        }
    }

    @SubscribeEvent
    public void onUseItemFinish(final LivingEntityUseItemEvent.Finish evt) {
        if (!GoldenAgeCombat.CONFIG.server().classic.goldenAppleEffects) return;
        if (evt.getItem().getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
            final LivingEntity entity = evt.getEntityLiving();
            entity.removeEffect(MobEffects.ABSORPTION);
            entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 4));
        }
    }

    @SubscribeEvent
    public void onLivingKnockBack(final LivingKnockBackEvent evt) {
        final LivingEntity entity = evt.getEntityLiving();
        if (GoldenAgeCombat.CONFIG.server().classic.upwardsKnockback && !entity.isOnGround() && !entity.isInWater()) {
            float strength = evt.getOriginalStrength();
            strength *= 1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            final Vec3 deltaMovement = entity.getDeltaMovement();
            entity.setDeltaMovement(deltaMovement.x, Math.min(0.4, deltaMovement.y / 2.0D + strength), deltaMovement.x);
        }
    }

    public static void disableCooldownPeriod(Player player) {
        ((LivingEntityAccessor) player).setAttackStrengthTicker((int) Math.ceil(player.getCurrentItemAttackStrengthDelay()));
    }
}
