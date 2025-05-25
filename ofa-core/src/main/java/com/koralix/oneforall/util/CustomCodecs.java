package com.koralix.oneforall.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import net.fabricmc.loader.api.Version;

public class CustomCodecs {
    public static final Codec<Version> VERSION = Codec.STRING.xmap(
            Functions.tryCatch(Version::parse),
            Version::getFriendlyString
    );

    public static <T> Codec<T> error(String error) {
        return Codec.of(
                Encoder.error(error),
                Decoder.error(error)
        );
    }
}
