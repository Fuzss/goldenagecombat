package fuzs.goldenagecombat.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;

import java.util.List;

public class ServerConfig implements ConfigCore {
    @Config(name = "classic_combat", description = "Classic combat is the focus of this mod, bringing back combat exactly as it used to be before the combat update. The option for removing the attack cooldown is probably the most important one here.")
    public ClassicConfig classic = new ClassicConfig();
    @Config(name = "combat_attributes")
    public AttributesConfig attributes = new AttributesConfig();
    @Config(name = "sword_blocking")
    public BlockingConfig blocking = new BlockingConfig();
    @Config(name = "combat_tests", description = "This section offers a selection of features related to Mojang's combat test snapshots which fit rather well alongside classic combat.")
    public CombatTestsConfig combatTests = new CombatTestsConfig();

    public enum FoodMechanics {
        VANILLA, COMBAT_UPDATE, LEGACY_COMBAT, CUSTOM
    }

    public static class ClassicConfig implements ConfigCore {
        @Config(name = "remove_attack_cooldown", description = "Completely remove the attack cooldown as if it never even existed in the first place.")
        public boolean removeCooldown = true;
        @Config(name = "food_mechanics", description = {"Choose the food mechanics to use:", "VANILLA will change nothing and use surplus saturation for very quick health regeneration.", "LEGACY_COMBAT will make health only regenerate every 4 seconds, while requiring 18 or more food points.", "CUSTOM will make health only regenerate every 3 seconds, which requires more than 6 food points.", "COMBAT_TEST will make health regenerate every 2 seconds, which requires more than 6 food points. Also food points will be directly consumed when healing."})
        public FoodMechanics foodMechanics = FoodMechanics.CUSTOM;
        @Config(name = "weak_attacks_knock_back_player", description = "Player is knocked back by attacks which do not cause any damage, such as when hit by snowballs, eggs, and fishing rod hooks.")
        public boolean weakPlayerKnockback = true;
        @Config(name = "critical_hits_while_sprinting", description = "Sprinting and attacking no longer interfere with each other, making critical hits possible at all times.")
        public boolean criticalHitsSprinting = true;
        @Config(category = "fishing_rod", name = "cause_knockback", description = "Fishing rod deals knockback upon hitting an entity.")
        public boolean fishingRodKnockback = true;
        @Config(category = "fishing_rod", name = "launch_entities", description = "Entities reeled in using a fishing rod are slightly launched upwards.")
        public boolean fishingRodLaunch = true;
        @Config(category = "fishing_rod", name = "slower_breaking", description = "Hooking entities with a fishing rod causes only 3 damage points to the rod instead of 5.")
        public boolean fishingRodSlowerBreaking = true;
        @Config(name = "boost_sharpness", description = "Boost sharpness enchantment to 1.25 damage points per level instead of just 0.5.")
        public boolean boostSharpness = false;
        @Config(name = "notch_apple_effects", description = "Give Regeneration V and Absorption I instead of Regeneration II and Absorption IV after consuming a notch apple.")
        public boolean goldenAppleEffects = true;
        @Config(name = "sideways_backwards_walking", description = "The player's body turns sideways when walking backwards instead of remaining straight. Only a visual feature.")
        public boolean backwardsWalking = true;
        @Config(name = "inflate_hitboxes", description = "Expand all entity hitboxes by 10%, making hitting a target possible from a slightly greater range and with much increased accuracy.")
        public boolean inflateHitboxes = true;
        @Config(name = "quick_slowdown", description = "When slowing down movement or stopping completely momentum is lost much quicker.")
        public boolean quickSlowdown = false;
        @Config(name = "interact_while_using", description = "Allow using the \"Attack\" button while the \"Use Item\" button is held for mining blocks. Does not make a lot of sense, but it used to be a feature in old pvp.")
        public boolean interactWhileUsing = false;
        @Config(name = "upwards_knockback", description = "Makes knockback stronger towards targets not on the ground (does not apply when in water).")
        public boolean upwardsKnockback = true;
        @Config(category = "adjustments", name = "canceled_attack_sounds", description = {"Prevent various attack sounds added for the cooldown mechanic from playing.", "This option can be used to prevent basically any individual sound from playing.", ConfigDataSet.CONFIG_DESCRIPTION})
        List<String> canceledAttackSoundsRaw = ConfigDataSet.toString(Registries.SOUND_EVENT, SoundEvents.PLAYER_ATTACK_CRIT, SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundEvents.PLAYER_ATTACK_STRONG, SoundEvents.PLAYER_ATTACK_WEAK, SoundEvents.PLAYER_ATTACK_SWEEP);
        @Config(category = "adjustments", name = "canceled_particles", description = {"Disable rendering for certain particle types from modern combat, since they kinda clutter the screen since attacks can be dealt much quicker with classic combat options enabled.", "This option can be used to prevent basically any particle from showing.", ConfigDataSet.CONFIG_DESCRIPTION})
        List<String> canceledParticlesRaw = ConfigDataSet.toString(Registries.PARTICLE_TYPE, ParticleTypes.DAMAGE_INDICATOR, ParticleTypes.SWEEP_ATTACK);

