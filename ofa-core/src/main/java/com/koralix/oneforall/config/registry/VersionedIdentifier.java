package com.koralix.oneforall.config.registry;

import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record VersionedIdentifier(Version version, Identifier identifier) implements Comparable<VersionedIdentifier> {

    @Contract("_, _ -> new")
    public static @NotNull VersionedIdentifier of(@NotNull String version, @NotNull Identifier identifier) {
        try {
            return new VersionedIdentifier(Version.parse(version), identifier);
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int compareTo(@NotNull VersionedIdentifier o) {
        return this.version.compareTo(o.version);
    }
}
