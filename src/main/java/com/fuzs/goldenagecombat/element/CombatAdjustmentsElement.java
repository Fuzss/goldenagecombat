package com.fuzs.goldenagecombat.element;

import com.fuzs.goldenagecombat.client.element.CombatAdjustmentsExtension;
import com.fuzs.puzzleslib_gc.config.ConfigManager;
import com.fuzs.puzzleslib_gc.config.serialization.EntryCollectionBuilder;
import com.fuzs.puzzleslib_gc.element.extension.ClientExtensibleElement;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

public class CombatAdjustmentsElement extends ClientExtensibleElement<CombatAdjustmentsExtension> {

    private boolean sweepingRequired;
    public boolean prioritizeShield;
    private Set<SoundEvent> removedAttackSounds;
    public boolean noDamageIndicators;

    public CombatAdjustmentsElement() {

        super(element -> new CombatAdjustmentsExtension((CombatAdjustmentsElement) element));
    }

    @Override
    public String[] getDescription() {

        return new String[]{"Small tweaks to make classic combat integrate more smoothly into modern Minecraft gameplay."};
    }

    @Override
    public void setupCommon() {

        this.addListener(this::onCriticalHit);
        this.addListener(this::onPlaySoundAtEntity);
    }

    @Override
    public void setupCommonConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Is the sweeping edge enchantment required to perform a sweep attack.").define("Require Sweeping Edge", true), v -> this.sweepingRequired = v);
        addToConfig(builder.comment("Prioritize shield blocking over sword blocking in case both items are held at the same time.").define("Prioritize Shield", true), v -> this.prioritizeShield = v);
        addToConfig(builder.comment("Prevent various attack sounds added for the cooldown mechanic from playing.", EntryCollectionBuilder.CONFIG_STRING).define("Removed Attack Sounds", ConfigManager.getKeyList(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK)), v -> this.removedAttackSounds = v, v -> deserializeToSet(v, ForgeRegistries.SOUND_EVENTS));
        addToConfig(builder.comment("Stop heart particles spawned when the player attacks an entity from appearing.").define("Hide Damage Indicators", false), v -> this.noDamageIndicators = v);
    }

    private void onCriticalHit(final CriticalHitEvent evt) {

        // prevent sweeping from taking effect unless the enchantment is in place, onGround flag is reset next tick anyways
        if (this.sweepingRequired && EnchantmentHelper.getSweepingDamageRatio(evt.getPlayer()) == 0.0F) {

            evt.getPlayer().onGround = false;
        }
    }

    private void onPlaySoundAtEntity(final PlaySoundAtEntityEvent evt) {

        // disable combat update player attack sounds
        if (this.removedAttackSounds.contains(evt.getSound())) {

            evt.setCanceled(true);
        }
    }

}
