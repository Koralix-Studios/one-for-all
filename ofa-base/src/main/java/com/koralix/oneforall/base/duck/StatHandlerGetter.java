package com.koralix.oneforall.base.duck;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.ServerStatHandler;

import java.util.UUID;

public interface StatHandlerGetter {
    ServerStatHandler get(PlayerEntity player);
    ServerStatHandler get(UUID uuid);
}
