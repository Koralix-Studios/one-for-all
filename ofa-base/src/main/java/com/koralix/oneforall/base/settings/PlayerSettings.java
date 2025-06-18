package com.koralix.oneforall.base.settings;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.PlayerConfigValue;
import com.koralix.oneforall.config.adapter.CommandAdapter;
import com.koralix.oneforall.config.registry.ConfigRegistrar;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.koralix.oneforall.config.registry.VersionedIdentifier;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayerSettings {
    private static final ConfigRegistrar REGISTRAR = ConfigRegistry.builder("0.1.0", OneForAll.id("player_settings")).prepare();

    public static final PlayerConfigValue<SneakMode, ByteBuf> CAREFUL_BREAK = REGISTRAR
            .player(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("careful_break")),
                    SneakMode.NEVER,
                    SneakMode.CODEC,
                    SneakMode.PACKET_CODEC,
                    CommandAdapter.ofEnum(SneakMode.class),
                    a -> false,
                    a -> false
            )
            .test(Objects::nonNull)
            .build();

    public static final PlayerConfigValue<SneakMode, ByteBuf> XP_BAR_MENDING = REGISTRAR
            .player(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("xp_bar_mending")),
                    SneakMode.NEVER,
                    SneakMode.CODEC,
                    SneakMode.PACKET_CODEC,
                    CommandAdapter.ofEnum(SneakMode.class),
                    a -> false,
                    a -> false
            )
            .test(Objects::nonNull)
            .build();

    public static final PlayerConfigValue<SneakMode, ByteBuf> CREATIVE_KILL = REGISTRAR
            .player(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("creative_kill")),
                    SneakMode.NEVER,
                    SneakMode.CODEC,
                    SneakMode.PACKET_CODEC,
                    CommandAdapter.ofEnum(SneakMode.class),
                    a -> false,
                    a -> false
            )
            .test(Objects::nonNull)
            .build();


    public static @NotNull ConfigRegistry register() {
        return REGISTRAR.complete();
    }
}
