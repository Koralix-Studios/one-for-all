package com.koralix.oneforall.config.registry;

import net.fabricmc.loader.api.Version;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class VersionedIdentifierMap<T> {
    private final NavigableMap<Version, Map<Identifier, T>> map;
    private boolean frozen = false;

    private VersionedIdentifierMap(NavigableMap<Version, Map<Identifier, T>> map) {
        this.map = map;
    }

    @Contract(" -> new")
    public static <T> @NotNull VersionedIdentifierMap<T> create() {
        return new VersionedIdentifierMap<>(new TreeMap<>());
    }

    public Optional<T> get(Version version, Identifier id) {
        Version key = this.map.floorKey(version);
        if (key == null) return Optional.empty();
        Map<Identifier, T> subMap = this.map.get(key);
        if (subMap == null) return Optional.empty();
        return Optional.ofNullable(subMap.get(id));
    }

    public void put(Version version, Identifier id, T value) {
        if (this.frozen) {
            throw new IllegalStateException("Cannot modify a frozen VersionedIdentifierMap");
        }
        this.map.computeIfAbsent(version, k -> new HashMap<>()).put(id, value);
    }

    public void putAll(@NotNull List<VersionedIdentifier> ids, T entry) {
        for (VersionedIdentifier id : ids) {
            this.put(id.version(), id.identifier(), entry);
        }
    }

    public @NotNull VersionedIdentifierMap<T> freeze() {
        this.frozen = true;
        return this;
    }

    public @NotNull VersionedIdentifierMap<T> frozenCopy() {
        NavigableMap<Version, Map<Identifier, T>> map = this.map.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), Map.copyOf(e.getValue())))
                .collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);

        return new VersionedIdentifierMap<>(Collections.unmodifiableNavigableMap(map)).freeze();
    }
}
