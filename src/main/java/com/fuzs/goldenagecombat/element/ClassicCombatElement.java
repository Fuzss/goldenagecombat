package com.fuzs.goldenagecombat.element;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.client.element.ClassicCombatExtension;
import com.fuzs.goldenagecombat.mixin.accessor.IItemAccessor;
import com.fuzs.goldenagecombat.mixin.accessor.ILivingEntityAccessor;
import com.fuzs.puzzleslib_gc.element.extension.ClientExtensibleElement;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class ClassicCombatElement extends ClientExtensibleElement<ClassicCombatExtension> {

    public static final Tags.IOptionalNamedTag<Item> ATTACK_DAMAGE_BLACKLIST_TAG = ItemTags.createOptional(new ResourceLocation(GoldenAgeCombat.MODID, "attack_damage_blacklist"));

    public boolean removeCooldown;
    private boolean oldAttackDamage;
    public boolean noFastRegen;
    public boolean weakPlayerKnockback;
    public boolean criticalSprinting;
    public boolean rodKnockback;
    public boolean rodLaunch;
    public boolean boostSharpness;
    private boolean goldenAppleEffects;
    public boolean backwardsWalking;

    public ClassicCombatElement() {

        super(element -> new ClassicCombatExtension((ClassicCombatElement) element));
    }

    @Override
    public String[] getDescription() {

        return new String[]{"Restores basic pre-Combat Update combat mechanics, achieved mainly by removing the cooldown mechanic."};
    }

    @Override
    public void setupCommon() {

        this.addListener(this::onAttackEntity);
        this.addListener(this::onItemAttributeModifier);
        this.addListener(this::onThrowableImpact);
        this.addListener(this::onUseItemFinish);
    }

    @Override
    public void setupCommonConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Completely remove the attack cooldown as if it never even existed in the first place.").define("Remove Attack Cooldown", true), v -> this.removeCooldown = v);
        addToConfig(builder.comment("Revert weapon and tool attack damage to legacy values.", "Items which are not handled as expected can be blacklisted using the \"" + GoldenAgeCombat.MODID + ":attack_damage_blacklist\" item tag.").define("Legacy Attack Damage", true), v -> this.oldAttackDamage = v);
        addToConfig(builder.comment("Surplus saturation is no longer used for quick health regeneration, resulting in health only being regenerated from food every 4 seconds.").define("Disable Health Regen Boost", true), v -> this.noFastRegen = v);
        addToConfig(builder.comment("Player is knocked back by attacks which do not cause any damage, such as when hit by a throwable item.").define("Weak Attacks Knock Back Player", true), v -> this.weakPlayerKnockback = v);
        addToConfig(builder.comment("Sprinting and attacking no longer interfere with each other, making critical hits possible at all times.").define("Critical Sprint Hits", true), v -> this.criticalSprinting = v);
        addToConfig(builder.comment("Fishing rod deals knockback upon hitting an entity.").define("Rod Causes Knockback", true), v -> this.rodKnockback = v);
        addToConfig(builder.comment("Entities reeled in using a fishing rod are slightly launched upwards.").define("Rod Launches Entities", true), v -> this.rodLaunch = v);
        addToConfig(builder.comment("Boost sharpness enchantment to 1.25 damage points per level instead of just 0.5.").define("Boost Sharpness", false), v -> this.boostSharpness = v);
        addToConfig(builder.comment("Give Regeneration V and Absorption I instead of Regeneration II and Absorption IV after consuming a notch apple.").define("Notch Apple Effects", true), v -> this.goldenAppleEffects = v);
        addToConfig(builder.comment("A notch apple can be crafted from a single apple and eight gold blocks.").define("Notch Apple Recipe", true), v -> {});
        addToConfig(builder.comment("The player's body turns sideways when walking backwards instead of remaining straight.").define("Backwards Walking", true), v -> this.backwardsWalking = v);
    }

    private void onAttackEntity(final AttackEntityEvent evt) {

        // reset cooldown right before every attack
        if (this.removeCooldown) {

            disableCooldownPeriod(evt.getPlayer());
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void onItemAttributeModifier(final ItemAttributeModifierEvent evt) {

        try {

            ItemStack stack = evt.getItemStack();
            if (this.oldAttackDamage && !stack.getItem().isIn(ATTACK_DAMAGE_BLACKLIST_TAG)) {

                if (stack.getItem() instanceof TieredItem && evt.getSlotType() == EquipmentSlotType.MAINHAND) {

                    // don't change items whose attributes have already been changed via the nbt tag
                    if (!stack.hasTag() || !stack.getTag().contains("AttributeModifiers", 9)) {

                        // always one less to account for base value of 1.0
                        if (stack.getItem() instanceof SwordItem) {

                            this.replaceDamageAttribute(evt, (TieredItem) stack.getItem(), 4.0F);
                        } else if (stack.getItem() instanceof AxeItem) {

                            this.replaceDamageAttribute(evt, (TieredItem) stack.getItem(), 3.0F);
                        } else if (stack.getItem() instanceof PickaxeItem) {

                            this.replaceDamageAttribute(evt, (TieredItem) stack.getItem(), 2.0F);
                        } else if (stack.getItem() instanceof ShovelItem) {

                            this.replaceDamageAttribute(evt, (TieredItem) stack.getItem(), 1.0F);
                        } else if (stack.getItem() instanceof HoeItem) {

                            this.replaceDamageAttribute(evt, (TieredItem) stack.getItem(), 0.0F);
                        }
                    }
                }
            }
        } catch (IllegalStateException ignored) {

            // item tag will crash with some mods due to not having been fetched yet, no way to check that really
        }
    }

    private void replaceDamageAttribute(ItemAttributeModifierEvent evt, TieredItem tieredItem, float damageBonus) {

        evt.removeAttribute(Attributes.ATTACK_DAMAGE);
        evt.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(IItemAccessor.getAttackDamageModifier(), GoldenAgeCombat.MODID + " modifier", tieredItem.getTier().getAttackDamage() + damageBonus, AttributeModifier.Operation.ADDITION));
    }

    private void onThrowableImpact(final ProjectileImpactEvent.Throwable evt) {

        if (this.weakPlayerKnockback && evt.getEntity() instanceof ProjectileItemEntity) {

            ProjectileItemEntity projectile = (ProjectileItemEntity) evt.getEntity();
            // getThrower
            if (evt.getRayTraceResult().getType() == RayTraceResult.Type.ENTITY && projectile.func_234616_v_() == null) {

                // enable knockback for item projectiles fired from dispensers by making shooter not be null
                // something similar is already done in AbstractArrowEntity::onEntityHit to account for arrows fired from dispensers
                projectile.setShooter(projectile);
            }
        }
    }

    private void onUseItemFinish(final LivingEntityUseItemEvent.Finish evt) {

        if (this.goldenAppleEffects && evt.getItem().getItem() == Items.ENCHANTED_GOLDEN_APPLE) {

            evt.getEntityLiving().removePotionEffect(Effects.ABSORPTION);
            evt.getEntityLiving().addPotionEffect(new EffectInstance(Effects.ABSORPTION, 2400, 0));
            evt.getEntityLiving().addPotionEffect(new EffectInstance(Effects.REGENERATION, 600, 4));
        }
    }

    public static void disableCooldownPeriod(PlayerEntity player) {

        ((ILivingEntityAccessor) player).setTicksSinceLastSwing((int) Math.ceil(player.getCooldownPeriod()));
    }

}
