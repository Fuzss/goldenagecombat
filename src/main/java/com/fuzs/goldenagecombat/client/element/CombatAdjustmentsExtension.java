package com.fuzs.goldenagecombat.client.element;

import com.fuzs.goldenagecombat.element.CombatAdjustmentsElement;
import com.fuzs.puzzleslib_gc.element.extension.ElementExtension;
import com.fuzs.puzzleslib_gc.element.side.IClientElement;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class CombatAdjustmentsExtension extends ElementExtension<CombatAdjustmentsElement> implements IClientElement {

    private boolean removeAttackSpeedTooltip;

    public CombatAdjustmentsExtension(CombatAdjustmentsElement parent) {

        super(parent);
    }

    @Override
    public void setupClient() {

        this.addListener(this::onItemTooltip, EventPriority.LOW);
    }

    @Override
    public void setupClientConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Remove \"Attack Speed\" attribute from inventory tooltips.").define("Remove Attack Speed Tooltip", true), v -> this.removeAttackSpeedTooltip = v);
    }

    private void onItemTooltip(final ItemTooltipEvent evt) {

        if (this.removeAttackSpeedTooltip) {

            evt.getToolTip().removeIf(component -> component.toString().contains("attribute.name.generic.attackSpeed"));
        }
    }

}
