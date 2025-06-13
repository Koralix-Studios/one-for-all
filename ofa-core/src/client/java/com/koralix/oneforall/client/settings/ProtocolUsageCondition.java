package com.koralix.oneforall.client.settings;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum ProtocolUsageCondition implements StringIdentifiable {
    ALWAYS,
    ONLY_ENFORCED,
    NEVER;

    public static final @NotNull Codec<ProtocolUsageCondition> CODEC = StringIdentifiable.createCodec(ProtocolUsageCondition::values);
    private static final IntFunction<ProtocolUsageCondition> BY_ID = ValueLists.createIndexToValueFunction(
            ProtocolUsageCondition::ordinal, values(), ValueLists.OutOfBoundsHandling.WRAP
    );
    public static final PacketCodec<ByteBuf, ProtocolUsageCondition> PACKET_CODEC = PacketCodecs.indexed(BY_ID, ProtocolUsageCondition::ordinal);

    public boolean isActive(boolean enforceProtocol) {
        return switch (this) {
            case ALWAYS -> true;
            case ONLY_ENFORCED -> enforceProtocol;
            case NEVER -> false;
        };
    }

    @Override
    public @NotNull String asString() {
        return this.name().toLowerCase();
    }
}
