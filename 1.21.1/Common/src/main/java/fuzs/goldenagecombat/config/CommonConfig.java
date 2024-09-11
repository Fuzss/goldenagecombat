package fuzs.goldenagecombat.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.List;

public class CommonConfig implements ConfigCore {
    @Config(description = "Boost sharpness enchantment to 1.25 damage points per level instead of just 0.5.", gameRestart = true)
    public boolean boostSharpness = false;
    @Config(description = "Only damage tools by 1 durability instead of 2 when attacking. Apply the same logic to swords when harvesting blocks.", gameRestart = true)
    public boolean noItemDurabilityPenalty = true;
    @Config(name = "legacy_attack_damage_values", description = "Revert weapon and tool attack damage to legacy values.", gameRestart = true)
    public boolean oldAttackDamage = true;
    @Config(name = "attack_damage_overrides", description = {"Overrides for setting and balancing attack damage values of items.", "Takes precedence over any changes made by \"legacy_attack_damage\" option, but requires it to be enabled.", "As with all items, this value is added ON TOP of the default attack strength of the player (which is 1.0 by default).", "Format for every entry is \"<namespace>:<path>,<amount>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color."}, gameRestart = true)
    List<String> attackDamageOverridesRaw = KeyedValueProvider.toString(Registries.ITEM);

    public ConfigDataSet<Item> attackDamageOverrides;

    @Override
    public void afterConfigReload() {
        this.attackDamageOverrides = ConfigDataSet.from(Registries.ITEM, this.attackDamageOverridesRaw, double.class);
    }
}
