package com.koralix.oneforall.config.loader;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.ConfigValue;
import com.koralix.oneforall.config.registry.ConfigKey;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ConfigLoader {
    private final Set<ConfigKey> keys = new HashSet<>();
    private boolean dirty = false;

    private static ConfigValue<?, ?, ?> getConfigValue(@NotNull ConfigKey key) {
        return ConfigRegistry.get(key).orElseThrow(() -> new IllegalArgumentException("Config value not found for key: " + key));
    }

    protected abstract @Nullable ConfigStorage loadStorage() throws IOException;

    protected abstract void saveStorage(@NotNull ConfigStorage storage) throws IOException;

    public final void load() throws IOException {
        ConfigStorage storage = this.loadStorage();
        if (storage == null) return;

        storage.entries.forEach((key, o) -> {
            ConfigValue<?, ?, ?> configValue = ConfigRegistry.get(key).orElseThrow();
            load(configValue, o);
            this.add(configValue);
        });
    }

    @SuppressWarnings("unchecked")
    private <S> void load(@NotNull ConfigValue<?, ?, S> configValue, @NotNull Object value) {
        S saveData = (S) value;
        configValue.loadData(saveData);
    }

    public final void save() throws IOException {
        if (!this.dirty) return;
        this.saveStorage(this.createStorage());
        this.dirty = false;
    }

    public void add(@NotNull ConfigValue<?, ?, ?> configValue) {
        this.keys.add(configValue.key());
        configValue.onChange(s -> {
            if (!this.keys.contains(configValue.key())) return false;
            this.dirty = true;
            try {
                this.save();
            } catch (IOException e) {
                OneForAll.logger().error(
                        "Failed to save config value: {}",
                        configValue.key(),
                        e
                );
            }
            return true;
        });
    }

    public void remove(@NotNull ConfigValue<?, ?, ?> configValue) {
        this.keys.remove(configValue.key());
    }

    public final @NotNull ConfigStorage createStorage() {
        List<ConfigValue<?, ?, ?>> configValues = new ArrayList<>();
        this.keys.stream().map(ConfigLoader::getConfigValue).forEach(configValues::add);
        return ConfigStorage.create(configValues);
    }
}
