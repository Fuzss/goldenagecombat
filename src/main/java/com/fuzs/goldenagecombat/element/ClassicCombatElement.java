package com.fuzs.goldenagecombat.element;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.client.element.ClassicCombatExtension;
import com.fuzs.goldenagecombat.mixin.accessor.IItemAccessor;
import com.fuzs.goldenagecombat.mixin.accessor.ILivingEntityAccessor;
import com.fuzs.puzzleslib_gc.config.ConfigManager;
import com.fuzs.puzzleslib_gc.element.extension.ClientExtensibleElement;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

public class ClassicCombatElement extends ClientExtensibleElement<ClassicCombatExtension> {

    public static final Tags.IOptionalNamedTag<Item> ATTACK_DAMAGE_BLACKLIST_TAG = ItemTags.createOptional(new ResourceLocation(GoldenAgeCombat.MODID, "attack_damage_blacklist"));

    private boolean oldAttackDamage;
    public boolean removeCooldown;
    private boolean sweepingRequired;
    private Set<SoundEvent> removedAttackSounds;
    private boolean goldenAppleEffects;

    public ClassicCombatElement() {

        super(element -> new ClassicCombatExtension((ClassicCombatElement) element));
    }

    @Override
    public String getDescription() {

        return "Restores basic pre-Combat Update combat mechanics.";
    }

    @Override
    public void setupCommon() {

        this.addListener(this::onItemAttributeModifier);
        this.addListener(this::onAttackEntity);
        this.addListener(this::onCriticalHit);
        this.addListener(this::onPlaySoundAtEntity);
        this.addListener(this::onUseItemFinish);
    }

    @Override
    public void setupCommonConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Revert weapon and tool attack damage to legacy values.").define("Legacy Attack Damage", true), v -> this.oldAttackDamage = v);
        addToConfig(builder.comment("Completely remove the attack cooldown as if it never even existed in the first place.").define("Remove Attack Cooldown", true), v -> this.removeCooldown = v);
        addToConfig(builder.comment("Is the sweeping edge enchantment required to perform a sweep attack.").define("Require Sweeping Edge", true), v -> this.sweepingRequired = v);
        addToConfig(builder.comment("Don't play various attack sounds added in more recent versions. Set individual sounds to \"true\" to disable.").define("Removed Attack Sounds", ConfigManager.getKeyList(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK)), v -> this.removedAttackSounds = v, v -> deserializeToSet(v, ForgeRegistries.SOUND_EVENTS));
        addToConfig(builder.comment("Give Regeneration V and Absorption I instead of Regeneration II and Absorption IV after consuming a notch apple.").define("Notch Apple Effects", true), v -> this.goldenAppleEffects = v);
        addToConfig(builder.comment("A notch apple can be crafted from a single apple and eight gold blocks.").define("Notch Apple Recipe", true), v -> {});
    }

    @SuppressWarnings("ConstantConditions")
    private void onItemAttributeModifier(final ItemAttributeModifierEvent evt) {

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
    }

    private void replaceDamageAttribute(ItemAttributeModifierEvent evt, TieredItem tieredItem, float damageBonus) {

        evt.removeAttribute(Attributes.ATTACK_DAMAGE);
        evt.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(IItemAccessor.getAttackDamageModifier(), GoldenAgeCombat.MODID + " modifier", tieredItem.getTier().getAttackDamage() + damageBonus, AttributeModifier.Operation.ADDITION));
    }

    private void onAttackEntity(final AttackEntityEvent evt) {

        // reset cooldown right before every attack
        if (this.removeCooldown) {

            disableCooldownPeriod(evt.getPlayer());
        }
    }

    private void onCriticalHit(final CriticalHitEvent evt) {

        // prevent sweeping from taking effect unless the enchantment is in place, onGround flag is reset next tick anyways
        if (this.sweepingRequired && EnchantmentHelper.getSweepingDamageRatio(evt.getPlayer()) == 0.0F) {

            evt.getPlayer().setOnGround(false);
        }
    }

    private void onPlaySoundAtEntity(final PlaySoundAtEntityEvent evt) {

        // disable combat update player attack sounds
        if (this.removedAttackSounds.contains(evt.getSound())) {

            evt.setCanceled(true);
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
