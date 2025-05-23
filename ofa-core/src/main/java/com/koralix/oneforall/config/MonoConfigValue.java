package com.koralix.oneforall.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MonoConfigValue<V> extends ConfigValue<V> {
    @NotNull V value();
    void value(@Nullable V value);
}
