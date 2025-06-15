package com.koralix.oneforall.settings;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.PlayerConfigValue;
import com.koralix.oneforall.config.adapter.CommandAdapter;
import com.koralix.oneforall.config.registry.ConfigRegistrar;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.koralix.oneforall.config.registry.VersionedIdentifier;
import com.koralix.oneforall.lang.Language;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class PlayerSettings {
    private static final ConfigRegistrar REGISTRAR = ConfigRegistry.builder("0.1.0", OneForAll.id("player_settings")).prepare();

    public static final PlayerConfigValue<Optional<Language>, ByteBuf> DEFAULT_LANGUAGE = REGISTRAR
            .player(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("default_language")),
                    Optional.empty(),
                    Language.CODEC.optionalFieldOf("value").codec(),
                    PacketCodecs.optional(Language.PACKET_CODEC),
                    CommandAdapter.ofEnum(Language.class).optional(),
                    a -> false,
                    a -> false
            )
            .test(Objects::nonNull)
            .build();

    public static @NotNull ConfigRegistry register() {
        return REGISTRAR.complete();
    }
}
