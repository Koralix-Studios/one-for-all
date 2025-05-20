package com.koralix.oneforall.config;

import org.jetbrains.annotations.NotNull;

public interface ConfigValue<K, V> {
    @NotNull V get(K key);
}
