package fuzs.goldenagecombat.client.element;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AttackSpeedTooltipHandler {
    @SubscribeEvent
    public void onItemTooltip(final ItemTooltipEvent evt) {
        if (GoldenAgeCombat.CONFIG.client().adjustments.noAttackSpeedTooltip) {
            evt.getToolTip().removeIf(component -> component.toString().contains("attribute.name.generic.attack_speed"));
        }
    }
}
