package com.koralix.oneforall.config;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MonoConfigValue<T, B extends ByteBuf, S> extends ConfigValue<T, B, S> {
    @NotNull T value();
    @NotNull ConfigResult<T> value(@Nullable T value);
    @NotNull ConfigResult<T> value(@NotNull ConfigActor actor);
    @NotNull ConfigResult<T> value(@NotNull ConfigActor actor, @Nullable T value);
    void onChange(@NotNull MonoConfigObserver<T> observer);

    @FunctionalInterface
    interface MonoConfigObserver<T> {
        void onChange(@NotNull MonoConfigValue<T, ?, ?> configValue, @Nullable T oldValue, @Nullable T newValue);
    }
}
