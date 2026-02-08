package com.koralix.oneforall.config;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConfigValue<K, T, B> {
    @NotNull ConfigRegistry<K> registry();

    @NotNull RegistryEntry.Reference<ConfigValue<K, T, B>> entry();

    @NotNull RegistryKey<ConfigValue<K, T, B>> key();

    @NotNull String translationKey();

    @NotNull Identifier id();

    @NotNull ConfigMetadata metadata();

    @NotNull T nominalValue();

    @NotNull ConfigCodec<T, B> codec();

    @NotNull ConfigTest<T> test();

    @NotNull T get(@NotNull K backend);

    boolean set(@NotNull K backend, @Nullable T value);

    void onChange(@NotNull ConfigChangeListener<K, T, B> listener);

    @FunctionalInterface
    interface ConfigChangeListener<K, T, B> {
        void onConfigChange(@NotNull K backend, @NotNull ConfigValue<K, T, B> configValue, @NotNull T oldValue, @NotNull T newValue);
    }
}
