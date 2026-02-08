package com.koralix.oneforall.config.storage;

import com.koralix.oneforall.config.ConfigRegistry;
import com.koralix.oneforall.config.backend.ConfigBundle;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Function;

public class MultiplexedConfigStorage<K, T> implements ConfigStorage<K, T> {
    private final Function<K, ConfigStorage<K, T>> provider;

    public MultiplexedConfigStorage(@NotNull Function<K, ConfigStorage<K, T>> provider) {
        this.provider = provider;
    }

    @Override
    public void save(@NotNull K backend) throws IOException {
        provider.apply(backend).save(backend);
    }

    @Override
    public @NotNull ConfigBundle<K> load(@NotNull K backend, @NotNull ConfigRegistry<K> registry) throws IOException {
        return provider.apply(backend).load(backend, registry);
    }
}
