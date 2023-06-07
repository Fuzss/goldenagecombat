package fuzs.goldenagecombat.core;

import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

    Attribute getBlockReachAttribute();

    Attribute getAttackRangeAttribute();

    boolean canPerformSwordSweepAction(ItemStack stack);

    AABB getSweepHitBox(Player player, Entity target);
}
