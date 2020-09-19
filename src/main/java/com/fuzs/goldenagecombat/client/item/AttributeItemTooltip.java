package com.fuzs.goldenagecombat.client.item;

import com.fuzs.goldenagecombat.client.config.ClientConfigHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class AttributeItemTooltip {

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onItemTooltip(final ItemTooltipEvent evt) {

        if (ClientConfigHandler.TOOLTIP_SPEED.get()) evt.getToolTip().removeIf(component -> component.toString().contains("attribute.name.generic.attackSpeed"));
        if (ClientConfigHandler.TOOLTIP_TOUGHNESS.get()) evt.getToolTip().removeIf(component -> component.toString().contains("attribute.name.generic.armorToughness"));
    }

}
