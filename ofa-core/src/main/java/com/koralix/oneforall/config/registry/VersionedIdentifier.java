package com.koralix.oneforall.config.registry;

import net.fabricmc.loader.api.Version;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record VersionedIdentifier(Version version, Identifier identifier) implements Comparable<VersionedIdentifier> {
    @Override
    public int compareTo(@NotNull VersionedIdentifier o) {
        return this.version.compareTo(o.version);
    }
}
