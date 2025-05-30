package com.koralix.oneforall.config;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface MultiConfigValue<K, T, B extends ByteBuf> extends ConfigValue<T, B> {
    @NotNull T value(K key);
    @NotNull ConfigResult<T> value(@NotNull K key, @Nullable T value);
    @NotNull ConfigResult<T> value(@NotNull ConfigActor actor, @NotNull K key);
    @NotNull ConfigResult<T> value(@NotNull ConfigActor actor, @NotNull K key, @Nullable T value);
    @NotNull T defaultValue();
    @NotNull ConfigResult<T> defaultValue(@NotNull T defaultValue);
    @NotNull ConfigResult<T> defaultValue(@NotNull ConfigActor actor, @NotNull T defaultValue);
    @NotNull Codec<K> keyCodec();
    @NotNull Map<K, T> valueMap();
    void onChange(@NotNull MultiConfigObserver<K, T> observer);

    @FunctionalInterface
    interface MultiConfigObserver<K, T> {
        void onChange(@NotNull MultiConfigValue<K, T, ?> configValue, @Nullable K key, @Nullable T oldValue, @Nullable T newValue);
    }
}
