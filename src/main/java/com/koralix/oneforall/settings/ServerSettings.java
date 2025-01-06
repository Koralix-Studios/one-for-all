package com.koralix.oneforall.settings;

import com.koralix.oneforall.lang.Language;
import com.mojang.serialization.Codec;

import java.util.Objects;

public final class ServerSettings {
    private ServerSettings() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static final SingletonConfigValue<Boolean> PROTOCOL_ENABLED = ConfigValue.singleton(true, Codec.BOOL)
            .test(Objects::nonNull)
            .permission(source -> source.hasPermissionLevel(4))
            .build();

    public static final SingletonConfigValue<Boolean> ENFORCE_PROTOCOL = ConfigValue.singleton(false, Codec.BOOL)
            .test(Objects::nonNull)
            .permission(source -> source.hasPermissionLevel(4))
            .build();

    public static final SingletonConfigValue<Language> DEFAULT_LANGUAGE = ConfigValue.singleton(Language.SPANISH, Language.CODEC)
            .permission(source -> source.hasPermissionLevel(4))
            .build();

    public static final SingletonConfigValue<Boolean> CAREFUL_BREAK = ConfigValue.singleton(false, Codec.BOOL)
            .test(Objects::nonNull)
            .permission(source -> source.hasPermissionLevel(4))
            .build();
}
