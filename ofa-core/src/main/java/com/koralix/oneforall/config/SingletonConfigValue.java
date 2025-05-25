package com.koralix.oneforall.config;

import com.koralix.oneforall.config.registry.ConfigEntry;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class SingletonConfigValue<T, B extends ByteBuf> implements MonoConfigValue<T, B> {
    private final @NotNull ConfigEntry<T> entry;
    private final @NotNull T nominal;
    private final @NotNull Codec<T> codec;
    private final @NotNull PacketCodec<B, T> packetCodec;
    private final @NotNull ConfigTest<T> test;
    private T value;

    public SingletonConfigValue(
            @NotNull Function<MonoConfigValue<T, B>, ConfigEntry<T>> registerFn,
            @NotNull T nominal,
            @NotNull Codec<T> codec,
            @NotNull PacketCodec<B, T> packetCodec,
            @NotNull ConfigTest<T> test
    ) {
        this.entry = registerFn.apply(this);
        this.nominal = nominal;
        this.codec = codec;
        this.packetCodec = packetCodec;
        this.test = test;
        this.value = null;
    }

    @Override
    public @NotNull T value() {
        return this.value == null ? this.nominal : this.value;
    }

    @Override
    public void value(@Nullable T value) {
        this.value = value;
    }

    @Override
    public @NotNull ConfigEntry<T> entry() {
        return this.entry;
    }

    @Override
    public @NotNull T nominal() {
        return this.nominal;
    }

    @Override
    public @NotNull Codec<T> codec() {
        return this.codec;
    }

    @Override
    public @NotNull PacketCodec<B, T> packetCodec() {
        return this.packetCodec;
    }
}
