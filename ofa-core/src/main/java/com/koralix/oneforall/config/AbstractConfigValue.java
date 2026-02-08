package com.koralix.oneforall.config;

import com.koralix.oneforall.CoreInit;
import com.koralix.oneforall.config.backend.ConfigBackend;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public abstract class AbstractConfigValue<K, T, B> implements ConfigValue<K, T, B> {
    private final ConfigRegistry<K> registry;
    private final RegistryEntry.Reference<ConfigValue<K, T, B>> entry;
    private final ConfigMetadata metadata;
    private final T nominalValue;
    private final ConfigCodec<T, B> codec;
    private final ConfigTest<T> test;
    private final Event<ConfigChangeListener<K, T, B>> event = EventFactory.createArrayBacked(ConfigChangeListener.class, listeners -> (backend, configValue, oldValue, newValue) -> {
        for (ConfigChangeListener<K, T, B> listener : listeners) {
            listener.onConfigChange(backend, configValue, oldValue, newValue);
        }
    });

    public AbstractConfigValue(
            @NotNull ConfigRegistry<K> registry,
            @NotNull ConfigMetadata metadata,
            @NotNull T nominalValue,
            @NotNull ConfigCodec<T, B> codec,
            @NotNull ConfigTest<T> test
    ) {
        this.registry = registry;
        this.metadata = metadata;
        this.nominalValue = nominalValue;
        this.codec = codec;
        this.test = test;
        this.entry = registry.register(this);

        onChange((backend, configValue, oldValue, newValue) -> {
            try {
                registry.save(backend);
            } catch (IOException e) {
                CoreInit.logger().error("Failed to save config value: {}", configValue.id(), e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public final @NotNull ConfigRegistry<K> registry() {
        return registry;
    }

    @Override
    public final RegistryEntry.@NotNull Reference<ConfigValue<K, T, B>> entry() {
        return entry;
    }

    @Override
    public final @NotNull RegistryKey<ConfigValue<K, T, B>> key() {
        return entry.registryKey();
    }

    @Override
    public final @NotNull String translationKey() {
        return "config." + key().getRegistry().toString() + "/" + key().getValue().toString();
    }

    @Override
    public final @NotNull Identifier id() {
        return key().getValue();
    }

    @Override
    public final @NotNull ConfigMetadata metadata() {
        return metadata;
    }

    @Override
    public final @NotNull T nominalValue() {
        return nominalValue;
    }

    @Override
    public final @NotNull ConfigCodec<T, B> codec() {
        return codec;
    }

    @Override
    public final @NotNull ConfigTest<T> test() {
        return test;
    }

    @Override
    public final @NotNull T get(@NotNull K backend) {
        ConfigBackend<K> casted = ConfigBackend.cast(backend);
        return casted == null ? nominalValue : casted.get(this);
    }

    @Override
    public final boolean set(@NotNull K backend, @Nullable T value) {
        if (value != null && !test.test(value)) return false;
        T oldValue = get(backend);
        ConfigBackend.cast(backend).set(this, value);
        event.invoker().onConfigChange(backend, this, oldValue, value == null ? nominalValue : value);
        return true;
    }

    @Override
    public final void onChange(@NotNull ConfigChangeListener<K, T, B> listener) {
        event.register(listener);
    }

    public static abstract class Builder<K, T, B, C extends AbstractConfigValue<K, T, B>> {
        protected final T nominalValue;
        protected final ConfigCodec<T, B> codec;
        private final Map<Version, Identifier> ids = new HashMap<>();
        private Version lastVersion;
        private ConfigMetadata metadata;
        private Predicate<T> test;
        private Predicate<ConfigActor> read;
        private Predicate<ConfigActor> write;
        private List<ConfigChangeListener<K, T, B>> listeners;

        public Builder(
                Version version,
                Identifier id,
                T nominalValue,
                ConfigCodec<T, B> codec
        ) {
            this.nominalValue = nominalValue;
            this.codec = codec;

            this.id(version, id);
        }

        @Contract("_, _ -> this")
        public Builder<K, T, B, C> id(Version version, Identifier id) {
            if (version == null || id == null) {
                throw new IllegalArgumentException("Version and ID must not be null.");
            }
            if (lastVersion != null && version.compareTo(lastVersion) <= 0) {
                throw new IllegalArgumentException("Versions must be in ascending order.");
            }
            ids.put(version, id);
            lastVersion = version;
            return this;
        }

        @Contract("_, _ -> new")
        public Builder<K, T, B, C> id(String version, Identifier id) {
            try {
                return id(Version.parse(version), id);
            } catch (VersionParsingException e) {
                throw new IllegalArgumentException("Invalid version string: " + version, e);
            }
        }

        @Contract("_ -> this")
        public Builder<K, T, B, C> test(@NotNull Predicate<T> test) {
            if (this.test == null) {
                this.test = test;
            } else {
                this.test = this.test.and(test);
            }
            return this;
        }

        @Contract("_ -> this")
        public Builder<K, T, B, C> read(@NotNull Predicate<ConfigActor> read) {
            if (this.read == null) {
                this.read = read;
            } else {
                this.read = this.read.and(read);
            }
            return this;
        }

        @Contract("_ -> this")
        public Builder<K, T, B, C> write(@NotNull Predicate<ConfigActor> write) {
            if (this.write == null) {
                this.write = write;
            } else {
                this.write = this.write.and(write);
            }
            return this;
        }

        @Contract("_ -> this")
        public Builder<K, T, B, C> onChange(@NotNull ConfigChangeListener<K, T, B> listener) {
            if (listeners == null) listeners = new ArrayList<>();
            listeners.add(listener);
            return this;
        }

        @Contract(" -> new")
        protected ConfigMetadata metadata() {
            if (metadata == null) metadata = new ConfigMetadata(ids);
            return metadata;
        }

        @Contract(" -> new")
        protected ConfigTest<T> test() {
            return ConfigTest.of(test, read, write);
        }

        protected abstract C create();

        private void applyListeners(C configValue) {
            if (listeners != null) {
                for (ConfigChangeListener<K, T, B> listener : listeners) {
                    configValue.onChange(listener);
                }
            }
        }

        public final C build() {
            C configValue = create();
            applyListeners(configValue);
            return configValue;
        }
    }
}
