package com.koralix.oneforall.settings;

import com.koralix.oneforall.lang.Language;

import java.util.Objects;

@SettingsRegistry(id = "server_settings", env = SettingsRegistry.Env.SERVER)
public final class ServerSettings {
    private ServerSettings() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static final ConfigValue<Boolean> PROTOCOL_ENABLED = ConfigValue.of(true)
            .test(Objects::nonNull)
            .permission(source -> source.hasPermissionLevel(4))
            .build();

    public static final ConfigValue<Boolean> ENFORCE_PROTOCOL = ConfigValue.of(false)
            .test(Objects::nonNull)
            .permission(source -> source.hasPermissionLevel(4))
            .build();

    public static final ConfigValue<Language> DEFAULT_LANGUAGE = ConfigValue.of(Language.SPANISH)
            .permission(source -> source.hasPermissionLevel(4))
            .build();

    public static final ConfigValue<Boolean> CAREFUL_BREAK = ConfigValue.of(false)
            .test(Objects::nonNull)
            .permission(source -> source.hasPermissionLevel(4))
            .build();
}
