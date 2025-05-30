package com.koralix.oneforall.client.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

public enum ProtocolUsageCondition {
    ALWAYS,
    ONLY_ENFORCED,
    NEVER;

    public static final @NotNull Codec<ProtocolUsageCondition> CODEC = Codec.BYTE.comapFlatMap(
            i -> {
                ProtocolUsageCondition[] values = ProtocolUsageCondition.values();
                return i >= 0 && i < values.length
                        ? DataResult.success(values[i])
                        : DataResult.error(() -> "Invalid protocol usage condition: " + i);
            },
            condition -> (byte) condition.ordinal()
    );

    public static final PacketCodec<ByteBuf, ProtocolUsageCondition> PACKET_CODEC = PacketCodecs.BYTE.xmap(
            i -> {
                ProtocolUsageCondition[] values = ProtocolUsageCondition.values();
                if (i >= 0 && i < values.length) return values[i];

                throw new IllegalArgumentException("Invalid protocol usage condition: " + i);
            },
            mode -> (byte) mode.ordinal()
    );

    public boolean isActive(boolean enforceProtocol) {
        return switch (this) {
            case ALWAYS -> true;
            case ONLY_ENFORCED -> enforceProtocol;
            case NEVER -> false;
        };
    }
}
