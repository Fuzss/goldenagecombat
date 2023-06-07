package fuzs.goldenagecombat.core;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.AABB;

public class FabricAbstractions implements CommonAbstractions {

    @Override
    public Attribute getBlockReachAttribute() {
        return ReachEntityAttributes.REACH;
    }

    @Override
    public Attribute getAttackRangeAttribute() {
        return ReachEntityAttributes.ATTACK_RANGE;
    }

    @Override
    public boolean canPerformSwordSweepAction(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }

    @Override
    public AABB getSweepHitBox(Player player, Entity target) {
        return target.getBoundingBox().inflate(1.0, 0.25, 1.0);
    }
}
