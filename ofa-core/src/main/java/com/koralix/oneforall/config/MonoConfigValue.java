package com.koralix.oneforall.config;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MonoConfigValue<T, B extends ByteBuf> extends ConfigValue<T, B> {
    @NotNull T value();
    void value(@Nullable T value);
}
