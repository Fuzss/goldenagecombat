package fuzs.goldenagecombat.config;

import fuzs.puzzleslib.config.AbstractConfig;
import fuzs.puzzleslib.config.annotation.Config;
import fuzs.puzzleslib.config.serialization.EntryCollectionBuilder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ServerConfig extends AbstractConfig {
    @Config
    public ClassicConfig classic = new ClassicConfig();
    @Config
    public AttributesConfig attributes = new AttributesConfig();
    @Config
    public BlockingConfig blocking = new BlockingConfig();
    @Config
    public AdjustmentsConfig adjustments = new AdjustmentsConfig();

    public ServerConfig() {
        super("");
    }

    @Override
    protected void afterConfigReload() {
        this.attributes.afterConfigReload();
        this.adjustments.afterConfigReload();
    }

    public enum FoodMechanics {
        VANILLA, COMBAT_UPDATE, LEGACY_COMBAT, CUSTOM
    }

    public static class ClassicConfig extends AbstractConfig {
        @Config(name = "remove_attack_cooldown", description = "Completely remove the attack cooldown as if it never even existed in the first place.")
        public boolean removeCooldown = true;
        @Config(name = "food_mechanics", description = {"Choose the food mechanics to use:", "VANILLA will change nothing and use surplus saturation for very quick health regeneration.", "LEGACY_COMBAT will make health only regenerated every 4 seconds, while requiring 18 or more food points.", "CUSTOM will make health only regenerated every 3 seconds, which requires more than 6 food points.", "COMBAT_TEST will make health regenerated every 2 seconds, which requires more than 6 food points. Also food points will be directly consumed when healing."})
        public FoodMechanics foodMechanics = FoodMechanics.CUSTOM;
        @Config(name = "weak_attacks_knock_back_player", description = "Player is knocked back by attacks which do not cause any damage, such as when hit by snowballs and eggs.")
        public boolean weakPlayerKnockback = true;
        @Config(name = "critical_hits_while_sprinting", description = "Sprinting and attacking no longer interfere with each other, making critical hits possible at all times.")
        public boolean criticalHitsSprinting = true;
        @Config(name = "fishing_rod_causes_knockback", description = "Fishing rod deals knockback upon hitting an entity.")
        public boolean fishingRodKnockback = true;
        @Config(name = "fishing_rod_launches_entities", description = "Entities reeled in using a fishing rod are slightly launched upwards.")
        public boolean fishingRodLaunch = true;
        @Config(name = "fishing_rod_breaks_slower", description = "Hooking entities with a fishing rod causes only 3 damage points to the rod instead of 5.")
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
        @Config(name = "upwards_knockback", description = "Makes knockback stronger towards targets not on the ground.")
        public boolean upwardsKnockback = true;

        public ClassicConfig() {
            super("classic_combat");
        }
    }

    public static class AttributesConfig extends AbstractConfig {
        @Config(name = "legacy_attack_damage", description = "Revert weapon and tool attack damage to legacy values.")
        public boolean oldAttackDamage = true;
        @Config(name = "attack_damage_overrides", description = {"Overrides for setting and balancing attack damage values of items.", "Takes precedence over any changes made by \"legacy_attack_damage\" option, but requires it to be enabled.", "As with all items, this value is added ON TOP of the default attack strength of the player (which is 1.0 by default).", "Format for every entry is \"<namespace>:<path>,<amount>\". Path may use asterisk as wildcard parameter. Tags are not supported."})
        private List<String> attackDamageOverridesRaw = Lists.newArrayList();
        @Config(name = "increased_attack_reach", description = "Makes it so that swords, hoes, and tridents have an increased reach when attacking.")
        public boolean increasedAttackReach = true;
        @Config(name = "attack_reach_overrides", description = {"Overrides for setting and balancing attack reach values of items.", "Takes precedence over any changes made by \"increasedAttackReach\" option, but requires it to be enabled.", "As with all items, this value is added ON TOP of the default attack reach of the player (which is 3.0 by default).", "Format for every entry is \"<namespace>:<path>,<amount>\". Path may use asterisk as wildcard parameter. Tags are not supported."})
        private List<String> attackReachOverridesRaw = Lists.newArrayList();

        public Map<Item, Double> attackDamageOverrides;
        public Map<Item, Double> attackReachOverrides;

        public AttributesConfig() {
            super("attributes");
        }

        @Override
        protected void afterConfigReload() {
            this.attackDamageOverrides = EntryCollectionBuilder.of(ForgeRegistries.ITEMS).buildMap(this.attackDamageOverridesRaw, (item, amount) -> amount.length == 1, "Wrong number of arguments").entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));
            this.attackReachOverrides = EntryCollectionBuilder.of(ForgeRegistries.ITEMS).buildMap(this.attackReachOverridesRaw, (item, amount) -> amount.length == 1, "Wrong number of arguments").entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));
        }
    }

    public static class BlockingConfig extends AbstractConfig {
        @Config(name = "allow_blocking", description = "Allow blocking with swords, which will reduce most incoming attacks by 50% and render a parry animation.")
        public boolean allowBlocking = true;

        public BlockingConfig() {
            super("sword_blocking");
        }
    }

    public static class AdjustmentsConfig extends AbstractConfig {
        @Config(name = "require_sweeping_edge", description = "Is the sweeping edge enchantment required to perform a sweep attack.")
        public boolean sweepingRequired = true;
        @Config(name = "prioritize_shield", description = "Prioritize shield blocking over sword blocking in case both items are held at the same time.")
        public boolean prioritizeShield = true;
        @Config(name = "sprint_attacks", description = "Attacking will no longer stop the player from sprinting. Very useful when swimming, so you can fight underwater without being stopped on every hit.")
        public boolean sprintAttacks = true;
        @Config(name = "canceled_attack_sounds", description = {"Prevent various attack sounds added for the cooldown mechanic from playing.", EntryCollectionBuilder.CONFIG_DESCRIPTION})
        private List<String> canceledAttackSoundsRaw = EntryCollectionBuilder.getKeyList(ForgeRegistries.SOUND_EVENTS, SoundEvents.PLAYER_ATTACK_CRIT, SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundEvents.PLAYER_ATTACK_STRONG, SoundEvents.PLAYER_ATTACK_WEAK);
        @Config(name = "hide_damage_indicators", description = "Stop heart particles spawned when the player attacks an entity from appearing.")
        public boolean noDamageIndicators = true;
        @Config(name = "min_hitbox_size", description = {"Force all entity hitboxes to have a cubic size of at least 0.9 blocks.", "This only affects targeting an entity, no collisions or whatsoever. Useful for hitting e.g. bats, rabbits, silverfish, fish, and most baby animals."})
        public boolean minHitboxSize = true;
        @Config(name = "half_sweeping_damage", description = "Only apply half the sweeping damage to indirectly hit mobs as seen in most recent combat test snapshots.")
        public boolean halfSweepingDamage = true;
        @Config(name = "no_sweeping_when_sneaking", description = "Do not perform sweep attacks when sneaking.")
        public boolean noSneakSweeping = false;
        @Config(name = "hold_attack_button", description = "Holding down the attack button keeps attacking continuously. No more spam clicking required.")
        public boolean holdAttackButton = true;
        @Config(name = "swing_through_grass", description = "Hit mobs through blocks without a collision shape such as tall grass without having to break the block first.")
        public boolean swingThroughGrass = true;

        public Set<SoundEvent> canceledAttackSounds;

        public AdjustmentsConfig() {
            super("combat_adjustments");
        }

        @Override
        protected void afterConfigReload() {
            this.canceledAttackSounds = EntryCollectionBuilder.of(ForgeRegistries.SOUND_EVENTS).buildSet(this.canceledAttackSoundsRaw);
        }
    }
}
