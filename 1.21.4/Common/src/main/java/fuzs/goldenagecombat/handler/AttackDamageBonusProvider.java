package fuzs.goldenagecombat.handler;

/**
 * Vanilla's new {@link net.minecraft.world.item.ToolMaterial} is no longer stored for items, it is only used during
 * {@link net.minecraft.world.item.Item.Properties} construction for calculating some values.
 * <p>
 * Since we need the attack damage bonus value from the tool material though, we must store it in the item constructor,
 * which is the only place where it is still available.
 */
public interface AttackDamageBonusProvider {

    float goldenagecombat$getAttackDamageBonus();
}
