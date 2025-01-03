package com.koralix.oneforall.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum ProtocolUsageConditions {
    ALWAYS,
    ONLY_ENFORCED,
    NEVER;

    public static final Codec<ProtocolUsageConditions> CODEC = Codec.BYTE.comapFlatMap(
            i -> {
                ProtocolUsageConditions[] values = ProtocolUsageConditions.values();
                return i >= 0 && i < values.length
                        ? DataResult.success(values[i])
                        : DataResult.error(() -> "Invalid protocol usage condition: " + i);
            },
            protocolUsageConditions -> (byte) protocolUsageConditions.ordinal()
    );
}
