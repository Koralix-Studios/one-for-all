package com.koralix.oneforall.utils;

import net.fabricmc.loader.api.SemanticVersion;

public final class SemVer {
    private SemVer() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static SemanticVersion parse(String version) {
        try {
            return SemanticVersion.parse(version);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid semantic version: " + version, e);
        }
    }
}
