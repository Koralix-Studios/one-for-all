package com.koralix.oneforall.mixin.config.backend;

import com.koralix.oneforall.config.ConfigValue;
import com.koralix.oneforall.config.backend.ConfigBackend;
import com.koralix.oneforall.config.backend.ConfigBundle;
import com.koralix.oneforall.config.impl.PlayerConfigValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ServerPlayerEntity.class)
@Implements(@Interface(iface = ConfigBackend.class, prefix = "config$"))
public abstract class ServerPlayerEntityMixin implements ConfigBackend<ServerPlayerEntity> {
    @Unique
    private ConfigBundle<ServerPlayerEntity> config$bundle;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initConfigBundle(
            MinecraftServer server,
            ServerWorld world,
            GameProfile profile,
            SyncedClientOptions clientOptions,
            CallbackInfo ci
    ) throws IOException {
        this.config$bundle = PlayerConfigValue.REGISTRY.load((ServerPlayerEntity) (Object) this);
    }

    public <T, B> T config$get(@NotNull ConfigValue<ServerPlayerEntity, T, B> configValue) {
        return config$bundle.get(configValue);
    }

    public <T, B> void config$set(@NotNull ConfigValue<ServerPlayerEntity, T, B> configValue, @Nullable T value) {
        config$bundle.set(configValue, value);
    }

    public @NotNull ConfigBundle<ServerPlayerEntity> config$bundle() {
        return config$bundle;
    }
}
