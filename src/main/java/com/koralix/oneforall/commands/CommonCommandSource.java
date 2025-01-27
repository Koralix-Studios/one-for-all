package com.koralix.oneforall.commands;

import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public interface CommonCommandSource {
    /**
     * The player associated with this source.
     *
     * @return the player associated with this source
     */
    Optional<PlayerEntity> player();
}
