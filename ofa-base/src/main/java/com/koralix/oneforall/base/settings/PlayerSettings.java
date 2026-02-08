package com.koralix.oneforall.base.settings;

import com.koralix.oneforall.base.BaseInit;
import com.koralix.oneforall.config.ConfigCodec;
import com.koralix.oneforall.config.impl.PlayerConfigValue;
import io.netty.buffer.ByteBuf;

public class PlayerSettings {
    public static final PlayerConfigValue<SneakMode, ByteBuf> CAREFUL_BREAK = PlayerConfigValue.create(
                    "0.1.0",
                    BaseInit.id("careful_break"),
                    SneakMode.NEVER,
                    ConfigCodec.of(SneakMode.CODEC, SneakMode.PACKET_CODEC, SneakMode.COMMAND_ADAPTER)
            )
            .build();

    public static final PlayerConfigValue<SneakMode, ByteBuf> XP_BAR_MENDING = PlayerConfigValue.create(
                    "0.1.0",
                    BaseInit.id("xp_bar_mending"),
                    SneakMode.NEVER,
                    ConfigCodec.of(SneakMode.CODEC, SneakMode.PACKET_CODEC, SneakMode.COMMAND_ADAPTER)
            )
            .build();

    public static final PlayerConfigValue<SneakMode, ByteBuf> CREATIVE_KILL = PlayerConfigValue.create(
                    "0.1.0",
                    BaseInit.id("creative_kill"),
                    SneakMode.NEVER,
                    ConfigCodec.of(SneakMode.CODEC, SneakMode.PACKET_CODEC, SneakMode.COMMAND_ADAPTER)
            )
            .build();
}
