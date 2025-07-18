package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ClientConfig;
import fuzs.goldenagecombat.util.AttributeTooltipHelper;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
abstract class ItemStackMixin implements DataComponentHolder {

    @Inject(method = "addAttributeTooltips", at = @At("HEAD"), cancellable = true)
    private void addAttributeTooltips(Consumer<Component> tooltipAdder, TooltipDisplay tooltipDisplay, @Nullable Player player, CallbackInfo callback) {
        if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).attributesStyle != ClientConfig.AttributesStyle.VANILLA) {
            callback.cancel();
        }
        if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).attributesStyle == ClientConfig.AttributesStyle.LEGACY) {
            AttributeTooltipHelper.addLegacyAttributeTooltips(ItemStack.class.cast(this),
                    tooltipAdder,
                    tooltipDisplay,
                    player);
        }
    }
}
