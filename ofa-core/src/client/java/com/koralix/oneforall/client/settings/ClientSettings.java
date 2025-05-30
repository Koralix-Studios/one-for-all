package com.koralix.oneforall.client.settings;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.MonoConfigValue;
import com.koralix.oneforall.config.registry.ConfigRegistrar;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.koralix.oneforall.config.registry.VersionedIdentifier;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ClientSettings {
    private static final ConfigRegistrar REGISTRAR = ConfigRegistry.builder("0.1.0", OneForAll.id("client_settings")).prepare();

    public static final MonoConfigValue<ProtocolUsageCondition, ByteBuf, ?> PROTOCOL_USAGE_CONDITION = REGISTRAR
            .mono(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("protocol_enabled")),
                    ProtocolUsageCondition.ALWAYS,
                    ProtocolUsageCondition.CODEC,
                    ProtocolUsageCondition.PACKET_CODEC
            )
            .test(Objects::nonNull)
            .build();

    public static @NotNull ConfigRegistry register() {
        return REGISTRAR.complete();
    }
}
