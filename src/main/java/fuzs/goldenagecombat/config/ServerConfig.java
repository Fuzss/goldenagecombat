package fuzs.goldenagecombat.config;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.puzzleslib.config.AbstractConfig;
import fuzs.puzzleslib.config.annotation.Config;
import fuzs.puzzleslib.config.serialization.EntryCollectionBuilder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Set;

public class ServerConfig extends AbstractConfig {
    @Config
    public AdjustmentsConfig adjustments = new AdjustmentsConfig();
    @Config
    public ClassicConfig classic = new ClassicConfig();

    public ServerConfig() {
        super("");
    }

    @Override
    protected void afterConfigReload() {
        this.adjustments.afterConfigReload();
        this.classic.afterConfigReload();
    }

    public static class AdjustmentsConfig extends AbstractConfig {
        @Config(name = "Require Sweeping Edge", description = "Is the sweeping edge enchantment required to perform a sweep attack.")
        public boolean sweepingRequired = true;
        @Config(name = "Prioritize Shield", description = "Prioritize shield blocking over sword blocking in case both items are held at the same time.")
        public boolean prioritizeShield = true;
        @Config(name = "Canceled Attack Sounds", description = {"Prevent various attack sounds added for the cooldown mechanic from playing.", EntryCollectionBuilder.CONFIG_DESCRIPTION})
        private List<String> canceledAttackSoundsRaw = EntryCollectionBuilder.getKeyList(ForgeRegistries.SOUND_EVENTS, SoundEvents.PLAYER_ATTACK_CRIT, SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundEvents.PLAYER_ATTACK_STRONG, SoundEvents.PLAYER_ATTACK_WEAK);
        @Config(name = "Hide Damage Indicators", description = "Stop heart particles spawned when the player attacks an entity from appearing.")
        public boolean noDamageIndicators = true;

        public Set<SoundEvent> canceledAttackSounds;

        public AdjustmentsConfig() {
            super("combat_adjustments");
        }

        @Override
        protected void afterConfigReload() {
            this.canceledAttackSounds = EntryCollectionBuilder.of(ForgeRegistries.SOUND_EVENTS).buildSet(this.canceledAttackSoundsRaw);
        }
    }

    public static class ClassicConfig extends AbstractConfig {
        @Config(name = "Remove Attack Cooldown", description = "Completely remove the attack cooldown as if it never even existed in the first place.")
        public boolean removeCooldown = true;
        @Config(name = "Legacy Attack Damage", description = "Revert weapon and tool attack damage to legacy values.")
        public boolean oldAttackDamage = true;
        @Config(name = "Attack Damage Blacklist", description = {"Blacklist for items not handled as expected yb the \"Legacy Attack Damage\" option.", EntryCollectionBuilder.CONFIG_DESCRIPTION, "Mod developers can include their items in the \"" + GoldenAgeCombat.MOD_ID + ":attack_damage_blacklist\" item tag."})
        private List<String> attackDamageBlacklistRaw = Lists.newArrayList();
        @Config(name = "Disable Health Regen Boost", description = "Surplus saturation is no longer used for quick health regeneration, resulting in health only being regenerated from food every 4 seconds.")
        public boolean noFastRegen = true;
        @Config(name = "Weak Attacks Knock Back Player", description = "Player is knocked back by attacks which do not cause any damage, such as when hit by snowballs.")
        public boolean weakPlayerKnockback = true;
        @Config(name = "Critical Sprint Hits", description = "Sprinting and attacking no longer interfere with each other, making critical hits possible at all times.")
        public boolean criticalSprinting = true;
        @Config(name = "Fishing Rod Causes Knockback", description = "Fishing rod deals knockback upon hitting an entity.")
        public boolean fishingRodKnockback = true;
        @Config(name = "Fishing Rod Launches Entities", description = "Entities reeled in using a fishing rod are slightly launched upwards.")
        public boolean fishingRodLaunch = true;
        @Config(name = "Boost Sharpness", description = "Boost sharpness enchantment to 1.25 damage points per level instead of just 0.5.")
        public boolean boostSharpness = false;
        @Config(name = "Notch Apple Effects", description = "Give Regeneration V and Absorption I instead of Regeneration II and Absorption IV after consuming a notch apple.")
        public boolean goldenAppleEffects = true;
        @Config(name = "Notch Apple Effects", description = "The player's body turns sideways when walking backwards instead of remaining straight.")
        public boolean backwardsWalking = true;

        public Set<Item> attackDamageBlacklist;

        public ClassicConfig() {
            super("classic_combat");
        }

        @Override
        protected void afterConfigReload() {
            this.attackDamageBlacklist = EntryCollectionBuilder.of(ForgeRegistries.ITEMS).buildSet(this.attackDamageBlacklistRaw);
        }
    }
}
