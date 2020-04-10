package com.fuzs.goldenagecombat.handler;

import com.fuzs.goldenagecombat.config.CommonConfigHandler;
import com.google.common.collect.Sets;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SoundEventHandler {

    private static final Set<SoundEvent> ATTACK_SOUNDS = Sets.newHashSet();
    private static final Map<SoundEvent, ForgeConfigSpec.BooleanValue> ATTACK_SOUNDS_SOURCE = new HashMap<SoundEvent, ForgeConfigSpec.BooleanValue>() {{

        put(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, CommonConfigHandler.SOUNDS_CRIT);
        put(SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, CommonConfigHandler.SOUNDS_KNOCKBACK);
        put(SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, CommonConfigHandler.SOUNDS_NODAMAGE);
        put(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, CommonConfigHandler.SOUNDS_STRONG);
        put(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, CommonConfigHandler.SOUNDS_SWEEP);
        put(SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, CommonConfigHandler.SOUNDS_WEAK);
    }};

    public SoundEventHandler() {

        ATTACK_SOUNDS.addAll(ATTACK_SOUNDS_SOURCE.entrySet().stream()
                .filter(entry -> entry.getValue().get())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet()));
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlaySoundAtEntity(final PlaySoundAtEntityEvent evt) {

        // disable Combat Update player attack sounds
        if (ATTACK_SOUNDS.contains(evt.getSound())) {

            evt.setCanceled(true);
        }
    }

}
