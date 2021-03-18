package com.fuzs.goldenagecombat.element;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.client.element.SwordBlockingExtension;
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
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class SwordBlockingElement extends ClientExtensibleElement<SwordBlockingExtension> {

    public static final Tags.IOptionalNamedTag<Item> SWORD_BLOCKING_EXCLUSIONS_TAG = ItemTags.createOptional(new ResourceLocation(GoldenAgeCombat.MODID, "sword_blocking_exclusions"));
    public static final Tags.IOptionalNamedTag<Item> SWORD_BLOCKING_INCLUSIONS_TAG = ItemTags.createOptional(new ResourceLocation(GoldenAgeCombat.MODID, "sword_blocking_inclusions"));

    public SwordBlockingElement() {

        super(element -> new SwordBlockingExtension((SwordBlockingElement) element));
    }

    @Override
    public String[] getDescription() {

        return new String[]{"Reintroduces sword blocking exactly like it used to be.", "Additional swords can be included using the \"" + GoldenAgeCombat.MODID + ":sword_blocking_inclusions\" item tag.", "Swords which already have a right-clicking ability can be excluded using the \"" + GoldenAgeCombat.MODID + ":sword_blocking_exclusions\" item tag."};
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

    private void onRightClickItem(final PlayerInteractEvent.RightClickItem evt) {

        PlayerEntity player = evt.getPlayer();
        if (canItemStackBlock(evt.getItemStack())) {

            CombatAdjustmentsElement element = (CombatAdjustmentsElement) GoldenAgeCombat.COMBAT_ADJUSTMENTS;
            if (!element.isEnabled() || !element.prioritizeShield || evt.getHand() != Hand.MAIN_HAND || evt.getHand() == Hand.MAIN_HAND && player.getHeldItemOffhand().getUseAction() != UseAction.BLOCK) {

                evt.setCanceled(true);
                player.setActiveHand(evt.getHand());
                // cause reequip animation, but don't swing hand, not to be confused with ActionResultType#SUCCESS
                evt.setCancellationResult(ActionResultType.CONSUME);
            }

        }
    }

    private void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity && canItemStackBlock(evt.getItem())) {

            // default use duration for items
            evt.setDuration(72000);
        }
    }

    private void onLivingHurt(final LivingHurtEvent evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) evt.getEntityLiving();
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

    public static boolean isActiveItemStackBlocking(PlayerEntity player) {

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
