package com.koralix.oneforall.settings.registry;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record ConfigValueKey(Identifier registry, String id) {
    public ConfigValueKey {
        if (registry == null) {
            throw new IllegalArgumentException("Registry cannot be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        if (registry.getNamespace().contains(".")) {
            throw new IllegalArgumentException("Registry namespace cannot contain '.'");
        }
        if (registry.getPath().contains(".")) {
            throw new IllegalArgumentException("Sub-registries are not supported");
        }
        if (id.contains(".")) {
            throw new IllegalArgumentException("Value cannot contain '.'");
        }
    }

    public static @NotNull ConfigValueKey from(Identifier identifier) {
        String namespace = identifier.getNamespace();
        int index = namespace.indexOf(".");
        if (index == -1) throw new IllegalArgumentException("Invalid registry identifier");
        String modid = namespace.substring(0, index);
        String path = namespace.substring(index + 1);
        return new ConfigValueKey(new Identifier(modid, path), identifier.getPath());
    }

    public static @NotNull ConfigValueKey from(String key) {
        return from(new Identifier(key));
    }

    public @NotNull Identifier asIdentifier() {
        String namespace = registry().getNamespace() + "." + registry().getPath();
        return new Identifier(namespace, id);
    }

    @Override
    public String toString() {
        return asIdentifier().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ConfigValueKey that = (ConfigValueKey) o;
        return Objects.equals(id, that.id) && Objects.equals(registry, that.registry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registry, id);
    }
}
