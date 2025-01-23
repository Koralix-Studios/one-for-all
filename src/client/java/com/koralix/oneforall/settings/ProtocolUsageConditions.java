package com.koralix.oneforall.settings;

import com.koralix.oneforall.utils.IntoText;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.text.Text;

public enum ProtocolUsageConditions implements IntoText {
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
    public static final ConfigValueAdapter.Command<ProtocolUsageConditions, String> COMMAND = ConfigValueAdapter.Command.enumOf(ProtocolUsageConditions.class);

    @Override
    public Text toText() {
        return Text.translatable("enum.oneforall.protocol_usage_conditions." + name().toLowerCase());
    }
}
