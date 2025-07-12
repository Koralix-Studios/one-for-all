package com.koralix.oneforall.util;

import com.koralix.oneforall.config.ConfigRegistry;
import com.koralix.oneforall.config.backend.ConfigBundle;
import com.mojang.serialization.*;
import net.fabricmc.loader.api.Version;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class CustomCodecs {
    public static final Codec<Version> VERSION = Codec.STRING.xmap(
            Functions.tryCatch(Version::parse),
            Version::getFriendlyString
    );

    @Contract("_ -> new")
    public static <T> @NotNull Codec<T> error(String error) {
        return Codec.of(
                Encoder.error(error),
                Decoder.error(error)
        );
    }
}
