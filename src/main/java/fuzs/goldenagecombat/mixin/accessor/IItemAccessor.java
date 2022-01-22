package fuzs.goldenagecombat.mixin.accessor;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(Item.class)
public interface IItemAccessor {

    @Accessor("ATTACK_DAMAGE_MODIFIER")
    static UUID getAttackDamageModifier() {

        throw new IllegalStateException();
    }

}
