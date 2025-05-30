package com.koralix.oneforall.settings;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.MonoConfigValue;
import com.koralix.oneforall.config.registry.ConfigRegistrar;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.koralix.oneforall.config.registry.VersionedIdentifier;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ServerSettings {
    private static final ConfigRegistrar REGISTRAR = ConfigRegistry.builder("0.1.0", OneForAll.id("server_settings")).prepare();

    public static final MonoConfigValue<Boolean, ByteBuf> PROTOCOL_ENABLED = REGISTRAR
            .mono(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("protocol_enabled")),
                    true,
                    Codec.BOOL,
                    PacketCodecs.BOOLEAN
            )
            .test(Objects::nonNull)
            .build();

    public static final MonoConfigValue<Boolean, ByteBuf> ENFORCE_PROTOCOL = REGISTRAR
            .mono(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("enforce_protocol")),
                    false,
                    Codec.BOOL,
                    PacketCodecs.BOOLEAN
            )
            .test(Objects::nonNull)
            .build();

    public static @NotNull ConfigRegistry register() {
        return REGISTRAR.complete();
    }
}
