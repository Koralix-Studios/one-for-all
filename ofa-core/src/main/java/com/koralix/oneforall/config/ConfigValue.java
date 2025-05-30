package com.koralix.oneforall.config;

import com.koralix.oneforall.config.registry.ConfigEntry;
import com.koralix.oneforall.config.registry.ConfigKey;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ConfigValue<T, B extends ByteBuf, S> {
    @NotNull ConfigEntry<T> entry();
    default @NotNull ConfigKey key() {
        return this.entry().key();
    }
    @NotNull T nominal();
    @NotNull Codec<T> codec();
    @NotNull PacketCodec<B, T> packetCodec();
    @NotNull Codec<S> saveCodec();
    void loadData(@NotNull S data);
    @NotNull Optional<S> saveData();
    void onChange(@NotNull Function<S, Boolean> observer);
    void onChange(@NotNull Consumer<S> observer);
}
