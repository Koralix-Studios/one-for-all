package com.koralix.oneforall.settings;

import com.koralix.oneforall.utils.SemVer;
import com.mojang.serialization.Codec;
import net.minecraft.client.MinecraftClient;

import java.util.Objects;

public class ClientSettings {
    public static final SingletonConfigValue<ProtocolUsageConditions> PROTOCOL_USAGE_CONDITIONS = ConfigValue.singleton(ProtocolUsageConditions.ALWAYS, ProtocolUsageConditions.CODEC, ProtocolUsageConditions.COMMAND, SemVer.parse("0.1.0"))
            .test(Objects::nonNull)
            .build();

    public static final SingletonConfigValue<Boolean> CENTERED_FLOWERS = ConfigValue.singleton(false, Codec.BOOL, ConfigValueAdapter.Command.BOOLEAN, SemVer.parse("0.1.0"))
            .test(Objects::nonNull)
            .build();

    static {
        CENTERED_FLOWERS.onChange().register(change -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.worldRenderer.reload();
        });
    }

    public static final SingletonConfigValue<Boolean> DONT_CONSUME_FIREWORKS = ConfigValue.singleton(false, Codec.BOOL, ConfigValueAdapter.Command.BOOLEAN, SemVer.parse("0.1.0"))
            .test(Objects::nonNull)
            .build();
}
