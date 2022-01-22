package fuzs.goldenagecombat.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.client.element.SwordBlockingRenderer;
import com.fuzs.puzzleslib_gc.element.extension.ClientExtensibleElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.UseAction;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SwordBlockingElement extends ClientExtensibleElement<SwordBlockingRenderer> {

    public static final Tags.IOptionalNamedTag<Item> SWORD_BLOCKING_EXCLUSIONS_TAG = ItemTags.createOptional(new ResourceLocation(GoldenAgeCombat.MOD_ID, "sword_blocking_exclusions"));
    public static final Tags.IOptionalNamedTag<Item> SWORD_BLOCKING_INCLUSIONS_TAG = ItemTags.createOptional(new ResourceLocation(GoldenAgeCombat.MOD_ID, "sword_blocking_inclusions"));

    public SwordBlockingElement() {

        super(element -> new SwordBlockingRenderer((SwordBlockingElement) element));
    }

    @Override
    public String[] getDescription() {

        return new String[]{"Reintroduces sword blocking exactly like it used to be.", "Additional swords can be included using the \"" + GoldenAgeCombat.MOD_ID + ":sword_blocking_inclusions\" item tag.", "Swords which already have a right-clicking ability can be excluded using the \"" + GoldenAgeCombat.MOD_ID + ":sword_blocking_exclusions\" item tag."};
    }

    @Override
    public String[] isIncompatibleWith() {

        return new String[]{"swordblockingmechanics"};
    }

    @Override
    public void setupCommon() {

        // give other mods a chance to cancel the event for their own swords before we do
        this.addListener(this::onRightClickItem, EventPriority.LOW);
        this.addListener(this::onItemUseStart);
        this.addListener(this::onLivingHurt);
    }

    @SubscribeEvent
    public void onRightClickItem(final PlayerInteractEvent.RightClickItem evt) {
        Player player = evt.getPlayer();
        if (canItemStackBlock(evt.getItemStack())) {

            CombatAdjustmentsHandler element = (CombatAdjustmentsHandler) GoldenAgeCombat.COMBAT_ADJUSTMENTS;
            if (!element.isEnabled() || !element.prioritizeShield || evt.getHand() != InteractionHand.MAIN_HAND || evt.getHand() == InteractionHand.MAIN_HAND && player.getOffhandItem().getUseAnimation() != UseAnim.BLOCK) {

                evt.setCanceled(true);
                player.setActiveHand(evt.getHand());
                // cause reequip animation, but don't swing hand, not to be confused with ActionResultType#SUCCESS
                evt.setCancellationResult(ActionResultType.CONSUME);
            }

        }
    }

    @SubscribeEvent
    public void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {
        if (evt.getEntityLiving() instanceof Player && canItemStackBlock(evt.getItem())) {
            // default use duration for items
            evt.setDuration(72000);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(final LivingHurtEvent evt) {
        if (evt.getEntityLiving() instanceof Player player) {
            if (isDamageSourceBlockable(evt.getSource()) && isActiveItemStackBlocking(player) && evt.getAmount() > 0.0F) {
                evt.setAmount((1.0F + evt.getAmount()) * 0.5F);
            }
        }
    }

    private static boolean isDamageSourceBlockable(DamageSource damageSourceIn) {

        Entity entity = damageSourceIn.getImmediateSource();
        if (entity instanceof AbstractArrowEntity) {

            if (((AbstractArrowEntity)entity).getPierceLevel() > 0) {

                return false;
            }
        }

        return !damageSourceIn.isUnblockable();
    }

    public static boolean isActiveItemStackBlocking(Player player) {

        return player.isHandActive() && canItemStackBlock(player.getActiveItemStack());
    }

    public static boolean canItemStackBlock(ItemStack blockingStack) {

        Item blockingItem = blockingStack.getItem();
        if (blockingItem.isIn(SWORD_BLOCKING_EXCLUSIONS_TAG)) {

            return false;
        } else if (blockingItem instanceof SwordItem) {

            return true;
        } else {

            return blockingItem.isIn(SWORD_BLOCKING_INCLUSIONS_TAG);
        }
    }

}
