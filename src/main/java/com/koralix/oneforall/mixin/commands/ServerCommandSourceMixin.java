package com.koralix.oneforall.mixin.commands;

import com.koralix.oneforall.commands.CommonCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(ServerCommandSource.class)
public abstract class ServerCommandSourceMixin implements CommonCommandSource {
    @Unique
    private final ServerCommandSource self = ServerCommandSource.class.cast(this);

    @Override
    public Optional<PlayerEntity> player() {
        return Optional.ofNullable(self.getPlayer());
    }
}
