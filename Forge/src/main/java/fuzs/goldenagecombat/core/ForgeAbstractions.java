package fuzs.goldenagecombat.core;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolActions;

public class ForgeAbstractions implements CommonAbstractions {

    @Override
    public Attribute getBlockReachAttribute() {
        return ForgeMod.BLOCK_REACH.get();
    }

    @Override
    public Attribute getAttackRangeAttribute() {
        return ForgeMod.ENTITY_REACH.get();
    }

    @Override
    public boolean canPerformSwordSweepAction(ItemStack stack) {
        return stack.canPerformAction(ToolActions.SWORD_SWEEP);
    }

    @Override
    public AABB getSweepHitBox(Player player, Entity target) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(player, target);
    }
}
