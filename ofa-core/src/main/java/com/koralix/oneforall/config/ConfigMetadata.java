package com.koralix.oneforall.config;

import net.fabricmc.loader.api.Version;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;

public class ConfigMetadata {
    private final NavigableMap<Version, Identifier> ids;

    public ConfigMetadata(@NotNull Map<Version, Identifier> ids) {
        if (ids.isEmpty()) {
            throw new IllegalArgumentException("Metadata must contain at least one version and identifier pair.");
        }
        this.ids = new TreeMap<>(ids);
    }

    @SafeVarargs
    public ConfigMetadata(Map.Entry<Version, Identifier>... ids) {
        this(Map.ofEntries(ids));
    }

    public Version version() {
        return ids.lastKey();
    }

    public Identifier id() {
        return ids.lastEntry().getValue();
    }

    public Identifier id(Version version) {
        return ids.floorEntry(version).getValue();
    }

    public void forEachId(BiConsumer<Version, Identifier> action) {
        ids.forEach(action);
    }
}
