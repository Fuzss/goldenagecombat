package fuzs.goldenagecombat.client.element;

import fuzs.goldenagecombat.GoldenAgeCombat;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class AttackSpeedTooltipHandler {
    @SubscribeEvent
    public void onItemTooltip(final ItemTooltipEvent evt) {
        if (GoldenAgeCombat.CONFIG.client().adjustments.noAttackSpeedTooltip) {
            final List<Component> list = evt.getToolTip();
            list.removeIf(component -> component.toString().contains("attribute.name.generic.attack_speed"));
            list.removeIf(component -> component instanceof TranslatableComponent translatableComponent && translatableComponent.getKey().startsWith("item.modifiers."));
            for (int i = 0; i < list.size(); i++) {

            }
        }
    }
}
