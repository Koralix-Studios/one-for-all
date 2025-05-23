package com.koralix.oneforall.config.registry;

import net.fabricmc.loader.api.Version;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ConfigRegistryBuilder {
    private final List<VersionedIdentifier> ids = new ArrayList<>();

    ConfigRegistryBuilder(VersionedIdentifier id) {
        this.ids.add(id);
    }

    ConfigRegistryBuilder(Version version, Identifier id) {
        this(new VersionedIdentifier(version, id));
    }

    public ConfigRegistryBuilder id(VersionedIdentifier id) {
        if (this.ids.getLast().compareTo(id) >= 0) {
            throw new IllegalArgumentException("VersionedIdentifier must be in ascending order");
        }
        this.ids.add(id);
        return this;
    }

    public ConfigRegistryBuilder id(Version version, Identifier id) {
        return id(new VersionedIdentifier(version, id));
    }

    public ConfigRegistrar prepare() {
        return new ConfigRegistrar(ids);
    }
}
