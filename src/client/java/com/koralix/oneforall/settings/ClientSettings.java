package com.koralix.oneforall.settings;

import java.util.Objects;

public class ClientSettings {
    public static final SingletonConfigValue<ProtocolUsageConditions> PROTOCOL_USAGE_CONDITIONS = ConfigValue.singleton(ProtocolUsageConditions.ALWAYS, ProtocolUsageConditions.CODEC)
            .test(Objects::nonNull)
            .build();
}
