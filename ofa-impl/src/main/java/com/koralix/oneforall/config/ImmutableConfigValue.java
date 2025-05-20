package com.koralix.oneforall.config;

import org.jetbrains.annotations.NotNull;

public class ImmutableConfigValue<K, T> implements ConfigValue<K, T> {
    private final T value;

    public ImmutableConfigValue(T value) {
        this.value = value;
    }

    @Override
    public @NotNull T get(K key) {
        return this.value;
    }
}
