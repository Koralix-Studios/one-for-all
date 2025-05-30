package com.koralix.oneforall.mixin.config;

import com.koralix.oneforall.config.ConfigActor;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;
import java.util.UUID;

@Mixin(ServerCommandSource.class)
@Implements(@Interface(iface = ConfigActor.class, prefix = "config$"))
public abstract class ServerCommandSourceMixin implements ConfigActor {
    @Shadow public abstract @Nullable ServerPlayerEntity getPlayer();

    public Optional<UUID> config$uuid() {
        return Optional.ofNullable(this.getPlayer()).map(ServerPlayerEntity::getUuid);
    }
}
