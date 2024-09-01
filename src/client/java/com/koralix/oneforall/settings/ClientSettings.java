package com.koralix.oneforall.settings;

import java.util.Objects;

@SettingsRegistry(id = "client_settings", env = SettingsRegistry.Env.CLIENT)
public class ClientSettings {
    public static final ConfigValue<ProtocolUsageConditions> PROTOCOL_USAGE_CONDITIONS = ConfigValue.of(ProtocolUsageConditions.Always)
            .test(Objects::nonNull)
            .build();
}