        public ConfigDataSet<SoundEvent> canceledAttackSounds;
        public ConfigDataSet<ParticleType<?>> canceledParticles;

        @Override
        public void afterConfigReload() {
            this.canceledAttackSounds = ConfigDataSet.from(Registries.SOUND_EVENT, this.canceledAttackSoundsRaw);
            this.canceledParticles = ConfigDataSet.from(Registries.PARTICLE_TYPE, this.canceledParticlesRaw);
        }
    }

    public static class AttributesConfig implements ConfigCore {
        @Config(name = "legacy_attack_damage", description = "Revert weapon and tool attack damage to legacy values.")
        public boolean oldAttackDamage = true;
        @Config(name = "attack_damage_overrides", description = {"Overrides for setting and balancing attack damage values of items.", "Takes precedence over any changes made by \"legacy_attack_damage\" option, but requires it to be enabled.", "As with all items, this value is added ON TOP of the default attack strength of the player (which is 1.0 by default).", "Format for every entry is \"<namespace>:<path>,<amount>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color."})
        List<String> attackDamageOverridesRaw = ConfigDataSet.toString(Registries.ITEM);
        @Config(name = "attack_reach", description = "Makes it so that swords, hoes, and tridents have an increased reach when attacking.")
        public boolean attackRange = true;
        @Config(name = "attack_reach_overrides", description = {"Overrides for setting and balancing attack reach values of items.", "Takes precedence over any changes made by \"attack_reach\" option, but requires it to be enabled.", "As with all items, this value is added ON TOP of the default attack reach of the player (which is 3.0 by default, and has a hard cap at 6.0).", "Format for every entry is \"<namespace>:<path>,<amount>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color."})
        List<String> attackReachOverridesRaw = ConfigDataSet.toString(Registries.ITEM);

        public ConfigDataSet<Item> attackDamageOverrides;
        public ConfigDataSet<Item> attackReachOverrides;

        @Override
        public void afterConfigReload() {
            this.attackDamageOverrides = ConfigDataSet.from(Registries.ITEM, this.attackDamageOverridesRaw, (i, o) -> ((double) o) >= 0.0, double.class);
            this.attackReachOverrides = ConfigDataSet.from(Registries.ITEM, this.attackReachOverridesRaw, (i, o) -> ((double) o) >= 0.0, double.class);
        }
    }

    public static class BlockingConfig implements ConfigCore {
        @Config(name = "allow_blocking", description = "Allow blocking with swords, which will reduce most incoming attacks by 50% and render a parry animation.")
        public boolean allowBlocking = true;
        @Config(description = "Prioritize usable off-hand items over sword blocking from the main hand. Items not recognized by default can be included in a dedicated item tag.")
        public boolean prioritizeOffHand = true;
        @Config(description = "Percentage an incoming attack will be reduced by when blocking.")
        public double blockedDamage = 0.5;
        @Config(description = "Damage sword when blocking an attack depending on the amount of damage blocked. Sword is only damaged when at least three damage points have been blocked, just like a shield.")
        public boolean damageSword = false;
        @Config(name = "knockback_reduction", description = "Percentage to reduce knockback by while sword blocking.")
        @Config.DoubleRange(min = 0.0, max = 1.0)
        public double knockbackReduction = 0.2;
        @Config(name = "protection_arc", description = "Arc of available protection depending on what angle the attack is coming from and where the player is looking (means the lower this angle the closer you need to be facing your attacker).")
        @Config.DoubleRange(min = 0.0, max = 360.0)
        public double protectionArc = 360.0;
        @Config(description = "Amount of ticks after starting to block in which an attack will be completely nullified like when blocking with a shield.")
        @Config.IntRange(min = 0, max = 72_000)
        public int parryWindow = 10;
        @Config(description = "Damage sword when successfully parrying depending on the amount of damage blocked. Sword is only damaged when at least three damage points have been parried, just like a shield.")
        public boolean damageSwordOnParry = false;
        @Config(description = "Blocking requires both hands, meaning the hand not holding the sword must be empty.")
        public boolean requireBothHands = false;
        @Config(description = "Incoming projectiles such as arrows or tridents will ricochet while blocking.")
        public boolean deflectProjectiles = false;
    }

