package com.koralix.oneforall.settings.registry;

import com.koralix.oneforall.settings.ConfigValue;
import net.minecraft.util.Identifier;

public record ConfigValueEntry<T>(ConfigValueRegistry registry, String id, ConfigValue<T> configValue) {
    public ConfigValueEntry {
        if (registry == null) {
            throw new IllegalArgumentException("Registry cannot be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (configValue == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        if (id.contains(".")) {
            throw new IllegalArgumentException("Key cannot contain '.'");
        }
    }

    public ConfigValueKey key() {
        Identifier registry = this.registry().id();
        return new ConfigValueKey(registry, id);
    }
}
