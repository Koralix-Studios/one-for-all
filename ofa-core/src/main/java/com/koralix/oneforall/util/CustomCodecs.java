package com.koralix.oneforall.util;

import com.mojang.serialization.Codec;
import net.fabricmc.loader.api.Version;

public class CustomCodecs {
    public static final Codec<Version> VERSION = Codec.STRING.xmap(
            Functions.tryCatch(Version::parse),
            Version::getFriendlyString
    );
}
