package com.koralix.oneforall.settings;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.MonoConfigValue;
import com.koralix.oneforall.config.adapter.CommandAdapter;
import com.koralix.oneforall.config.registry.ConfigRegistrar;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.koralix.oneforall.config.registry.VersionedIdentifier;
import com.koralix.oneforall.lang.Language;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ServerSettings {
    private static final ConfigRegistrar REGISTRAR = ConfigRegistry.builder("0.1.0", OneForAll.id("server_settings")).prepare();

    public static final MonoConfigValue<Boolean, ByteBuf, ?> PROTOCOL_ENABLED = REGISTRAR
            .mono(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("protocol_enabled")),
                    true,
                    Codec.BOOL,
                    PacketCodecs.BOOLEAN,
                    CommandAdapter.bool()
            )
            .test(Objects::nonNull)
            .build();

    public static final MonoConfigValue<Boolean, ByteBuf, ?> ENFORCE_PROTOCOL = REGISTRAR
            .mono(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("enforce_protocol")),
                    false,
                    Codec.BOOL,
                    PacketCodecs.BOOLEAN,
                    CommandAdapter.bool()
            )
            .test(Objects::nonNull)
            .build();

    public static final MonoConfigValue<Language, ByteBuf, ?> DEFAULT_LANGUAGE = REGISTRAR
            .mono(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("default_language")),
                    Language.ENGLISH,
                    Language.CODEC,
                    Language.PACKET_CODEC,
                    CommandAdapter.ofEnum(Language.class)
            )
            .test(Objects::nonNull)
            .build();

    public static @NotNull ConfigRegistry register() {
        return REGISTRAR.complete();
    }
}
