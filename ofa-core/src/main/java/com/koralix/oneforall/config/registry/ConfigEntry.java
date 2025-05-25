package com.koralix.oneforall.config.registry;

import com.koralix.oneforall.config.ConfigValue;
import org.jetbrains.annotations.NotNull;

public record ConfigEntry<T>(ConfigKey key, ConfigValue<T, ?> configValue) {
    public @NotNull ConfigRegistry registry() {
        return ConfigRegistry.getConfigRegistry(key.registryId()).orElseThrow();
    }
}
