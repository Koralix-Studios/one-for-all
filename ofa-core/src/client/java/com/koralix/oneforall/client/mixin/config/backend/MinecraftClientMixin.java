package com.koralix.oneforall.client.mixin.config.backend;


import com.koralix.oneforall.client.config.impl.ClientConfigValue;
import com.koralix.oneforall.config.ConfigValue;
import com.koralix.oneforall.config.backend.ConfigBackend;
import com.koralix.oneforall.config.backend.ConfigBundle;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;

@Mixin(MinecraftClient.class)
@Implements(@Interface(iface = ConfigBackend.class, prefix = "config$"))
public abstract class MinecraftClientMixin implements ConfigBackend<MinecraftClient> {
    @Unique
    private final ConfigBundle<MinecraftClient> config$bundle = ClientConfigValue.REGISTRY.load((MinecraftClient) (Object) this);

    protected MinecraftClientMixin() throws IOException {
    }

    public <T, B> T config$get(@NotNull ConfigValue<MinecraftClient, T, B> configValue) {
        return config$bundle.get(configValue);
    }

    public <T, B> void config$set(@NotNull ConfigValue<MinecraftClient, T, B> configValue, @Nullable T value) {
        config$bundle.set(configValue, value);
    }

    public @NotNull ConfigBundle<MinecraftClient> config$bundle() {
        return config$bundle;
    }
}
