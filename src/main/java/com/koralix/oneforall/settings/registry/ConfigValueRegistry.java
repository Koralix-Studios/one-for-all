package com.koralix.oneforall.settings.registry;

import com.koralix.oneforall.settings.ConfigValue;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigValueRegistry {
    private final Identifier id;
    private final Map<String, ConfigValue<?>> registry = new HashMap<>();
    private boolean frozen = false;

    public ConfigValueRegistry(Identifier id) {
        this.id = id;
    }

    public Identifier id() {
        return id;
    }

    public Identifier id(String id) {
        String modid = this.id.getNamespace();
        String path = this.id.getPath();
        return new Identifier(modid + "." + path, id);
    }

    public void register(String id, ConfigValue<?> value) {
        if (frozen) {
            throw new IllegalStateException("Registry is frozen");
        }
        if (registry.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate key: " + id);
        }
        registry.put(id, value);
    }

    public void freeze() {
        frozen = true;
    }

    public void forEach(Consumer<ConfigValueEntry<?>> action) {
        registry.forEach((id, value) -> action.accept(new ConfigValueEntry<>(this, id, value)));
    }
}
