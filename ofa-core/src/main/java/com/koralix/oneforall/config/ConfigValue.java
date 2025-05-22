package com.koralix.oneforall.config;

import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

public interface ConfigValue<V> {
    @NotNull V nominal();
    @NotNull Codec<V> codec();
    @NotNull PacketCodec<PacketByteBuf, V> packetCodec();
}