    public static class CombatTestsConfig implements ConfigCore {
        @Config(category = "sweeping", name = "require_sweeping_edge", description = "Is the sweeping edge enchantment required to perform a sweep attack.")
        public boolean sweepingRequired = true;
        @Config(category = "sweeping", name = "half_sweeping_damage", description = "Only apply half the sweeping damage to indirectly hit mobs for better balancing of the sweeping feature.")
        public boolean halfSweepingDamage = true;
        @Config(category = "sweeping", name = "no_sweeping_when_sneaking", description = "Do not perform sweep attacks when sneaking.")
        public boolean noSneakSweeping = false;
        @Config(category = "sweeping", name = "air_sweep_attack", description = {"Allow sweep attack without hitting mobs, just by attacking air basically.", "This attack will not work when the attack button is held for continuous attacking."})
        public boolean airSweepAttack = true;
        @Config(category = "sweeping", name = "continuous_air_sweeping", description = "Allow sweep attacks to trigger without hitting a target even when attacking continuously by holding the attack button.")
        public boolean continuousAirSweeping = true;
        @Config(name = "sprint_attacks", description = "Attacking will no longer stop the player from sprinting. Very useful when swimming, so you can fight underwater without being stopped on every hit.")
        public boolean sprintAttacks = true;
        @Config(name = "min_hitbox_size", description = {"Force all entity hitboxes to have a cubic size of at least 0.9 blocks, making them easier to hit and shoot.", "This only affects targeting an entity, no collisions or whatsoever. Useful for hitting e.g. bats, rabbits, silverfish, fish, and most baby animals."})
        public boolean minHitboxSize = true;
        @Config(name = "hold_attack_button", description = "Holding down the attack button keeps attacking continuously. No more spam clicking required.")
        public boolean holdAttackButton = false;
        @Config(name = "hold_attack_button_delay", description = {"Delay in ticks between attacks when holding the attack button is enabled.", "This basically also puts a cap on the max spam clicking speed."})
        @Config.IntRange(min = 0)
        public int holdAttackButtonDelay = 5;
        @Config(name = "swing_through_grass", description = "Hit mobs through blocks without a collision shape such as tall grass without having to break the block first.")
        public boolean swingThroughGrass = true;
        @Config(name = "shield_knockback_fix", description = "Fix shields not knocking back attackers (see MC-147694).")
        public boolean shieldKnockbackFix = true;
        @Config(name = "increase_stack_size", description = "Increase snowball and egg stack size from 16 to 64, and potion stack size from 1 to 16 (only for potions of the same type of course).")
        public boolean increaseStackSize = true;
        @Config(name = "throwables_delay", description = "Add a delay of 4 ticks between throwing snowballs or eggs, just like with ender pearls.")
        public boolean throwablesDelay = true;
        @Config(name = "eating_interruption", description = "Eating and drinking both are interrupted if the player is damaged.")
        public boolean eatingInterruption = true;
        @Config(name = "remove_shield_delay", description = "Skip 5 tick warm-up delay when activating a shield, so they become effective instantly.")
        public boolean noShieldDelay = true;
        @Config(name = "fast_switching", description = "Attack cooldown is unaffected by switching hotbar items.")
        public boolean fastSwitching = true;
        @Config(name = "fast_drinking", description = "It only takes 20 ticks to drink liquid foods (such as potions, milk, and bottled liquids) instead of 32 or 40.")
        public boolean fastDrinking = true;
        @Config(name = "no_axe_attack_penalty", description = "Only damages axes by 1 durability instead of 2 when attacking so they properly be used as weapons.")
        public boolean noAxeAttackPenalty = true;
        @Config(name = "retain_energy_on_miss", description = "Melee attacks that don't hit a target won't trigger the attack cooldown.")
        public boolean retainEnergy = true;
        @Config(name = "min_attack_strength", description = {"Disable attacking when attack cooldown is below a certain percentage.", "Setting this to 0.0 means attacking is possible with any strength as in vanilla."})
        @Config.DoubleRange(min = 0.0, max = 1.0)
        public double minAttackStrength = 0.0;
        @Config(name = "no_projectile_immunity", description = "Disables damage immunity when hit by a projectile. Makes it possible for entities to be hit by multiple projectiles at once (useful for the multishot enchantment).")
        public boolean noProjectileImmunity = true;
        @Config(name = "shield_protection_arc", description = {"Arc of available protection depending on what angle the attack is coming from and where the player is looking (means the lower this angle the closer you need to be facing your attacker).", "Vanilla protection arc is 180 degrees, which has been reduced to around 100 in combat tests.", "This does not change the protection arc for projectiles which remains at 180 degress."})
        @Config.DoubleRange(min = 0.0, max = 360.0)
        public double shieldProtectionArc = 100.0;
    }
}