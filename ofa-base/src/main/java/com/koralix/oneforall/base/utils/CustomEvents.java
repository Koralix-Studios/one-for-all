package com.koralix.oneforall.base.utils;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;

public class CustomEvents {
    public static final Event<OnStatEvent> ON_STAT_EVENT = EventFactory.createArrayBacked(OnStatEvent.class, (callbacks) -> (handler, player, stat, value) -> {
        for (OnStatEvent callback : callbacks) {
            callback.onStatEvent(handler, player, stat, value);
        }
    });

    @FunctionalInterface
    public interface OnStatEvent {
        void onStatEvent(StatHandler handler, PlayerEntity player, Stat<?> stat, int value);
    }
}


