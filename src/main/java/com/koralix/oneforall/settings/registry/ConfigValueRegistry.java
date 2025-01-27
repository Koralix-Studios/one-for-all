package com.koralix.oneforall.settings.registry;

import com.koralix.oneforall.settings.AbstractConfigValue;
import com.koralix.oneforall.settings.ConfigValue;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigValueRegistry {
    private final Identifier id;
    private final ConfigValueEnvironment environment;
    private final Map<String, ConfigValue<?>> registry = new HashMap<>();
    private boolean frozen = false;

    public ConfigValueRegistry(Identifier id, ConfigValueEnvironment environment) {
        this.id = id;
        this.environment = environment;
    }

    public Identifier id() {
        return id;
    }

    public ConfigValueEnvironment environment() {
        return environment;
    }

    public Identifier id(String id) {
        String modid = this.id.getNamespace();
        String path = this.id.getPath();
        return new Identifier(modid + "." + path, id);
    }

    public <T> void register(String id, ConfigValue<T> configValue) {
        if (frozen) {
            throw new IllegalStateException("Registry is frozen");
        }
        if (registry.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate key: " + id);
        }
        ((AbstractConfigValue<T>) configValue).entry(new ConfigValueEntry<>(this, id, configValue));
        registry.put(id, configValue);
    }

    public void freeze() {
        frozen = true;
    }

    public void forEach(Consumer<ConfigValue<?>> action) {
        registry.values().forEach(action);
    }
}
