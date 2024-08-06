package com.koralix.oneforall.settings;

import java.util.Objects;

@SettingsRegistry(id = "server_settings")
public final class ServerSettings {
    private ServerSettings() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static final ConfigValue<Boolean> PROTOCOL_ENABLED = ConfigValue.of(true)
            .test(Objects::nonNull)
            .build();

    public static final ConfigValue<Boolean> ENFORCE_PROTOCOL = ConfigValue.of(true)
            .test(Objects::nonNull)
            .build();
}
