package com.koralix.oneforall.config;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record ConfigCodec<T, B>(
        Codec<T> codec,
        PacketCodec<B, T> packetCodec,
        ConfigCommandAdapter<T> commandAdapter
) {
    public static final ConfigCodec<Boolean, ByteBuf> BOOLEAN = of(
            Codec.BOOL,
            PacketCodecs.BOOLEAN,
            ConfigCommandAdapter.BOOLEAN
    );

    public static @NotNull ConfigCodec<Integer, ByteBuf> integer(int minValue, int maxValue) {
        return of(
                Codec.INT,
                PacketCodecs.INTEGER,
                ConfigCommandAdapter.integer(minValue, maxValue)
        );
    }

    @Contract("_, _, _ -> new")
    public static <T, B> @NotNull ConfigCodec<T, B> of(
            @NotNull Codec<T> codec,
            @NotNull PacketCodec<B, T> packetCodec,
            @NotNull ConfigCommandAdapter<T> commandAdapter
    ) {
        return new ConfigCodec<>(codec, packetCodec, commandAdapter);
    }
}
