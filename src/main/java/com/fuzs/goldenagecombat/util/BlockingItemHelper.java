package com.fuzs.goldenagecombat.util;

import com.fuzs.materialmaster.api.SyncProvider;
import com.google.common.collect.Sets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;

import java.util.Set;

public class BlockingItemHelper {

    @SyncProvider(path = {"blocking", "Blocking Exclusion List"})
    public static final Set<Item> EXCLUDE = Sets.newHashSet();
    @SyncProvider(path = {"blocking", "Blocking Inclusion List"})
    public static final Set<Item> INCLUDE = Sets.newHashSet();

    private Item activeItem = Items.AIR;
    private boolean activeBlock;

    public boolean isActiveItemStackBlocking(PlayerEntity player) {

        return player.isHandActive() && this.canItemStackBlock(player.getActiveItemStack());
    }

    public boolean canItemStackBlock(ItemStack stack) {

        Item item = stack.getItem();
        if (item != this.activeItem) {

            this.activeItem = item;
            if (EXCLUDE.contains(item)) {

                this.activeBlock = false;
            } else if (item instanceof SwordItem) {

                this.activeBlock = true;
            } else {

                this.activeBlock = INCLUDE.contains(item);
            }
        }

        return this.activeBlock;
    }

}
