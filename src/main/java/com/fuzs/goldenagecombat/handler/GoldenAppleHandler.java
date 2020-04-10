package com.fuzs.goldenagecombat.handler;

import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.config.CommonConfigHandler;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = GoldenAgeCombat.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GoldenAppleHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onUseItemFinish(final LivingEntityUseItemEvent.Finish evt) {

        if (CommonConfigHandler.GOLDEN_APPLE_EFFECTS.get() && evt.getItem().getItem() == Items.ENCHANTED_GOLDEN_APPLE) {

            evt.getEntityLiving().removePotionEffect(Effects.ABSORPTION);
            evt.getEntityLiving().addPotionEffect(new EffectInstance(Effects.ABSORPTION, 2400, 0));
            evt.getEntityLiving().addPotionEffect(new EffectInstance(Effects.REGENERATION, 600, 4));
        }
    }

    @SubscribeEvent
    public static void registerRecipeSerialziers(final RegistryEvent.Register<IRecipeSerializer<?>> evt) {

//        CraftingHelper.register(ConfigEnabledCondition.Serializer.INSTANCE);
//        ForgeRegistries.RECIPE_SERIALIZERS
    }

}
