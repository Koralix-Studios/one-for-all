package com.koralix.oneforall.config.registry;

import net.fabricmc.loader.api.Version;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class VersionedIdentifierMap<T> {
    private final NavigableMap<Version, Map<Identifier, T>> map;

    @Contract(" -> new")
    public static <T> @NotNull VersionedIdentifierMap<T> create() {
        return new VersionedIdentifierMap<>(new TreeMap<>());
    }

    private VersionedIdentifierMap(NavigableMap<Version, Map<Identifier, T>> map) {
        this.map = map;
    }

    public Optional<T> get(Version version, Identifier id) {
        Version key = this.map.floorKey(version);
        if (key == null) return Optional.empty();
        Map<Identifier, T> subMap = this.map.get(key);
        if (subMap == null) return Optional.empty();
        return Optional.ofNullable(subMap.get(id));
    }

    public void put(Version version, Identifier id, T value) {
        this.map.computeIfAbsent(version, k -> new HashMap<>()).put(id, value);
    }
}
