package com.koralix.oneforall.config;

import com.koralix.oneforall.config.registry.ConfigEntry;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractConfigValue<T, B extends ByteBuf, O> implements ConfigValue<T, B> {
    protected final @NotNull ConfigEntry<T> entry;
    protected final @NotNull T nominal;
    protected final @NotNull Codec<T> codec;
    protected final @NotNull PacketCodec<B, T> packetCodec;
    protected final @NotNull ConfigTest<T> test;
    protected final @NotNull List<O> observers = new ArrayList<>();

    public AbstractConfigValue(
            @NotNull Function<ConfigValue<T, B>, ConfigEntry<T>> registerFn,
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

    public void onChange(@NotNull O observer) {
        this.observers.add(observer);
    }
}
