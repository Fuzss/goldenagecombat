package fuzs.goldenagecombat.mixin.accessor;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(Item.class)
public interface ItemAccessor {
    @Accessor("BASE_ATTACK_DAMAGE_UUID")
    static UUID getBaseAttackDamageUUID() {
        throw new IllegalStateException();
    }

    @Accessor
    @Mutable
    void setMaxStackSize(int maxStackSize);
}
