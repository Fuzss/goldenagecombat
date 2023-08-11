package fuzs.goldenagecombat.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;

import java.util.List;

public class ServerConfig implements ConfigCore {
    @Config(description = "Completely remove the attack cooldown as if it never even existed in the first place.")
    public boolean removeAttackCooldown = true;
    @Config(description = "Health only regenerates every 4 seconds, while requiring 18 or more food points. Surplus saturation does not yield quick health regeneration.")
    public boolean legacyFoodMechanics = false;
    @Config(description = "Player is knocked back by attacks which do not cause any damage, such as when hit by snowballs, eggs, and fishing rod hooks.")
    public boolean weakAttacksKnockBackPlayers = true;
    @Config(description = "Sprinting and attacking no longer interfere with each other, making critical hits possible at all times.")
    public boolean criticalHitsWhileSprinting = true;
    @Config(category = "fishing_rods", name = "cause_knockback", description = "Fishing rod deals knockback upon hitting an entity.")
    public boolean fishingRodKnockback = true;
    @Config(category = "fishing_rods", name = "launch_entities", description = "Entities reeled in using a fishing rod are slightly launched upwards.")
    public boolean fishingRodLaunch = true;
    @Config(category = "fishing_rods", name = "slower_breaking", description = "Hooking entities with a fishing rod causes only 3 damage points to the rod instead of 5.")
    public boolean fishingRodSlowerBreaking = true;
    @Config(description = "Boost sharpness enchantment to 1.25 damage points per level instead of just 0.5.")
    public boolean boostSharpness = false;
    @Config(name = "notch_apple_effects", description = "Give Regeneration V and Absorption I instead of Regeneration II and Absorption IV after consuming a notch apple.")
    public boolean goldenAppleEffects = true;
    @Config(description = "Expand all entity hitboxes by 10%, making hitting a target possible from a slightly greater range and with much increased accuracy.")
    public boolean inflateHitboxes = true;
    @Config(description = "Allow using the \"Attack\" button while the \"Use Item\" button is held for mining blocks. Does not make a lot of sense, but it used to be a feature in old pvp.")
    public boolean interactWhileUsing = false;
    @Config(description = "Makes knockback stronger towards targets not on the ground (does not apply when in water).")
    public boolean upwardsKnockback = true;
    @Config(category = "adjustments", name = "canceled_attack_sounds", description = {"Prevent various attack sounds added for the cooldown mechanic from playing.", "This option can be used to prevent basically any individual sound from playing.", ConfigDataSet.CONFIG_DESCRIPTION})
    List<String> canceledAttackSoundsRaw = ConfigDataSet.toString(Registry.SOUND_EVENT_REGISTRY, SoundEvents.PLAYER_ATTACK_CRIT, SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundEvents.PLAYER_ATTACK_STRONG, SoundEvents.PLAYER_ATTACK_WEAK, SoundEvents.PLAYER_ATTACK_SWEEP);
    @Config(category = "adjustments", name = "canceled_particles", description = {"Disable rendering for certain particle types from modern combat, since they kinda clutter the screen since attacks can be dealt much quicker with classic combat options enabled.", "This option can be used to prevent basically any particle from showing.", ConfigDataSet.CONFIG_DESCRIPTION})
    List<String> canceledParticlesRaw = ConfigDataSet.toString(Registry.PARTICLE_TYPE_REGISTRY, ParticleTypes.DAMAGE_INDICATOR, ParticleTypes.SWEEP_ATTACK);
    @Config(description = "Is the sweeping edge enchantment required to perform a sweep attack.")
    public boolean requireSweepingEdge = true;
    @Config(description = "Attacking will no longer stop the player from sprinting. Very useful when swimming, so you can fight underwater without being stopped on every hit.")
    public boolean sprintAttacks = true;
    @Config(description = "Only damage tools by 1 durability instead of 2 when attacking. Apply the same logic to swords when harvesting blocks.")
    public boolean noItemDurabilityPenalty = true;
    @Config(category = "attributes", name = "legacy_attack_damage_values", description = "Revert weapon and tool attack damage to legacy values.")
    public boolean oldAttackDamage = true;
    @Config(category = "attributes", name = "custom_attack_damage_overrides", description = {"Overrides for setting and balancing attack damage values of items.", "Takes precedence over any changes made by \"legacy_attack_damage\" option, but requires it to be enabled.", "As with all items, this value is added ON TOP of the default attack strength of the player (which is 1.0 by default).", "Format for every entry is \"<namespace>:<path>,<amount>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color."})
    List<String> attackDamageOverridesRaw = ConfigDataSet.toString(Registry.ITEM_REGISTRY);

    public ConfigDataSet<SoundEvent> canceledAttackSounds;
    public ConfigDataSet<ParticleType<?>> canceledParticles;
    public ConfigDataSet<Item> attackDamageOverrides;

    @Override
    public void afterConfigReload() {
        this.canceledAttackSounds = ConfigDataSet.from(Registry.SOUND_EVENT_REGISTRY, this.canceledAttackSoundsRaw);
        this.canceledParticles = ConfigDataSet.from(Registry.PARTICLE_TYPE_REGISTRY, this.canceledParticlesRaw);
        this.attackDamageOverrides = ConfigDataSet.from(Registry.ITEM_REGISTRY, this.attackDamageOverridesRaw, (i, o) -> ((double) o) >= 0.0, double.class);
    }
}
