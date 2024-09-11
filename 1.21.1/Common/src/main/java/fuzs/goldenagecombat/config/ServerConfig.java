package fuzs.goldenagecombat.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.List;

public class ServerConfig implements ConfigCore {
    static final String FISHING_RODS_CATEGORY = "fishing_rods";
    
    @Config(description = "Completely remove the attack cooldown as if it never even existed in the first place.")
    public boolean removeAttackCooldown = true;
    @Config(description = "Health only regenerates every 4 seconds, while requiring 18 or more food points. Surplus saturation does not yield quick health regeneration.")
    public boolean legacyFoodMechanics = false;
    @Config(description = "Player is knocked back by attacks which do not cause any damage, such as when hit by snowballs, eggs, and fishing rod hooks.")
    public boolean weakAttacksKnockBackPlayers = true;
    @Config(description = "Sprinting and attacking no longer interfere with each other, making critical hits possible at all times.")
    public boolean criticalHitsWhileSprinting = true;
    @Config(category = FISHING_RODS_CATEGORY, name = "cause_knockback", description = "Fishing rod deals knockback upon hitting an entity.")
    public boolean fishingRodKnockback = true;
    @Config(category = FISHING_RODS_CATEGORY, name = "launch_entities", description = "Entities reeled in using a fishing rod are slightly launched upwards.")
    public boolean fishingRodLaunch = true;
    @Config(category = FISHING_RODS_CATEGORY, name = "slower_breaking", description = "Hooking entities with a fishing rod causes only 3 damage points to the rod instead of 5.")
    public boolean fishingRodSlowerBreaking = true;
    @Config(name = "notch_apple_effects", description = "Give Regeneration V and Absorption I instead of Regeneration II and Absorption IV after consuming a notch apple.")
    public boolean goldenAppleEffects = true;
    @Config(description = "Expand all entity hitboxes by 10%, making hitting a target possible from a slightly greater range and with much increased accuracy.")
    public boolean inflateHitboxes = true;
    @Config(description = "Allow using the \"Attack\" button while the \"Use Item\" button is held for mining blocks. Does not make a lot of sense, but it used to be a feature in old pvp.")
    public boolean interactWhileUsing = false;
    @Config(description = "Makes knockback stronger towards targets not on the ground (does not apply when in water).")
    public boolean upwardsKnockback = true;
    @Config(name = "canceled_attack_sounds", description = {"Prevent various attack sounds added for the cooldown mechanic from playing.", "This option can be used to prevent basically any individual sound from playing.", ConfigDataSet.CONFIG_DESCRIPTION})
    List<String> canceledAttackSoundsRaw = KeyedValueProvider.toString(Registries.SOUND_EVENT, SoundEvents.PLAYER_ATTACK_CRIT, SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundEvents.PLAYER_ATTACK_STRONG, SoundEvents.PLAYER_ATTACK_WEAK, SoundEvents.PLAYER_ATTACK_SWEEP);
    @Config(name = "canceled_particles", description = {"Disable rendering for certain particle types from modern combat, since they kinda clutter the screen since attacks can be dealt much quicker with classic combat options enabled.", "This option can be used to prevent basically any particle from showing.", ConfigDataSet.CONFIG_DESCRIPTION})
    List<String> canceledParticlesRaw = KeyedValueProvider.toString(Registries.PARTICLE_TYPE, ParticleTypes.DAMAGE_INDICATOR, ParticleTypes.SWEEP_ATTACK);
    @Config(description = "Is the sweeping edge enchantment required to perform a sweep attack.")
    public boolean requireSweepingEdge = true;
    @Config(description = "Attacking will no longer stop the player from sprinting. Very useful when swimming, so you can fight underwater without being stopped on every hit.")
    public boolean sprintAttacks = true;

    public ConfigDataSet<SoundEvent> canceledAttackSounds;
    public ConfigDataSet<ParticleType<?>> canceledParticles;

    @Override
    public void afterConfigReload() {
        this.canceledAttackSounds = ConfigDataSet.from(Registries.SOUND_EVENT, this.canceledAttackSoundsRaw);
        this.canceledParticles = ConfigDataSet.from(Registries.PARTICLE_TYPE, this.canceledParticlesRaw);
    }
}
