package com.koralix.oneforall.config;

import com.koralix.oneforall.config.registry.ConfigEntry;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

public interface ConfigValue<T, B extends ByteBuf> {
    @NotNull ConfigEntry<T> entry();
    @NotNull T nominal();
    @NotNull Codec<T> codec();
    @NotNull PacketCodec<B, T> packetCodec();
}
