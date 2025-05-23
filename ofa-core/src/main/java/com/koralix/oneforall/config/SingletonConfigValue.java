package com.koralix.oneforall.config;

import com.koralix.oneforall.config.registry.ConfigEntry;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class SingletonConfigValue<V> implements MonoConfigValue<V> {
    private final @NotNull ConfigEntry<V> entry;
    private final @NotNull V nominal;
    private final @NotNull Codec<V> codec;
    private final @NotNull PacketCodec<? extends ByteBuf, V> packetCodec;
    private V value;

    public SingletonConfigValue(
            @NotNull Function<MonoConfigValue<V>, ConfigEntry<V>> registerFn,
            @NotNull V nominal,
            @NotNull Codec<V> codec,
            @NotNull PacketCodec<? extends ByteBuf, V> packetCodec
    ) {
        this.entry = registerFn.apply(this);
        this.nominal = nominal;
        this.codec = codec;
        this.packetCodec = packetCodec;
        this.value = null;
    }

    @Override
    public @NotNull V value() {
        return this.value == null ? this.nominal : this.value;
    }

    @Override
    public void value(@Nullable V value) {
        this.value = value;
    }

    @Override
    public @NotNull ConfigEntry<V> entry() {
        return this.entry;
    }

    @Override
    public @NotNull V nominal() {
        return this.nominal;
    }

    @Override
    public @NotNull Codec<V> codec() {
        return this.codec;
    }

    @Override
    public @NotNull PacketCodec<? extends ByteBuf, V> packetCodec() {
        return this.packetCodec;
    }
}
