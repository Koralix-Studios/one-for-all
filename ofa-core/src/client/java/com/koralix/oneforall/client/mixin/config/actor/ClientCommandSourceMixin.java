package com.koralix.oneforall.client.mixin.config.actor;

import com.koralix.oneforall.config.ConfigActor;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;
import java.util.UUID;

@Mixin(ClientCommandSource.class)
@Implements(@Interface(iface = ConfigActor.class, prefix = "config$"))
public abstract class ClientCommandSourceMixin implements ConfigActor, FabricClientCommandSource {

    @Shadow public abstract boolean hasPermissionLevel(int level);

    public Optional<UUID> config$uuid() {
        return Optional.of(this.getClient().getGameProfile().getId());
    }

    public boolean config$isOp(int level) {
        return this.hasPermissionLevel(level);
    }
}
