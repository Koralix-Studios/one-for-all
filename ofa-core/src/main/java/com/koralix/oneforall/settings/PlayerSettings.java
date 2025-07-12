package com.koralix.oneforall.settings;

import com.koralix.oneforall.CoreInit;
import com.koralix.oneforall.config.ConfigCodec;
import com.koralix.oneforall.config.impl.PlayerConfigValue;
import com.koralix.oneforall.lang.Language;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Optional;

public class PlayerSettings {
    public static final PlayerConfigValue<Optional<Language>, ByteBuf> DEFAULT_LANGUAGE = PlayerConfigValue.create(
                    "0.1.0",
                    CoreInit.id("default_language"),
                    Optional.empty(),
                    ConfigCodec.of(Language.CODEC.optionalFieldOf("value").codec(), PacketCodecs.optional(Language.PACKET_CODEC), Language.COMMAND_ADAPTER.optional())
            )
            .build();
}
