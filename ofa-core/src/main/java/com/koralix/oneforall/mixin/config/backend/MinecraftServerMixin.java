package com.koralix.oneforall.mixin.config.backend;

import com.koralix.oneforall.config.ConfigValue;
import com.koralix.oneforall.config.backend.ConfigBackend;
import com.koralix.oneforall.config.backend.ConfigBundle;
import com.koralix.oneforall.config.impl.ServerConfigValue;
import com.mojang.datafixers.DataFixer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.ApiServices;
import net.minecraft.world.level.storage.LevelStorage;
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
import java.net.Proxy;

@Mixin(MinecraftServer.class)
@Implements(@Interface(iface = ConfigBackend.class, prefix = "config$"))
public abstract class MinecraftServerMixin implements ConfigBackend<MinecraftServer> {
    @Unique
    private ConfigBundle<MinecraftServer> config$bundle;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initConfigBundle(
            Thread serverThread,
            LevelStorage.Session session,
            ResourcePackManager dataPackManager,
            SaveLoader saveLoader,
            Proxy proxy,
            DataFixer dataFixer,
            ApiServices apiServices,
            WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory,
            CallbackInfo ci
    ) throws IOException {
        this.config$bundle = ServerConfigValue.REGISTRY.load((MinecraftServer) (Object) this);
    }

    public <T, B> T config$get(@NotNull ConfigValue<MinecraftServer, T, B> configValue) {
        return config$bundle.get(configValue);
    }

    public <T, B> void config$set(@NotNull ConfigValue<MinecraftServer, T, B> configValue, @Nullable T value) {
        config$bundle.set(configValue, value);
    }

    public @NotNull ConfigBundle<MinecraftServer> config$bundle() {
        return config$bundle;
    }
}
