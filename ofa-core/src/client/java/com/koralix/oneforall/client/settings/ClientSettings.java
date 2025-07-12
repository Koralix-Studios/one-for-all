package com.koralix.oneforall.client.settings;

import com.koralix.oneforall.CoreInit;
import com.koralix.oneforall.client.config.impl.ClientConfigValue;
import com.koralix.oneforall.config.ConfigCodec;
import io.netty.buffer.ByteBuf;

public class ClientSettings {
    public static final ClientConfigValue<ProtocolUsageCondition, ByteBuf> PROTOCOL_USAGE_CONDITION = ClientConfigValue.create(
                    "0.1.0",
                    CoreInit.id("protocol_enabled"),
                    ProtocolUsageCondition.ALWAYS,
                    ConfigCodec.of(ProtocolUsageCondition.CODEC, ProtocolUsageCondition.PACKET_CODEC, ProtocolUsageCondition.COMMAND_ADAPTER)
            )
            .build();
}
