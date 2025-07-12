package com.koralix.oneforall.config.backend;

import com.koralix.oneforall.config.ConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConfigBackend<K> {
    @SuppressWarnings("unchecked")
    static <K> ConfigBackend<K> cast(@NotNull K object) {
        return (ConfigBackend<K>) object;
    }

    <T, B> T get(@NotNull ConfigValue<K, T, B> configValue);

    <T, B> void set(@NotNull ConfigValue<K, T, B> configValue, @Nullable T value);

    @NotNull ConfigBundle<K> bundle();
}
