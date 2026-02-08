package com.koralix.oneforall.settings;

import com.koralix.oneforall.CoreInit;
import com.koralix.oneforall.config.ConfigCodec;
import com.koralix.oneforall.config.impl.ServerConfigValue;
import com.koralix.oneforall.lang.Language;
import io.netty.buffer.ByteBuf;

public class ServerSettings {
    public static final ServerConfigValue<Boolean, ByteBuf> PROTOCOL_ENABLED = ServerConfigValue.create(
                    "0.1.0",
                    CoreInit.id("protocol_enabled"),
                    true,
                    ConfigCodec.BOOLEAN
            )
            .write(actor -> actor.isOp(4))
            .build();

    public static final ServerConfigValue<Boolean, ByteBuf> ENFORCE_PROTOCOL = ServerConfigValue.create(
                    "0.1.0",
                    CoreInit.id("enforce_protocol"),
                    false,
                    ConfigCodec.BOOLEAN
            )
            .write(actor -> actor.isOp(4))
            .build();

    public static final ServerConfigValue<Language, ByteBuf> DEFAULT_LANGUAGE = ServerConfigValue.create(
                    "0.1.0",
                    CoreInit.id("default_language"),
                    Language.ENGLISH,
                    ConfigCodec.of(Language.CODEC, Language.PACKET_CODEC, Language.COMMAND_ADAPTER)
            )
            .write(actor -> actor.isOp(4))
            .build();
}
